package com.yww.api.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.net.url.UrlQuery;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Data;

import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * 使用的请求工具为hutool
 * <dependency>
 * <groupId>cn.hutool</groupId>
 * <artifactId>hutool-all</artifactId>
 * <version>${hutool.version}</version>
 * </dependency>
 *
 * @author yww
 * @since 2023/11/26
 */
public class V1Demo {

    private static final Map<String, ApiInfo> API_INFO_MAP = new HashMap<>();
    private static final String APP_ID = "123";
    private static final String APP_SECRET = "123";
    private static final String URL = "http://127.0.0.1:9900/api/v1/action";

    public static ApiInfo getApiInfo() {
        ApiInfo apiInfo = API_INFO_MAP.get(APP_ID);
        if (apiInfo != null && (apiInfo.getDueTime().getTime() - 3000) > System.currentTimeMillis()) {
            return apiInfo;
        }

        Map<String, Object> bizContentMap = new HashMap<>();
        bizContentMap.put("appId", APP_ID);
        bizContentMap.put("appSecret", APP_SECRET);

        String apiNo = IdUtil.simpleUUID();
        String timestamp = DateUtil.format(DateUtil.date(), DatePattern.PURE_DATETIME_PATTERN);
        //JSON字符串
        String bizContent = JSONUtil.toJsonStr(bizContentMap);
        Map<String, Object> params = new HashMap<>(6);
        params.put("appId", APP_ID);
        params.put("apiNo", apiNo);
        params.put("method", "getApiToken");
        params.put("timestamp", timestamp);
        params.put("bizContent", URLUtil.encode(Base64.encode(bizContent)));

        HttpRequest request = HttpRequest.post(URL)
                .contentType("application/x-www-form-urlencoded")
                .form(params);
        System.out.println("请求报文 => " + UrlQuery.of(params));

        HttpResponse execute = request.execute();
        String body = execute.body();
        System.out.println("响应报文 => " + body);
        int status = execute.getStatus();
        if (status == 200) {
            JSONObject obj = JSONUtil.parseObj(body);
            Integer code = obj.getInt("code");
            if (code == 200) {
                JSONObject data = obj.getJSONObject("data");
                apiInfo = data.toBean(ApiInfo.class);
                apiInfo.setDueTime(new Date(System.currentTimeMillis() + (data.getInt("expireTime") * 1000)));
                API_INFO_MAP.put(APP_ID, apiInfo);
            }
        }
        return apiInfo;
    }

    public static Object doPost(String method, Map<String, Object> bizContentMap, String sessionKey) {
        return doPost(method, bizContentMap, sessionKey, null, null);
    }

    public static Object doPost(String method, Map<String, Object> bizContentMap, String sessionKey, File file, File[] fileList) {
        String apiNo = IdUtil.simpleUUID();
        String timestamp = DateUtil.format(DateUtil.date(), DatePattern.PURE_DATETIME_PATTERN);
        //JSON字符串
        String bizContent = bizContentMap == null ? "" : JSONUtil.toJsonStr(bizContentMap);
        Map<String, Object> params = new HashMap<>(6);
        params.put("appId", APP_ID);
        params.put("apiNo", apiNo);
        params.put("method", method);
        params.put("timestamp", timestamp);
        params.put("bizContent", URLUtil.encode(Base64.encode(bizContent)));
        params.put("sign", getSign(bizContent, method, timestamp, apiNo, sessionKey, file, fileList));

        params.put("file", file);
        params.put("fileList", fileList);

        // POST的Content-Type默认是application/x-www-form-urlencoded，有文件则是multipart/form-data
        HttpRequest request = HttpRequest.post(URL)
                .form(params);
        System.out.println("请求报文 => " + UrlQuery.of(params));

        HttpResponse execute = request.execute();
        String body = execute.body();
        System.out.println("响应报文 => " + body);
        // 失败 => {"apiNo":"09c1ad82ec0f4b2d80cae0cfb1d7059b","code":"103","msg":"调用方法异常","timestamp":1638176339560,"data":null,"success":false}
        // 成功 => {"apiNo":"bc070a7c31ac4b8eb1180b2d82a2096b","code":"200","msg":"成功","timestamp":1638176552353,"data":{"userId":"123","username":"admin@gxfy.com"},"success":true}
        int status = execute.getStatus();
        if (status == 200) {
            JSONObject obj = JSONUtil.parseObj(body);
            Integer code = obj.getInt("code");
            if (code == 200) {
                return obj.get("data");
            }
        }
        return null;
    }

    /**
     * 下载文件
     *
     * @param method
     * @param bizContentMap
     * @param sessionKey
     * @return
     */
    public static InputStream download(String method, Map<String, Object> bizContentMap, String sessionKey) {
        String apiNo = IdUtil.simpleUUID();
        String timestamp = DateUtil.format(DateUtil.date(), DatePattern.PURE_DATETIME_PATTERN);
        //JSON字符串
        String bizContent = bizContentMap == null ? "" : JSONUtil.toJsonStr(bizContentMap);
        Map<String, Object> params = new HashMap<>(6);
        params.put("appId", APP_ID);
        params.put("apiNo", apiNo);
        params.put("method", method);
        params.put("timestamp", timestamp);
        params.put("bizContent", URLUtil.encode(Base64.encode(bizContent)));
        params.put("sign", getSign(bizContent, method, timestamp, apiNo, sessionKey, null, null));

        // POST的Content-Type默认是application/x-www-form-urlencoded，有文件则是multipart/form-data
        HttpRequest request = HttpRequest.post(URL)
                .form(params);
        System.out.println("请求报文 => " + UrlQuery.of(params));

        HttpResponse execute = request.execute();

        if (!execute.isOk()) {
            String body = execute.body();
            System.out.println("响应报文 => " + body);
            return null;
        }

        return execute.bodyStream();
    }

    /**
     * 获取签名
     *
     * @param bizContent
     * @param method
     * @param timestamp
     * @param apiNo
     * @param sessionKey
     * @return
     */
    public static String getSign(String bizContent, String method, String timestamp, String apiNo, String sessionKey, File file, File[] fileList) {
        // 参与签名的参数排序
        TreeMap<String, String> signTreeMap = new TreeMap<>();
        signTreeMap.put("apiNo", apiNo);
        signTreeMap.put("timestamp", timestamp);
        signTreeMap.put("method", method);
        signTreeMap.put("bizContent", bizContent);

        if (file != null) {
            signTreeMap.put("fileMd5", SecureUtil.md5(file));
        }
        if (ArrayUtil.isNotEmpty(fileList)) {
            StringBuilder md5 = new StringBuilder();
            for (File f : fileList) {
                md5.append(SecureUtil.md5(f));
            }
            signTreeMap.put("fileMd5", md5.toString());
        }

        // 拼接签名参数
        StringBuilder src = new StringBuilder();
        for (Map.Entry<String, String> entry : signTreeMap.entrySet()) {
            src.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        src.append("key=").append(sessionKey);

        return SecureUtil.md5(src.toString());
    }

    public static void main(String[] args) {
        // 这两个值可以存起来，不需每次都获取
        ApiInfo apiInfo = getApiInfo();
        String sessionKey = apiInfo.getKey();
        System.out.println(sessionKey);
    }

    @Data
    public static class ApiInfo {

        private String userId;

        private String username;

        private String key;

        private String expireTime;

        private String unit;

        private Date dueTime;

    }

}

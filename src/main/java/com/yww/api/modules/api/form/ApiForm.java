package com.yww.api.modules.api.form;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.yww.api.exception.BusinessException;
import com.yww.api.modules.api.utils.ApiUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

/**
 *      api 基础form
 *
 * @author yww
 * @since 2023/11/26
 */
@Slf4j
@Data
public class ApiForm {

    private String appId;
    private String method;// 请求方法
    private String sign;// 签名
    private String timestamp;// 时间戳, 单位: 毫秒
    private String version;// 接口版本
    private String apiNo;// 接口码
    private String bizContent;// 请求业务参数
    private JSONObject bizContentJson;// 请求业务的json对象
    private MultipartFile file; // 上传文件用
    private MultipartFile[] fileList; // 上传文件用

    /**
     * 解析业务参数，将业务参数转为JSON字符串
     */
    public boolean parseBizContent() {
        try {
            // 业务参数不为空，便给参数进行BASE64解码
            if (StrUtil.isNotBlank(bizContent)) {
                bizContent = ApiUtils.decode(bizContent, "BASE64");
            }
            // 参数为空就设置为空
            if (bizContent == null) {
                bizContent = "";
            }
            // 参数转换为字符串
            bizContentJson = JSON.parseObject(bizContent);
            if (bizContentJson == null) {
                bizContentJson = new JSONObject();
            }
            return true;
        } catch (Exception e) {
            log.error("bizContent解析失败：{}", e.getMessage());
            return false;
        }
    }

    public <T> T toBean(Class<T> beanClass) {
        JSONObject contentJson = getContentJson();
        return contentJson.toJavaObject(beanClass);
    }

    /**
     * 获取参数
     */
    public String getString(String key) {
        String value = getContentJson().getString(key);
        return value == null ? "" : value;
    }

    /**
     * 获取业务参数的JSON字符串
     */
    public JSONObject getContentJson() {
        if (bizContentJson != null) {
            return bizContentJson;
        }
        parseBizContent();
        return bizContentJson;
    }

    /**
     * 加签，其实就是拼接加上Key
     */
    public String getSignStr(String key) {
        // 参数进行排序
        TreeMap<String, String> treeMap = getSignTreeMap();

        // 参数拼接
        StringBuilder src = new StringBuilder();
        for (Map.Entry<String, String> entry : treeMap.entrySet()) {
            src.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        src.append("key=").append(key);

        return src.toString();
    }

    /**
     * 获取签名所需参数，并进行字典序排序
     */
    public TreeMap<String, String> getSignTreeMap() {
        TreeMap<String, String> treeMap = new TreeMap<>();
        treeMap.put("apiNo", this.apiNo);
        treeMap.put("timestamp", this.timestamp);
        treeMap.put("method", this.method);
        String bizContent = StrUtil.isBlank(this.bizContent) ? "" : this.bizContent;
        treeMap.put("bizContent", bizContent);
        // 如果有文件传入，并设置文件的MD5
        if (file != null) {
            InputStream inputStream;
            try {
                inputStream = file.getInputStream();
            } catch (IOException e) {
                log.error("获取文件流发生错误", e);
                throw new BusinessException("获取文件流发生错误：" + e.getMessage());
            }
            treeMap.put("fileMd5", SecureUtil.md5(inputStream));
        }
        // 如果有文件列表，拼接所有文件的MD5
        if (ArrayUtil.isNotEmpty(fileList)) {
            StringBuilder md5 = new StringBuilder();
            for (MultipartFile multipartFile : fileList) {
                InputStream inputStream;
                try {
                    inputStream = multipartFile.getInputStream();
                } catch (IOException e) {
                    log.error("获取文件流发生错误", e);
                    throw new BusinessException("获取文件流发生错误：" + e.getMessage());
                }
                md5.append(SecureUtil.md5(inputStream));
            }
            treeMap.put("fileMd5", md5.toString());
        }
        return treeMap;
    }

}

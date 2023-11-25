package com.yww.api.modules.api.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.yww.api.constant.ApiCodeEnum;
import com.yww.api.modules.api.form.ApiForm;
import com.yww.api.modules.api.form.ApiResp;
import com.yww.api.modules.api.service.IApiLogic;
import com.yww.api.modules.api.service.impl.ApiV1Logic;
import com.yww.api.modules.api.service.impl.ApiV2Logic;
import com.yww.api.modules.system.vo.ResultVo;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yww
 * @since 2023/11/26
 */
@Component
public class ApiUtils {

    private static final Map<String, IApiLogic> MAP = new HashMap<>();

    public ApiUtils(ApiV1Logic apiV1Logic, ApiV2Logic apiV2Logic) {
        addApi("v1", apiV1Logic);
        addApi("v2", apiV2Logic);
    }

    public static void addApi(String version, IApiLogic apiLogic) {
        MAP.put(version, apiLogic);
    }

    public static IApiLogic getApiLogic(ApiForm form) {
        return MAP.get(form.getVersion());
    }

    /**
     * 获取成功响应
     */
    public static ApiResp success(ApiForm form) {
        return new ApiResp(form, ApiCodeEnum.SUCCESS);
    }

    /**
     * 获取失败的响应
     */
    public static ApiResp fail(ApiForm form, String errMsg) {
        ApiResp apiResp = new ApiResp(form, ApiCodeEnum.BUSINESS_ERROR);
        apiResp.setMsg(errMsg);
        return apiResp;
    }

    public static ApiResp of(ApiForm form, ApiCodeEnum apiCodeEnum) {
        return new ApiResp(form, apiCodeEnum);
    }

    public static ApiResp toApiResp(ApiForm form, ResultVo resultVo) {
        ApiResp apiResp = new ApiResp(form);
        if (resultVo.isSuccess()) {
            apiResp.setData(resultVo.getData() == null ? "" : resultVo.getData());
        } else {
            return apiResp.setCode(String.valueOf(resultVo.getErrCode())).setMsg(resultVo.getErrMsg());
        }
        return apiResp;
    }

    public static ApiResp toApiResp(ApiForm form, Object data) {
        ApiResp apiResp = new ApiResp(form);
        apiResp.setData(data);
        return apiResp;
    }

    /**
     * 解码
     */
    public static String decode(String params, String encryptType) {
        if (StrUtil.isBlank(params)) {
            return "";
        }
        // params = EncryptUtils.urlDecode(params, "UTF-8");
        if ("RSA".equals(encryptType)) {
            params = EncryptUtils.rsaDecodeByPrivateKey(params, RsaUtils.privateKey);
        } else {
            params = Base64.decodeStr(params);
        }
        return params;
    }

    /**
     * 编码
     * <p>
     * 2017年3月15日 下午1:49:09
     *
     * @param params
     * @return
     */
    public static String encode(String params, String encryptType) {
        if (StrUtil.isBlank(params)) {
            return "";
        }
        if ("RSA".equals(encryptType)) {
            params = EncryptUtils.rsaDecodeByPrivateKey(params, RsaUtils.publicKey);
        } else {
            params = EncryptUtils.base64Decode(params);
        }
        params = EncryptUtils.urlEncode(params, "UTF-8");
        return params;
    }

    /**
     * 获取验证sign
     * <p>
     * 2017年3月15日 下午3:14:27
     *
     * @param content
     * @return
     */
    public static String getSign(String content) {
        return SecureUtil.md5(content);
    }

}

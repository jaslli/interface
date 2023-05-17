package com.yww.api.modules.api.utils;

import com.yww.api.constant.ApiCodeEnum;
import com.yww.api.modules.api.vo.ApiForm;
import com.yww.api.modules.api.vo.ApiResp;

/**
 * <p>
 *
 * </p>
 *
 * @author yww
 * @since 2023/5/16
 */
public class ApiUtil {

    public static ApiResp of(ApiForm form, ApiCodeEnum apiCodeEnum) {
        return new ApiResp(form, apiCodeEnum);
    }

    public static ApiResp success(ApiForm form) {
        return new ApiResp(form, ApiCodeEnum.SUCCESS);
    }

    public static ApiResp fail(ApiForm form, String errMsg) {
        ApiResp apiResp = new ApiResp(form, ApiCodeEnum.BUSINESS_ERROR);
        apiResp.setMsg(errMsg);
        return apiResp;
    }

}

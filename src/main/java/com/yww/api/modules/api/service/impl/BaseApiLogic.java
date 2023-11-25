package com.yww.api.modules.api.service.impl;


import com.yww.api.modules.api.form.ApiForm;
import com.yww.api.modules.api.form.ApiResp;
import com.yww.api.modules.api.service.IApiLogic;
import com.yww.api.modules.api.utils.ApiUtils;

/**
 * API基础类
 * <p>
 *
 * @author yww
 * @since 2023/11/26
 */
public abstract class BaseApiLogic implements IApiLogic {

    @Override
    public ApiResp signValid(ApiForm form) {
        return ApiUtils.success(form);
    }

}

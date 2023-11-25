package com.yww.api.modules.api.service;


import com.yww.api.modules.api.form.ApiForm;
import com.yww.api.modules.api.form.ApiResp;

/**
 * API通用接口
 *
 * @author yww
 * @since 2023/11/26
 */
public interface IApiCommon {

    ApiResp signValid(ApiForm form);

}

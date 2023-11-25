package com.yww.api.modules.api.service;


import com.yww.api.modules.api.form.ApiForm;
import com.yww.api.modules.api.form.ApiResp;

/**
 * API接口类
 * 这里的注解没有用，只是为了注释说明方法
 *
 * @author yww
 * @since 2023/11/26
 */
public interface IApiLogic extends IApiCommon {

    /**
     * 获取第三方调用token
     */
    ApiResp getApiToken(ApiForm form);

}

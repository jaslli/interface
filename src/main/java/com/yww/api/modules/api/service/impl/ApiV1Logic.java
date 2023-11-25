package com.yww.api.modules.api.service.impl;

import com.yww.api.aspect.OpenApi;
import com.yww.api.modules.api.form.ApiForm;
import com.yww.api.modules.api.form.ApiResp;
import com.yww.api.modules.api.service.IApiLogic;
import com.yww.api.modules.api.utils.ApiUtils;
import com.yww.api.modules.system.service.ApiUserInfoService;
import com.yww.api.utils.AssertUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * API实现类
 *
 * @author yww
 * @since 2023/11/26
 */
@Slf4j
@Component
public class ApiV1Logic extends BaseApiLogic implements IApiLogic {

    @Autowired
    ApiUserInfoService apiUserInfoService;

    @Override
    @OpenApi("获取Token")
    public ApiResp getApiToken(ApiForm form) {
        String appId = form.getString("appId");
        String appSecret = form.getString("appSecret");
        AssertUtils.hasText(appId, "缺少参数appId");
        AssertUtils.hasText(appSecret, "缺少参数secret");

        return ApiUtils.toApiResp(form, apiUserInfoService.getApiToken(appId, appSecret));
    }


}

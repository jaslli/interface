package com.yww.api.modules.api.service;


import com.yww.api.modules.api.vo.ApiForm;
import com.yww.api.modules.api.vo.ApiResp;

/**
 * API通用接口
 *
 * @author wilmiam
 * @since 2022-11-04 11:35
 */
public interface IApiCommon {

    ApiResp test(ApiForm apiForm);

}

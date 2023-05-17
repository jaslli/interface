package com.yww.api.modules.api.service.v1;

import com.yww.api.modules.api.service.IApiCommon;
import com.yww.api.modules.api.vo.ApiForm;
import com.yww.api.modules.api.vo.ApiResp;

/**
 * <p>
 *      API接口类
 * </p>
 *
 * @author yww
 * @since 2023/5/16
 */
public interface IApiV1Logic extends IApiCommon {

    ApiResp testv1(ApiForm apiForm);

}

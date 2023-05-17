package com.yww.api.modules.api.service.v2;

import com.yww.api.modules.api.service.IApiCommon;
import com.yww.api.modules.api.vo.ApiForm;
import com.yww.api.modules.api.vo.ApiResp;

/**
 * <p>
 *
 * </p>
 *
 * @author yww
 * @since 2023/5/17
 */
public interface IApiV2Logic extends IApiCommon {

    ApiResp testv1(ApiForm apiForm);

}

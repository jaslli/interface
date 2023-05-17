package com.yww.api.modules.api.service;

import com.yww.api.modules.api.vo.ApiForm;
import com.yww.api.modules.api.vo.ApiResp;

/**
 * <p>
 *      API服务
 * </p>
 *
 * @author yww
 * @since 2023/5/16
 */
public interface ApiService {

    ApiResp action(ApiForm form, String version);

}

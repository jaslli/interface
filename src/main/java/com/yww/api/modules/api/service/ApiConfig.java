package com.yww.api.modules.api.service;

import com.yww.api.constant.ApiVersionEnum;
import com.yww.api.modules.api.service.v1.ApiV1Logic;
import com.yww.api.modules.api.service.v2.ApiV2Logic;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author yww
 * @since 2023/5/17
 */
public class ApiConfig {

    /**
     * 允许用户未登录状态下执行的方法名
     */
    protected static final List<String> ALLOW_METHOD = Arrays.asList("getApiToken");

    /**
     * 方法列表
     */
    protected static final Map<String, Class<?>> CLASS_MAP = new HashMap<>();

    /**
     *  获取API接口类中的所有接口
     */
    @PostConstruct
    protected static void init() {
        CLASS_MAP.put(ApiVersionEnum.v1.name(), ApiV1Logic.class);
        CLASS_MAP.put(ApiVersionEnum.v2.name(), ApiV2Logic.class);
    }

}

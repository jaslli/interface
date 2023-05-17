package com.yww.api.modules.api.controller;

import com.yww.api.constant.ApiVersionEnum;
import com.yww.api.modules.api.service.ApiService;
import com.yww.api.modules.api.vo.ApiForm;
import com.yww.api.modules.api.vo.ApiResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *      API接口
 * </p>
 *
 * @author yww
 * @since 2023/5/16
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ApiController {

    private final ApiService apiService;

    @PostMapping(value = "/v1/action")
    public ApiResp action(ApiForm form) {
        return apiService.action(form, ApiVersionEnum.v1.name());
    }

}

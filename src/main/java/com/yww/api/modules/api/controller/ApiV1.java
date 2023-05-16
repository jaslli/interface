package com.yww.api.modules.api.controller;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.yww.api.constant.ApiCodeEnum;
import com.yww.api.constant.ApiVersionEnum;
import com.yww.api.exception.BusinessException;
import com.yww.api.modules.api.service.ApiService;
import com.yww.api.modules.api.utils.ApiUtil;
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
@RequestMapping("/api/v1")
public class ApiV1 {

    private final ApiService apiService;

    @PostMapping(value = "/action")
    public ApiResp action(ApiForm form) {
        form.setVersion(ApiVersionEnum.v1.name());

        //  解析参数不成功，则直接返回
        if (!form.parseBizContent()) {
            return ApiUtil.of(form, ApiCodeEnum.PARAM_ERROR);
        }

        ApiResp resp;
        try {
            //  调用接口方法
            resp = null;
//            resp = apiService.action(form);
        } catch (BusinessException e) {
            // 接口调用出现异常，返回错误信息
            log.error("接口调用异常！", e);
            resp = ApiUtil.fail(form, e.getMessage());
        } catch (Exception e) {
            // 转化指定异常为来自或者包含指定异常
            BusinessException businessException = ExceptionUtil.convertFromOrSuppressedThrowable(e, BusinessException.class, true);

            if (businessException != null) {
                // 如果是业务异常，则返回对应的业务错误
                resp = ApiUtil.fail(form, businessException.getMessage());
            } else {
                // 不是业务异常,表示是接口系统异常，打印错误日志
                log.error("接口调用方法发生错误", e);
                resp = ApiUtil.of(form, ApiCodeEnum.METHOD_HANDLER_ERROR);
            }
        }

        // 如果输出为空，则表示出现问题
        if (resp == null) {
            resp = ApiUtil.of(form, ApiCodeEnum.UNKNOWN_ERROR);
        }

        return resp;
    }

}

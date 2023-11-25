package com.yww.api.modules.api.controller;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.yww.api.constant.ApiCodeEnum;
import com.yww.api.constant.ApiVersionEnum;
import com.yww.api.exception.BusinessException;
import com.yww.api.modules.api.form.ApiForm;
import com.yww.api.modules.api.form.ApiResp;
import com.yww.api.modules.api.service.ApiManage;
import com.yww.api.modules.api.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

/**
 *      API方法总入口
 *
 * @author yww
 * @since 2023/11/26
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class ApiV1 {

    private final ApiManage apiManage;

    @PostMapping(value = "/action")
    public ResponseEntity<ApiResp> action(ApiForm form, HttpServletResponse response) {
        // 设置版本号
        form.setVersion(ApiVersionEnum.v1.name());

        //解析业务参数
        if (!form.parseBizContent()) {
            ApiResp apiResp = ApiUtils.of(form, ApiCodeEnum.PARAM_ERROR);
            return ResponseEntity.badRequest().body(apiResp);
        }

        // 调用接口方法
        ApiResp resp;
        try {
            // 调用接口方法
            resp = apiManage.action(form);
        } catch (BusinessException e) {
            // 捕抓业务发生的异常
            log.error("v1调用方法业务发生错误", e);
            resp = ApiUtils.fail(form, e.getMessage());
        } catch (Exception e) {
            // 转化指定异常为来自或者包含指定异常
            BusinessException businessException = ExceptionUtil.convertFromOrSuppressedThrowable(e, BusinessException.class, true);
            if (businessException == null) {
                // 不是业务异常才打印错误日志
                log.error("v1调用方法发生错误", e);
            }
            if (businessException != null) {
                resp = ApiUtils.fail(form, businessException.getMessage());
            } else {
                resp = ApiUtils.of(form, ApiCodeEnum.METHOD_HANDLER_ERROR);
            }
        }

        // 没有数据输出空
        if (resp == null) {
            resp = ApiUtils.of(form, ApiCodeEnum.UNKNOWN_ERROR);
        }

        // 如果接口返回数据为输入流，则返回输入
        Object data = resp.getData();
        if (data instanceof InputStream) {
            InputStream inputStream = (InputStream) data;
            ServletUtil.write(response, inputStream);
            return null;
        }

        return resp.isSuccess() ? ResponseEntity.ok(resp) : ResponseEntity.badRequest().body(resp);
    }

}

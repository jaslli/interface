package com.yww.api.modules.api.service;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.yww.api.constant.ApiCodeEnum;
import com.yww.api.exception.BusinessException;
import com.yww.api.modules.api.utils.ApiUtil;
import com.yww.api.modules.api.vo.ApiForm;
import com.yww.api.modules.api.vo.ApiResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;

/**
 * <p>
 *
 * </p>
 *
 * @author yww
 * @since 2023/5/17
 */
@Slf4j
@Service
public class ApiServiceImpl extends ApiConfig implements ApiService {
    @Override
    public ApiResp action(ApiForm form, String version) {
        // 设置版本
        form.setVersion(version);

        //  解析参数不成功，则直接返回
        if (!form.parseBizContent()) {
            return ApiUtil.of(form, ApiCodeEnum.PARAM_ERROR);
        }

        ApiResp resp;
        try {
            //  调用接口方法
            resp = action(form);
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

    private ApiResp action(ApiForm form) throws Exception {

        ApiResp authResp = checkSign(form);
        if (!authResp.isSuccess()) {
            return authResp;
        }

        ApiResp apiResp;
        try {
            // 获取目标类的Class对象
            Class<?> apiClass = CLASS_MAP.get(form.getVersion());

            // 获取目标方法
            Method method = apiClass.getDeclaredMethod("test2", ApiForm.class);

            // 创建目标类的实例
            Object instance = apiClass.newInstance();

            // 设置方法的可访问性
            method.setAccessible(true);

            // 执行方法
            Object result = method.invoke(instance, form);

            // 类型转换
            apiResp = Convert.convert(ApiResp.class, result);

        } catch (NoSuchMethodException e) {
            log.error("{} 方法不存在，调用失败", form.getMethod());
            apiResp = ApiUtil.of(form, ApiCodeEnum.METHOD_ERROR);
        }

        return apiResp;
    }

    private ApiResp checkSign(ApiForm apiForm) {
        // 如果方法名在白名单上，则直接执行
        if (ALLOW_METHOD.contains(apiForm.getMethod())) {
            return ApiUtil.success(apiForm);
        }

        // 验证签名逻辑
//        ApiTokenVo tokenVo = TokenUtils.getUserContext();
//        // 验证签名
//        String sign = ApiUtils.getSign(form.getSignStr(tokenVo.getSessionKey() == null ? "" : tokenVo.getSessionKey()));
//        if (!sign.equals(form.getSign())) {
//            return ApiUtils.of(form, ApiCodeEnum.CHECK_SIGN_VALID_ERROR);
//        }

        return ApiUtil.success(apiForm);
    }

}

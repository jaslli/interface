package com.yww.api.modules.api.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.yww.api.constant.ApiCodeEnum;
import com.yww.api.modules.api.form.ApiForm;
import com.yww.api.modules.api.form.ApiResp;
import com.yww.api.modules.api.utils.ApiUtils;
import com.yww.api.modules.api.utils.ReflectionUtils;
import com.yww.api.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author yww
 * @since 2023/11/26
 */
@Slf4j
@Service
public class ApiManage {

    /**
     * 允许用户未登录状态下执行的方法名
     */
    private static final List<String> ALLOW_METHOD = Arrays.asList("getApiToken");

    private static final List<String> METHOD_LIST;

    static {
        METHOD_LIST = methodList();
    }

    public static List<String> methodList() {
        List<String> methodList = new ArrayList<>();
        Method[] methods = IApiLogic.class.getMethods();
        for (Method method : methods) {
            Class<?>[] params = method.getParameterTypes();
            if (params.length == 1 && (params[0] == ApiForm.class)) {
                methodList.add(method.getName());
            }
        }
        return methodList;
    }

    public static boolean isNotValid(String method) {
        return ALLOW_METHOD.contains(method);
    }

    public ApiResp action(ApiForm form) throws Exception {
        // 如果接口方法不存在，则返回异常
        if (!METHOD_LIST.contains(form.getMethod())) {
            return ApiUtils.of(form, ApiCodeEnum.METHOD_ERROR);
        }
        // 校验签名，校验失败直接返回
        ApiResp authResp = signValid(form);
        if (!authResp.isSuccess()) {
            return authResp;
        }

        IApiLogic apiLogic = ApiUtils.getApiLogic(form);

        // 调用接口方法，利用反射更简洁
        Object result = ReflectionUtils.invokeMethod(apiLogic, form.getMethod(), new Class<?>[]{ApiForm.class}, new Object[]{form});
        return (ApiResp) result;
    }

    /**
     * 签名验证
     */
    public ApiResp signValid(ApiForm form) {
        // 白名单存在该方法，直接放行
        boolean contains = ALLOW_METHOD.contains(form.getMethod());
        if (contains) {
            return ApiUtils.success(form);
        }
        // 获取Token中的sessionKey
        DecodedJWT apiToken = TokenUtil.getUserContext();
        String sessionKey = TokenUtil.getSessionkey(apiToken);
        // 通过sessionKey签名，并进行校验
        String sign = ApiUtils.getSign(form.getSignStr(sessionKey == null ? "" : sessionKey));
        if (!sign.equals(form.getSign())) {
            return ApiUtils.of(form, ApiCodeEnum.CHECK_SIGN_VALID_ERROR);
        }

        return ApiUtils.success(form);
    }

}

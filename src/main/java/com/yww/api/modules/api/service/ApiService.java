package com.yww.api.modules.api.service;

import com.yww.api.modules.api.vo.ApiForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 *      API服务
 * </p>
 *
 * @author yww
 * @since 2023/5/16
 */
@Slf4j
@Service
public class ApiService {

    /**
     * 允许用户未登录状态下执行的方法名
     */
    private static final List<String> ALLOW_METHOD = Arrays.asList("getApiToken");

    /**
     * 方法列表
     */
    private static final List<String> METHOD_LIST;

    static {
        METHOD_LIST = methodList();
    }

    /**
     *  获取API接口类中的所有接口
     */
    private static List<String> methodList() {
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

//    public ApiResp action(ApiForm form) throws Exception {
//        if (!METHOD_LIST.contains(form.getMethod())) {
//            return ApiUtil.of(form, ApiCodeEnum.METHOD_ERROR);
//        }
//
//        ApiResp authResp = signValid(form);
//        if (!authResp.isSuccess()) {
//            return authResp;
//        }
//
//        IApiLogic apiLogic = ApiUtils.getApiLogic(form);
//
//        // 调用接口方法，利用反射更简洁
//        return (ApiResp) ReflectionUtils.invokeMethod(apiLogic, form.getMethod(), new Class<?>[]{ApiForm.class}, new Object[]{form});
//    }

}

package com.yww.api.interceptor;

import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.yww.api.constant.ApiCodeEnum;
import com.yww.api.modules.api.form.ApiForm;
import com.yww.api.modules.api.service.ApiManage;
import com.yww.api.modules.api.utils.ApiUtils;
import com.yww.api.utils.IpUtil;
import com.yww.api.utils.RedisUtil;
import com.yww.api.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *      方法拦截器
 * 主要是校验Token信息，校验成功线后程保存Token信息
 *
 * @author yww
 * @since 2023/11/26
 */
@Slf4j
@Component
public class ApiMethodInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 当前环境
     */
    @Value("${spring.profiles.active:product}")
    private String active;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String ip = IpUtil.getIpAddr(request);

        ApiForm form = ServletUtil.toBean(request, ApiForm.class, true);

        log.info("from [{}] to [{}]", ip, request.getRequestURI() + "#" + form.getMethod());

        // 判断是否是可以在未登录状态下执行的方法名
        boolean notValid = ApiManage.isNotValid(form.getMethod());
        if (notValid) {
            return true;
        }

        // 验证认证信息
        String apiToken = redisUtil.getStr(TokenUtil.getApiTokenKey(form.getAppId()));
        if (apiToken == null) {
            response.setStatus(HttpStatus.HTTP_BAD_REQUEST);
            ServletUtil.write(response, JSONUtil.toJsonStr(ApiUtils.of(form, ApiCodeEnum.LOGIN_VALID_ERROR)), "application/json;charset=utf-8");
            return false;
        }
        DecodedJWT decodedJwt;
        try {
            decodedJwt = TokenUtil.parse(apiToken);
        } catch (Exception e) {
            response.setStatus(HttpStatus.HTTP_BAD_REQUEST);
            ServletUtil.write(response, JSONUtil.toJsonStr(ApiUtils.of(form, ApiCodeEnum.LOGIN_VALID_ERROR)), "application/json;charset=utf-8");
            return false;
        }
        TokenUtil.setUserContext(decodedJwt);

        return true;
    }

}

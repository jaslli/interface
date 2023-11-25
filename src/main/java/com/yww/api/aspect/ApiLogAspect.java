package com.yww.api.aspect;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yww.api.exception.BusinessException;
import com.yww.api.modules.api.form.ApiForm;
import com.yww.api.modules.system.dao.ApiUserInfoDao;
import com.yww.api.modules.system.dao.OpenApiLogDao;
import com.yww.api.modules.system.entity.ApiUserInfo;
import com.yww.api.modules.system.entity.OpenApiLog;
import com.yww.api.utils.IpUtil;
import com.yww.api.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * <p>
 *      用户操作日志处理切面
 * </p>
 *
 * @author yww
 * @since 2023/11/26
 */
@Order(1)
@Aspect
@Slf4j
@Component
public class ApiLogAspect {

    private final OpenApiLogDao openApiLogDao;
    private final ApiUserInfoDao apiUserInfoDao;

    @Autowired
    public ApiLogAspect(OpenApiLogDao openApiLogDao, ApiUserInfoDao apiUserInfoDao) {
        this.openApiLogDao = openApiLogDao;
        this.apiUserInfoDao = apiUserInfoDao;
    }

    ThreadLocal<Long> currentTime = new ThreadLocal<>();

    /**
     * 声明切入点
     */
    @Pointcut("@annotation(com.yww.api.aspect.OpenApi)")
    public void pointCut() {}

    /**
     * 环绕通知
     */
    @Around("pointCut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        currentTime.set(System.currentTimeMillis());
        // 执行目标方法
        Object result = joinPoint.proceed();
        // 计算执行耗时
        long time = System.currentTimeMillis() - currentTime.get();
        currentTime.remove();

        // 获取方法名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method signatureMethod = signature.getMethod();
        // 获取注解信息
        OpenApi openApi = AnnotationUtils.getAnnotation(signatureMethod, OpenApi.class);
        // 获取当前请求对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = null;
        if (attributes != null) {
            request = attributes.getRequest();
        }

        // 保存日志
        insertLog(openApi, request, "INFO", "", time);

        return result;
    }

    /**
     * 配置异常通知
     *
     * @param joinPoint join point for advice
     * @param e         exception
     */
    @AfterThrowing(pointcut = "pointCut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        // 计算执行耗时
        long time = System.currentTimeMillis() - currentTime.get();
        currentTime.remove();

        String errMsg = "";
        if (e instanceof BusinessException) {
            errMsg = e.getMessage();
        }

        // 获取方法名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method signatureMethod = signature.getMethod();
        // 获取注解信息
        OpenApi openApi = AnnotationUtils.getAnnotation(signatureMethod, OpenApi.class);

        // 获取当前请求对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = null;
        if (attributes != null) {
            request = attributes.getRequest();
        }
        // 保存日志
        insertLog(openApi, request, "ERROR", errMsg, time);
    }

    @Async
    public void insertLog(OpenApi openApi, HttpServletRequest request, String logType, String errMsg, long timeCost) {
        try {
            // 获取IP信息
            String clientIp = IpUtil.getIpAddr(request);
            // 获取请求对象
            ApiForm apiForm = ServletUtil.toBean(request, ApiForm.class, true);

            OpenApiLog openApiLog = new OpenApiLog();
            openApiLog.setLogType(logType);
            DecodedJWT decodedJWT = TokenUtil.getUserContext();
            if (decodedJWT != null) {
                openApiLog.setAppId(TokenUtil.getAppid(decodedJWT));
                openApiLog.setApplyer(TokenUtil.getApplyer(decodedJWT));
            } else {
                ApiUserInfo apiUserInfo = apiUserInfoDao.selectOne(
                        Wrappers.lambdaQuery(ApiUserInfo.builder().appId(apiForm.getAppId()).build())
                );
                if (apiUserInfo == null) {
                    openApiLog.setLogType("ERROR");
                } else {
                    openApiLog.setAppId(apiUserInfo.getAppId());
                    openApiLog.setApplyer(apiUserInfo.getApplyer());
                }
            }
            openApiLog.setVersion(getVersion(request));
            openApiLog.setMethod(apiForm.getMethod());
            openApiLog.setClientIp(clientIp);
            openApiLog.setDescription(openApi == null ? "" : openApi.value());
            openApiLog.setServerIp(NetUtil.getLocalhostStr());
            openApiLog.setErrMsg(errMsg);
            openApiLog.setTimeCost(timeCost);
            openApiLog.setCreateTime(DateUtil.now());

            // 保存日志
            openApiLogDao.insert(openApiLog);
        } catch (Exception e) {
            log.error("记录日志发生错误", e);
        }
    }

    /**
     * 获取版本号
     */
    public String getVersion(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        if (StrUtil.isBlank(requestUri)) {
            log.warn("为获取到版本号，URI [{}]", request);
            return "";
        }

        return requestUri.replace("/api/", "").replace("/action", "");
    }

}

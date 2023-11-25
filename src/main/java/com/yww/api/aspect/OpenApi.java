package com.yww.api.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 *      用于标记记录用户操作日志的方法
 * </P>
 *
 * @author yww
 * @since 2023/11/26
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OpenApi {

    String value() default "";

}
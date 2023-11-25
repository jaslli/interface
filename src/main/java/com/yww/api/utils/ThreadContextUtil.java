package com.yww.api.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 基于ThreadLocal的线程相关上下文帮助类, 用于在同一线程下传递变量.
 * </p>
 *
 * @author yww
 * @since 2023/11/26
 */
public class ThreadContextUtil {

    private static ThreadLocal<Map<String, Object>> threadLocalMap = ThreadLocal.withInitial(HashMap::new);

    /**
     * 根据指定的key获取当前线程相关的变量值
     *
     * @param key 变量的key
     * @param <T> 变量值的具体类型
     * @return 若无此key对应的变量值, 则返回{@code null}
     * @throws ClassCastException 若接收此返回值的变量类型与上下文保存的值的实际类型不匹配, 则抛出异常
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(String key) {
        return (T) threadLocalMap.get().get(key);
    }

    /**
     * 根据指定的key获取当前线程相关的变量值, 若为{@code null}则返回指定的默认值
     *
     * @param key          变量的key
     * @param defaultValue 默认值
     * @param <T>          变量值的具体类型
     * @return 若无此key对应的变量值, 则返回defaultValue
     * @throws ClassCastException 若接收此返回值的变量类型与上下文保存的值的实际类型不匹配, 则抛出异常
     */
    public static <T> T get(String key, T defaultValue) {
        T value = get(key);
        return value == null ? defaultValue : value;
    }

    /**
     * 设置线程相关上下文的变量值
     *
     * @param key   变量的key
     * @param value 变量值
     */
    public static void set(String key, Object value) {
        threadLocalMap.get().put(key, value);
    }

    /**
     * 删除指定key的变量
     *
     * @param key 变量的key
     */
    public static void remove(String key) {
        threadLocalMap.get().remove(key);
    }

    /**
     * 清除当前线程相关的上下文
     */
    public static void close() {
        threadLocalMap.remove();
    }

    /**
     * Don't let anyone instantiate this class
     */
    private void ThreadContext() {
    }

}

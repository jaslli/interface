package com.yww.api.utils;

import java.util.UUID;

/**
 * Utility class that handles the uuid stuff.
 *
 * @author yww
 * @since 2023/11/26
 */
public class UuidUtils {

    /**
     * Don't let anyone instantiate this class
     */
    private UuidUtils() {
    }

    /**
     * 返回一个随机的带有分隔符"-"的36位UUID字符串
     */
    public static String uuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * 返回一个随机的没有分隔符"-"的32位UUID字符串
     */
    public static String uuidNoDash() {
        return uuid().replaceAll("-", "");
    }


}


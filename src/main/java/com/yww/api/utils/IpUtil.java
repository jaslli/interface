package com.yww.api.utils;

import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * IP相关工具类
 * </p>
 *
 * @author yww
 * @since 2023/11/26
 */
public class IpUtil {

    public static long getLongIpAddr(HttpServletRequest request) {
        String ip = getIpAddr(request);
        return ipv4ToLong(ip);
    }

    /**
     * 获取客户端IP
     *
     * @return IP
     */
    public static String getIpAddr() {
        return getIpAddr(getRequest());
    }

    /**
     * 获取客户端IP
     *
     * @param request 请求
     * @return IP
     */
    public static String getIpAddr(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        // 判断x-forwarded-for是否有IP
        String ip = request.getHeader("x-forwarded-for");
        if (isNotUnknown(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        // 判断Proxy-Client-IP是否有IP
        if (isNotUnknown(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        // 判断X-Forwarded-For是否有IP
        if (isNotUnknown(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        // 判断WL-Proxy-Client-IP是否有IP
        if (isNotUnknown(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        // 判断X-Real-IP是否有IP
        if (isNotUnknown(ip)) {
            ip = request.getRemoteAddr();
        }
        // 若是IP最后为0:0:0:0:0:0:0:1，就是本地回环地址，否则第一个非unknown的IP地址就是真实地址
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : getMultistageReverseProxyIp(ip);
    }

    /**
     * 从多级反向代理中获得第一个非unknown IP地址
     *
     * @param ip 获得的IP地址
     * @return 第一个非unknown IP地址
     */
    @SuppressWarnings("all")
    private static String getMultistageReverseProxyIp(String ip) {
        // 多级反向代理检测
        if (ip != null && ip.indexOf(",") > 0) {
            final String[] ips = ip.trim().split(",");
            for (String subIp : ips) {
                if (isNotUnknown(subIp)) {
                    ip = subIp;
                    break;
                }
            }
        }
        return ip;
    }

    /**
     * 检测给定字符串是否为未知，多用于检测HTTP请求相关
     *
     * @param checkString 被检测的字符串
     * @return 是否未知
     */
    private static boolean isNotUnknown(String checkString) {
        return StrUtil.isNotBlank(checkString) && StrUtil.isNotEmpty(checkString) && !"unknown".equalsIgnoreCase(checkString);
    }

    /**
     * 获取UserAgent信息对象
     *
     * @param request 请求
     * @return UserAgent
     */
    public static UserAgent getBrowser(HttpServletRequest request) {
        return UserAgentUtil.parse(request.getHeader("User-Agent"));
    }


    /**
     * 获取当前请求
     *
     * @return 当前请求
     */
    private static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    }

    /**
     * 将ipv4地址转换为long类型
     * 等同于MySQL的inet_aton方法
     *
     * @param ipStr ipv4地址
     * @return long类型的ipv地址
     */
    public static long ipv4ToLong(String ipStr) {
        String regex = "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(ipStr);
        if (matcher.matches()) {
            long addr = 0;
            for (int i = 1; i <= 4; i++) {
                addr |= Long.parseLong(matcher.group(i)) << 8 * (4 - i);
            }
            return addr;
        } else {
            throw new RuntimeException("ipv4地址格式出错！");
        }
    }

    /**
     * 将long类型的ip转为ipv4字符串
     * 等同于MySQL的inet_ntoa方法
     *
     * @param longIp long类型IP
     * @return IPV4字符串
     */
    public static String longToIpv4(long longIp) {
        StringBuilder sb = new StringBuilder();
        // 直接右移24位
        sb.append(longIp >> 24 & 0xFF);
        sb.append(CharUtil.DOT);
        // 将高8位置0，然后右移16位
        sb.append(longIp >> 16 & 0xFF);
        sb.append(CharUtil.DOT);
        sb.append(longIp >> 8 & 0xFF);
        sb.append(CharUtil.DOT);
        sb.append(longIp & 0xFF);
        return sb.toString();
    }

}

package com.yww.api.modules.api.utils;

import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ServerErrorException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * 加密工具类
 *
 * @author yww
 * @since 2023/11/26
 */
public class EncryptUtils {

    private static final Logger log = LoggerFactory.getLogger(EncryptUtils.class);

    /**
     * 字符编码
     */
    private static final String CHARSET_NAME = "UTF-8";

    /**
     * 用来将字节转换成 16 进制表示的字符
     */
    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static final int HEX_RADIUS = 16;

    /**
     * MD5 加密字符串
     */
    public static String md5Encrypt(final String sourceStr) {
        return md5Encrypt(sourceStr, CHARSET_NAME);
    }

    /**
     * MD5加密字符串
     */
    public static String md5Encrypt(final String sourceStr, String coding) {
        if (StrUtil.isBlank(sourceStr)) {
            return null;
        }

        byte[] sourceByte;
        try {
            sourceByte = sourceStr.getBytes(coding);
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
            sourceByte = sourceStr.getBytes();
        }

        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(sourceByte);
            // MD5 的计算结果是一个 128 位的长整数，用字节表示就是 16 个字节
            final byte[] tmp = md.digest();

            // 每个字节用 16 进制表示的话，使用两个字符，所以表示成 16 进制需要 32 个字符
            // 16 << 1 相当于 16*2
            final char[] str = new char[16 << 1];

            // 表示转换结果中对应的字符位置
            int k = 0;

            // 从第一个字节开始，对 MD5 的每一个字节转换成 16 进制字符的转换
            for (int i = 0; i < HEX_RADIUS; i++) {
                // 取第 i 个字节
                final byte byte0 = tmp[i];

                // 取字节中高 4 位的数字转换, >>> 为逻辑右移，将符号位一起右移
                str[k++] = HEX_DIGITS[byte0 >>> 4 & 0xf];

                // 取字节中低 4 位的数字转换
                str[k++] = HEX_DIGITS[byte0 & 0xf];
            }
            // 换后的结果转换为字符串
            return new String(str);
        } catch (final NoSuchAlgorithmException e) {
            log.error(">> MD5加密错误", e);
        }

        return null;
    }

    /**
     * 私钥解密(注意：encodedData是base64加密后的才行)
     */
    public static String rsaDecodeByPrivateKey(String encodedData, String privateKey) {
        try {
            byte[] decodedData = RsaUtils.decryptByPrivateKey(Base64.getDecoder().decode(encodedData), privateKey);
            return new String(decodedData, CHARSET_NAME);
        } catch (Exception e) {
            log.error("rsaDecodeByPrivateKey error", e);
            return null;
        }
    }

    /**
     * 使用BASE64进行解密
     *
     * @param base64Str base64字符串
     * @return 解密字符串
     */
    public static String base64Decode(final String base64Str) {
        byte[] decode = Base64.getDecoder().decode(base64Str);
        try {
            return new String(decode, CHARSET_NAME);
        } catch (UnsupportedEncodingException e) {
            log.error("base64 decode error", e);
            throw new ServerErrorException("编码失败");
        }
    }

    /**
     * 对字符串进行URL编码
     *
     * @param sourceStr 需要编码的字符串
     * @param enc       编码格式
     * @return
     */
    public static String urlEncode(String sourceStr, String enc) {
        try {
            return URLEncoder.encode(sourceStr, enc);
        } catch (UnsupportedEncodingException e) {
            log.error("urlEncode error", e);
            return null;
        }
    }

}

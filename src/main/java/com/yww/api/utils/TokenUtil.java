package com.yww.api.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.yww.api.constant.TokenConstant;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Token工具类
 * 1. Header,记录令牌类型和签名算法
 * 2. payload,携带用户信息
 * (1) iss(issuer), 签发者
 * (2) sub(subject), 面向的主体
 * (3) aud(audience), 接收方
 * (4) nbf(notBefore), 开始生效生效时间戳
 * (5) exp(expiresAt), 过期时间戳
 * (6) iat(issuedAt ), 签发时间
 * (7) jti(jwtId), 唯一标识
 * 3. signature, 签名，防止Token被篡改
 * </p>
 *
 * @author yww
 * @since 2023/11/26
 */
public class TokenUtil {

    public static final String APPID = "appId";
    public static final String SESSIONKEY = "sessionKey";
    public static final String APPLYER = "applyer";

    /**
     * 过期时长
     */
    public static final long API_TOKEN_EXPIRE_MINUTES = 120L;

    /**
     * 线程变量的键值
     */
    private static final String APP_TOKEN_CONTEXT_KEY = "app-token";

    /**
     * 生成Token
     * 当前使用HMAC512的加密算法
     *
     * @return Token
     */
    public static String createToken(String appId, String sessionKey, String applyer) {
        // 设置Token头部（不设置也会默认有这两个值）
        Map<String, Object> header = new HashMap<String, Object>(2) {
            private static final long serialVersionUID = 1L;

            {
                put("alg", TokenConstant.TOKEN_ALG);
                put("typ", TokenConstant.TOKEN_TYP);
            }
        };
        // 设置负载
        Map<String, Object> payload = new HashMap<String, Object>(1) {
            private static final long serialVersionUID = 1L;

            {
                put(APPID, appId);
                put(SESSIONKEY, sessionKey);
                put(APPLYER, applyer);
            }
        };
        // 过期时间三小时
        long now = DateUtil.current();
        long exp = now + 1000 * API_TOKEN_EXPIRE_MINUTES * 60;
        return JWT.create()
                // 设置header
                .withHeader(header)
                // 设置payload
                .withIssuer(TokenConstant.TOKEN_ISSUER)
                .withSubject(TokenConstant.TOKEN_SUBJECT)
                .withAudience(TokenConstant.TOKEN_AUDIENCE)
                .withNotBefore(new Date(now))
                .withExpiresAt(new Date(exp))
                .withIssuedAt(new Date(now))
                .withJWTId(IdUtil.fastSimpleUUID())
                .withPayload(payload)
                // 签名
                .sign(Algorithm.HMAC512(TokenConstant.TOKEN_SECRET));
    }

    /**
     * 解析Token
     * 当前使用HMAC512的加密算法
     *
     * @param token Token
     */
    public static DecodedJWT parse(String token) {
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC512(TokenConstant.TOKEN_SECRET)).build();
        return jwtVerifier.verify(token);
    }

    /**
     * 获取APPID
     *
     * @param decoded 解析后的Token
     * @return appId
     */
    public static String getAppid(DecodedJWT decoded) {
        return decoded.getClaim(APPID).asString();
    }

    /**
     * 获取SessionKey
     *
     * @param decoded 解析后的Token
     * @return sessionKey
     */
    public static String getSessionkey(DecodedJWT decoded) {
        return decoded.getClaim(SESSIONKEY).asString();
    }

    /**
     * 获取Applyer
     *
     * @param decoded 解析后的Token
     * @return applyer
     */
    public static String getApplyer(DecodedJWT decoded) {
        return decoded.getClaim(APPLYER).asString();
    }


    public static String getApiTokenKey(String tokenKey) {
        return TokenConstant.TOKEN_PREFIX + tokenKey;
    }

    public static DecodedJWT getUserContext() {
        return ThreadContextUtil.get(APP_TOKEN_CONTEXT_KEY);
    }

    public static void setUserContext(DecodedJWT decodedJWT) {
        if (decodedJWT != null) {
            ThreadContextUtil.set(APP_TOKEN_CONTEXT_KEY, decodedJWT);
        }
    }

    public static void main(String[] args) {
//        String token = createToken("ec3f54b398e64fbeb9ed00bb0144a91b",
//                "4c0d6c2a99e541c8a1affc3c8d8dcddb");
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcGkiLCJhdWQiOiJhcGkiLCJuYmYiOjE2OTk0OTc1MTIsInNlc3Npb25LZXkiOiI5MjhkMmM0YjE2NTY0YTY3OTkwZjY4ZmE2ZmExZmIxNSIsImlzcyI6IkhXVEoiLCJleHAiOjE2OTk1MDQ3MTIsInVzZXJOYW1lIjoiZWMzZjU0YjM5OGU2NGZiZWI5ZWQwMGJiMDE0NGE5MWIiLCJpYXQiOjE2OTk0OTc1MTIsImp0aSI6ImNmYTBhNzNmYmM0NTRlYmE4NzNlNmY3ODA3ZDE1Y2QyIn0.0ZoVFzZWFYmUEwbymR7M75hbt6RMDlpyWlh9NzmXgwATaPZGsGCFSou5RvjQWa2vKFOOdUV065pXX3uyWJ3c0Q";
        DecodedJWT decodedJWT = parse(token);
        setUserContext(decodedJWT);
        DecodedJWT jwt = getUserContext();
        System.out.println(jwt.getToken());
        System.out.println(getSessionkey(decodedJWT));
    }

}

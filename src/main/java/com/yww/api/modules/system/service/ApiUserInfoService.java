package com.yww.api.modules.system.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yww.api.modules.system.dao.ApiUserInfoDao;
import com.yww.api.modules.system.entity.ApiUserInfo;
import com.yww.api.utils.AssertUtils;
import com.yww.api.utils.RedisUtil;
import com.yww.api.utils.TokenUtil;
import com.yww.api.utils.UuidUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author yww
 * @since 2023/11/26
 */
@Slf4j
@Service
public class ApiUserInfoService {

    @Autowired
    ApiUserInfoDao apiUserInfoDao;
    @Resource
    RedisUtil redisUtil;

    public static void main(String[] args) {
        // 创建用户
        ApiUserInfo apiUserInfo = new ApiUserInfo();
        apiUserInfo.setId(UuidUtils.uuidNoDash());
        apiUserInfo.setAppId(UuidUtils.uuidNoDash());
        apiUserInfo.setAppSecret(UuidUtils.uuidNoDash());
        apiUserInfo.setApplyer("测试用户");
        apiUserInfo.setState(1);
        System.out.println(DateUtil.now());
        System.out.println(JSONUtil.toJsonStr(apiUserInfo));
    }

    public Map<String, Object> getApiToken(String appId, String appSecret) {
        ApiUserInfo apiAppInfo = apiUserInfoDao.selectOne(
                Wrappers.lambdaQuery(ApiUserInfo.builder().appId(appId).appSecret(appSecret).build())
        );

        AssertUtils.notNull(apiAppInfo, "账号或密码错误");
        AssertUtils.isTrue(apiAppInfo.getState() == 1, "账号未激活");

        String sessionKey = IdUtil.simpleUUID();
        String apiToken = TokenUtil.createToken(apiAppInfo.getAppId(), sessionKey, apiAppInfo.getApplyer());
        log.debug(">> [session-key]：{}", sessionKey);

        redisUtil.setStr(TokenUtil.getApiTokenKey(apiAppInfo.getAppId()), apiToken, 120L);

        Map<String, Object> data = new HashMap<>();
        data.put("userId", apiAppInfo.getId());
        data.put("username", apiAppInfo.getApplyer());
        data.put("key", sessionKey);
        data.put("expireTime", TimeUnit.MINUTES.toSeconds(TokenUtil.API_TOKEN_EXPIRE_MINUTES));
        data.put("unit", "秒");

        return data;
    }


}

package com.yww.api.modules.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yww.api.modules.system.entity.ApiUserInfo;
import org.springframework.stereotype.Repository;

/**
 * 第三方接口调用用户信息(ApiUserInfo)表数据库访问层
 *
 * @author makejava
 * @since 2021-12-09 17:20:44
 */
@Repository
public interface ApiUserInfoDao extends BaseMapper<ApiUserInfo> {

}

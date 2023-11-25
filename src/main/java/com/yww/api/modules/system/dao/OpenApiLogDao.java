package com.yww.api.modules.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yww.api.modules.system.entity.OpenApiLog;
import org.springframework.stereotype.Repository;

/**
 * 开放接口日志Dao层
 *
 * @author chenhao
 */
@Repository
public interface OpenApiLogDao extends BaseMapper<OpenApiLog> {
}

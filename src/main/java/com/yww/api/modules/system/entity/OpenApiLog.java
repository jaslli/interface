package com.yww.api.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * 开放接口日志实体类
 *
 * @author yww
 * @since 2023/11/26
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "open_api_log")
public class OpenApiLog {

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty("ID")
    private String id;

    /**
     * APPID
     */
    private String appId;

    /**
     * 申请人名称
     */
    private String applyer;

    /**
     * 调用方法
     */
    private String method;

    /**
     * IP
     */
    private String serverIp;

    /**
     * 版本号
     */
    private String version;

    /**
     * 接口描述
     */
    private String description;

    /**
     * 报错描述
     */
    private String errMsg;

    /**
     * 日志级别
     */
    private String logType;

    /**
     * IP
     */
    private String clientIp;

    /**
     * 耗时-毫秒
     */
    private Long timeCost;

    /**
     * 创建时间
     */
    private String createTime;

}

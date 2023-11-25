package com.yww.api.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 第三方接口调用用户信息(ApiUserInfo)实体类
 *
 * @author yww
 * @since 2023/11/26
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "api_user_info")
public class ApiUserInfo {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * APP_ID
     */
    private String appId;

    /**
     * APP_SECRET
     */
    private String appSecret;

    /**
     * 申请人
     */
    private String applyer;

    /**
     * 状态
     */
    private Integer state;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}

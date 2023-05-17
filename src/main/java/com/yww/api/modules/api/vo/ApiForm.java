package com.yww.api.modules.api.vo;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 *
 * </p>
 *
 * @author yww
 * @since 2023/5/16
 */
@Data
@Builder
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class ApiForm {

    /**
     * 请求方法
     */
    private String method;

    /**
     *  接口码
     */
    private String apiNo;

    /**
     *  接口版本
     */
    private String version;

    /**
     *  请求参数
     */
    private String bizContent;

    /**
     *  请求参数的JSON对象
     */
    private JSONObject bizContentJson;



    /**
     * 解析请求参数
     */
    public boolean parseBizContent() {
        try {
            // 参数为空
            if (bizContent == null) {
                bizContent = "";
            }

            // 使用Base64对参数进行解码
            if (StrUtil.isNotBlank(bizContent)) {
                bizContent = Base64.decodeStr(bizContent);
            }
            // 将参数转换为JSON字符串
            bizContentJson = JSON.parseObject(bizContent);
            if (bizContentJson == null) {
                bizContentJson = new JSONObject();
            }
            return true;
        } catch (Exception e) {
            log.error("bizContent参数解析失败：{}", e.getMessage());
            return false;
        }
    }

}

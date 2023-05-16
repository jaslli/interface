package com.yww.api.modules.api.vo;

import com.yww.api.constant.ApiCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor
@AllArgsConstructor
public class ApiResp {

    private String apiNo = "";
    /**
     * 状态码
     */
    private String code = ApiCodeEnum.SUCCESS.code();
    /**
     * 返回信息
     */
    private String msg = ApiCodeEnum.SUCCESS.msg();
    /**
     * 时间戳
     */
    private Long timestamp = System.currentTimeMillis();
    /**
     * 返回结果
     */
    private Object data;

    public ApiResp(ApiForm form, ApiCodeEnum apiCodeEnum) {
        this.code = apiCodeEnum.code();
        this.msg = apiCodeEnum.msg();
        this.apiNo = form.getApiNo();
    }

}

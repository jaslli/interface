package com.yww.api.modules.api.form;

import com.yww.api.constant.ApiCodeEnum;
import lombok.Getter;

/**
 * @author yww
 * @since 2023/11/26
 */
@Getter
public class ApiResp {

    private String apiNo = "";
    private String code = ApiCodeEnum.SUCCESS.code();
    private String msg = ApiCodeEnum.SUCCESS.msg();
    private Long timestamp = System.currentTimeMillis();
    private Object data;

    public ApiResp(ApiForm form) {
        this.apiNo = form.getApiNo() == null ? "" : form.getApiNo();
    }

    public ApiResp(ApiCodeEnum apiCodeEnum) {
        this.code = apiCodeEnum.code();
        this.msg = apiCodeEnum.msg();
    }

    public ApiResp(ApiForm form, ApiCodeEnum apiCodeEnum) {
        this.code = apiCodeEnum.code();
        this.msg = apiCodeEnum.msg();
        this.apiNo = form.getApiNo();
    }

    public ApiResp setApiNo(String apiNo) {
        this.apiNo = apiNo;
        return this;
    }

    public ApiResp setCode(String code) {
        this.code = code;
        return this;
    }

    public ApiResp setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public ApiResp setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public ApiResp setData(Object data) {
        this.data = data;
        return this;
    }

    public Boolean isSuccess() {
        return this.getCode().equals(ApiCodeEnum.SUCCESS.code());
    }

}

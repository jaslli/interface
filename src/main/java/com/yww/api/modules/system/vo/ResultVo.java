package com.yww.api.modules.system.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.io.Serializable;

/**
 * 统一的接口响应信息
 *
 * @author yww
 * @since 2023/11/26
 */
@Getter
@ApiModel("API响应消息")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultVo<T> implements Serializable {

    @ApiModelProperty(value = "成功标记", example = "true")
    private boolean success;

    @ApiModelProperty("错误码")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int errCode;

    @ApiModelProperty("错误信息")
    private String errMsg;

    @ApiModelProperty("响应的数据")
    private T data;

    public static ResultVo success() {
        return success(null);
    }

    public static <E> ResultVo<E> success(E data) {
        ResultVo<E> result = new ResultVo<>();
        result.setSuccess(true);
        result.setData(data);
        return result;
    }

    public static ResultVo fail(String errMsg) {
        return fail(500, errMsg);
    }

    public static ResultVo fail(int errCode, String errMsg) {
        ResultVo result = new ResultVo<>();
        result.setSuccess(false);
        result.setErrCode(errCode);
        result.setErrMsg(errMsg);
        return result;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public void setData(T data) {
        this.data = data;
    }

}

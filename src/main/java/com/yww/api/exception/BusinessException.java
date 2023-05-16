package com.yww.api.exception;

import lombok.Getter;

/**
 * <p>
 *      自定义业务异常
 * </p>
 *
 * @author yww
 * @since 2023/5/16
 */
@Getter
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = -1574716826948451793L;

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误信息
     */
    private final String message;

    public BusinessException(String message) {
        this.code = 500;
        this.message = message;
    }

    public BusinessException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}

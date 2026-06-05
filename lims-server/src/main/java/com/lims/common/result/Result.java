package com.lims.common.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("统一响应结果")
public class Result<T> implements Serializable {

    @ApiModelProperty("响应码")
    private Integer code;

    @ApiModelProperty("响应消息")
    private String message;

    @ApiModelProperty("响应数据")
    private T data;

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(ResultCode.SUCCESS.getMessage());
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(String message) {
        return error(ResultCode.ERROR.getCode(), message);
    }

    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public static <T> Result<T> error(ResultCode resultCode) {
        return error(resultCode.getCode(), resultCode.getMessage());
    }
}

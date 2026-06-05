package com.lims.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "权限不足"),
    NOT_FOUND(404, "请求资源不存在"),
    LOGIN_ERROR(1001, "用户名或密码错误"),
    USER_DISABLED(1002, "用户已被禁用"),
    CAPTCHA_ERROR(1003, "验证码错误"),
    TOKEN_INVALID(1004, "Token无效"),
    TOKEN_EXPIRED(1005, "Token已过期"),
    DATA_NOT_EXIST(2001, "数据不存在"),
    DATA_EXIST(2002, "数据已存在"),
    DATA_IN_USE(2003, "数据被引用，无法删除"),
    FILE_UPLOAD_ERROR(3001, "文件上传失败"),
    FILE_NOT_EXIST(3002, "文件不存在"),
    EXPORT_ERROR(4001, "导出失败"),
    IMPORT_ERROR(4002, "导入失败"),
    BIZ_ERROR(9999, "业务异常");

    private final Integer code;
    private final String message;
}

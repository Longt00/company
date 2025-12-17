package com.manage.common;

/**
 * 统一状态码枚举
 *
 * @author System
 * @version 1.0
 */
public enum ResultCode {

    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 失败
     */
    ERROR(500, "操作失败"),

    /**
     * 参数校验失败
     */
    VALIDATE_FAILED(400, "参数校验失败"),

    /**
     * 未登录
     */
    UNAUTHORIZED(401, "未登录或登录已过期"),

    /**
     * 无权限
     */
    FORBIDDEN(403, "没有相关权限"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 用户相关
     */
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_ALREADY_EXISTS(1002, "用户名已存在"),
    EMAIL_ALREADY_EXISTS(1003, "邮箱已存在"),
    PASSWORD_ERROR(1004, "密码错误"),
    USER_DISABLED(1005, "用户已被禁用"),

    /**
     * 认证相关
     */
    TOKEN_INVALID(2001, "Token无效"),
    TOKEN_EXPIRED(2002, "Token已过期"),
    TOKEN_MISSING(2003, "Token缺失"),

    /**
     * 业务异常
     */
    USERNAME_OR_PASSWORD_ERROR(3001, "用户名或密码错误"),
    OLD_PASSWORD_ERROR(3002, "原密码错误"),
    EMAIL_FORMAT_ERROR(3003, "邮箱格式错误"),

    /**
     * 通用错误码
     */
    BAD_REQUEST(400, "请求参数错误"),
    CONFLICT(409, "资源冲突"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
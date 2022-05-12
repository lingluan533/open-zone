package com.zms.openzone.common.exception;

/**
 * @author: zms
 * @create: 2021/3/1 17:29
 */

/*
 * 错误码列表：
 * 10：通用
 * 11：认证服务
 * 12：用户服务
 * 13：搜索服务
 * 14：消息服务
 * 15：交互服务
 * 16：网关服务
 *
 * */
public enum BizCodeEnum {
    OK(0, "成功！"),
    UNKNOW_EXCEPTION(10000, "系统未知异常"),

    USERNAME_REP_EXCEPTION(11000, "该用户名已经被注册!"),
    EMAIL_REP_EXCEPTION(10002, "该邮箱已经被注册!"),
    TO_MANY_REQUEST(10003, "请求流量过大"),
    SMS_SEND_CODE_EXCEPTION(10403, "短信发送失败"),
    USER_EXIST_EXCEPTION(15001, "用户已经存在"),
    PHONE_EXIST_EXCEPTION(15002, "手机号已经存在"),
    LOGINACTT_PASSWORD_ERROR(15003, "账号或密码错误"),
    SOCIALUSER_LOGIN_ERROR(15004, "社交账号登录失败"),
    NOT_STOCK_EXCEPTION(21000, "商品库存不足");

    private int code;
    private String msg;

    BizCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

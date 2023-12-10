package com.meteor.aifadianpay;

/**
 * api错误码
 */
public enum Status {

    N200(200,"success!"),
    N400001(400001,"params incomplete"),
    N400002(400002,"time was expired"),
    N400003(400003,"params was not valid json string"),
    N400004(400004,"no valid token found"),
    N400005(400005,"sign validation failed");

    private int code;
    private String tip;

    Status(int code, String tip) {
        this.code = code;
        this.tip = tip;
    }

    public static Status match(int code){
        return valueOf("N"+code);
    }

    public int getCode() {
        return code;
    }

    public String getTip() {
        return tip;
    }
}

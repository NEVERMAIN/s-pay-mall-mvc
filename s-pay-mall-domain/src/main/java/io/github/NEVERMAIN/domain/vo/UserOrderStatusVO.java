package io.github.NEVERMAIN.domain.vo;

public enum UserOrderStatusVO {

    CREATE("create","创建完成"),

    PAY_WAIT("pay_wait","待支付"),

    pay_success("pay_success","支付成功"),

    DEAL_DONE("deal_done","交易完成"),

    CLOSE("close","交易关闭")

    ;


    private String code;

    private String desc;


    UserOrderStatusVO(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}

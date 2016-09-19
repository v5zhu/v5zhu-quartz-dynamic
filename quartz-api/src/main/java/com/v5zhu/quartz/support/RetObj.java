package com.v5zhu.quartz.support;

public class RetObj {
    private int code = 200;
    private String msg;


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public RetObj() {

    }

    public RetObj(int code, String msg) {
        super();
        this.code = code;
        this.msg = msg;
    }
}

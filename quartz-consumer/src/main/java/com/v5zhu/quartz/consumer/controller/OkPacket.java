package com.v5zhu.quartz.consumer.controller;

/**
 * Created by v5zhu@qq.com on 2016-9-20.
 */
public class OkPacket {
    private final int code = 200;
    private String msg = "操作成功";

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

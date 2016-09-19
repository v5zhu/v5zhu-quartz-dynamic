package com.v5zhu.quartz.consumer.controller;

/**
 * Created by wei9.li@changhong.com on 2015/4/22.
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

package com.team.jixiao.Entity;

import java.io.Serializable;

public class Response_User implements Serializable {
    private int code;
    private String msg;
    private int role;
    private int staff_info_id;

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

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public int getStaff_info_id() {
        return staff_info_id;
    }

    public void setStaff_info_id(int staff_info_id) {
        this.staff_info_id = staff_info_id;
    }
}

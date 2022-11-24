package com.team.jixiao.Entity;

public class Line {
    private int staff_info_id;
    private String Address;
    private String face_photo;
    private String add_time;
    private int sign;
    private String type;

    public int getStaff_info_id() {
        return staff_info_id;
    }

    public void setStaff_info_id(int staff_info_id) {
        this.staff_info_id = staff_info_id;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getFace_photo() {
        return face_photo;
    }

    public void setFace_photo(String face_photo) {
        this.face_photo = face_photo;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public int getSign() {
        return sign;
    }

    public void setSign(int sign) {
        this.sign = sign;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

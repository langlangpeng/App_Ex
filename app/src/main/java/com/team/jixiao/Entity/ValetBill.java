package com.team.jixiao.Entity;

public class ValetBill {
    private int bdid;
    private int id;
    private String ct_name;
    private String ct_phone;
    private String ct_address;
    private String ct_face_photo;
    private String createtime;

    public int getBdid() {
        return bdid;
    }

    public void setBdid(int bdid) {
        this.bdid = bdid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCt_name() {
        return ct_name;
    }

    public void setCt_name(String ct_name) {
        this.ct_name = ct_name;
    }

    public String getCt_phone() {
        return ct_phone;
    }

    public void setCt_phone(String ct_phone) {
        this.ct_phone = ct_phone;
    }

    public String getCt_address() {
        return ct_address;
    }

    public void setCt_address(String ct_address) {
        this.ct_address = ct_address;
    }

    public String getCt_face_photo() {
        return ct_face_photo;
    }

    public void setCt_face_photo(String ct_face_photo) {
        this.ct_face_photo = ct_face_photo;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }
}

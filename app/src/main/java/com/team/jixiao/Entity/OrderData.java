package com.team.jixiao.Entity;

import java.util.List;

public class OrderData {
    /*
    private Integer id;
    private String item_no;
    private String item_name;
    private String pl_num1;
    private String pl_price1;
    private String pl_num2;
    private String pl_price2;
    private String pl_num3;
    private String pl_price3;
    private String gg;
    private String picture_url;
    private boolean isChecked_good;//商品是否选中
    private boolean isChecked_type;//商品是否选中
     */
    private Integer id;
    private Integer item_no;
    private String item_name;
    private String picture_url;
    private String pl_num1;
    private String pl_price1;
    private String pl_num2;
    private String pl_price2;
    private String pl_num3;
    private String pl_price3;
    private boolean isChecked;//商品是否选中
    private List<OrderDataBean> OrderDatalist;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getItem_no() {
        return item_no;
    }

    public void setItem_no(Integer item_no) {
        this.item_no = item_no;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getPicture_url() {
        return picture_url;
    }

    public void setPicture_url(String picture_url) {
        this.picture_url = picture_url;
    }

    public String getPl_num1() {
        return pl_num1;
    }

    public void setPl_num1(String pl_num1) {
        this.pl_num1 = pl_num1;
    }

    public String getPl_price1() {
        return pl_price1;
    }

    public void setPl_price1(String pl_price1) {
        this.pl_price1 = pl_price1;
    }

    public String getPl_num2() {
        return pl_num2;
    }

    public void setPl_num2(String pl_num2) {
        this.pl_num2 = pl_num2;
    }

    public String getPl_price2() {
        return pl_price2;
    }

    public void setPl_price2(String pl_price2) {
        this.pl_price2 = pl_price2;
    }

    public String getPl_num3() {
        return pl_num3;
    }

    public void setPl_num3(String pl_num3) {
        this.pl_num3 = pl_num3;
    }

    public String getPl_price3() {
        return pl_price3;
    }

    public void setPl_price3(String pl_price3) {
        this.pl_price3 = pl_price3;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public List<OrderDataBean> getOrderDatalist() {
        return OrderDatalist;
    }

    public void setOrderDatalist(List<OrderDataBean> orderDatalist) {
        OrderDatalist = orderDatalist;
    }

    public static class OrderDataBean{
        private String gg;
        private boolean isChecked;//商品是否选中

        public String getGg() {
            return gg;
        }

        public void setGg(String gg) {
            this.gg = gg;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }
    }
}

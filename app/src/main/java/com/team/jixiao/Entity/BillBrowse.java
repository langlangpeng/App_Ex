package com.team.jixiao.Entity;

import java.io.Serializable;

public class BillBrowse implements Serializable {
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
    private boolean check;
    private String unit;
    private Double goods_num=1.0;
    private Double sum=0.0;
    private String gg_choice="";
    private String price_choice = "";

    public String getPrice_choice() {
        return price_choice;
    }

    public void setPrice_choice(String price_choice) {
        this.price_choice = price_choice;
    }

    public String getGg_choice() {
        return gg_choice;
    }

    public void setGg_choice(String gg_choice) {
        this.gg_choice = gg_choice;
    }

    public Double getGoods_num() {
        return goods_num;
    }

    public void setGoods_num(Double goods_num) {
        this.goods_num = goods_num;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getItem_no() {
        return item_no;
    }

    public void setItem_no(String item_no) {
        this.item_no = item_no;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
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

    public String getGg() {
        return gg;
    }

    public void setGg(String gg) {
        this.gg = gg;
    }

    public String getPicture_url() {
        return picture_url;
    }

    public void setPicture_url(String picture_url) {
        this.picture_url = picture_url;
    }

}

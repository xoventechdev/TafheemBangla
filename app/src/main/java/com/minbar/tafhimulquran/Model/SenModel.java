package com.minbar.tafhimulquran.Model;

public class SenModel {

    String ch_id;
    String wd_id;
    String sn_id;
    String item;

    public SenModel(String ch_id, String wd_id, String sn_id, String item) {
        this.ch_id = ch_id;
        this.wd_id = wd_id;
        this.sn_id = sn_id;
        this.item = item;
    }

    public String getCh_id() {
        return ch_id;
    }

    public String getWd_id() {
        return wd_id;
    }

    public String getSn_id() {
        return sn_id;
    }

    public String getItem() {
        return item;
    }
}

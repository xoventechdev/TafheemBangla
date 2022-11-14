package com.minbar.tafhimulquran.Model;

public class CharacterSubModel {

    int ch_id;
    int wd_id;
    String item;

    public CharacterSubModel(int ch_id, int wd_id, String item) {
        this.ch_id = ch_id;
        this.wd_id = wd_id;
        this.item = item;
    }

    public int getCh_id() {
        return ch_id;
    }

    public int getWd_id() {
        return wd_id;
    }

    public String getItem() {
        return item;
    }
}

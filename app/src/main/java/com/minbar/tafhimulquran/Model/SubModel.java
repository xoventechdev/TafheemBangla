package com.minbar.tafhimulquran.Model;

public class SubModel {

    /* renamed from: id */
    int id;
    String number;
    String title;
    String total;

    public SubModel(int id, String number, String title, String total) {
        this.id = id;
        this.number = number;
        this.title = title;
        this.total = total;
    }

    public int getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public String getTitle() {
        return title;
    }

    public String getTotal() {
        return total;
    }
}

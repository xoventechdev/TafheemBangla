package com.minbar.tafhimulquran.Model;

public class VumikaModel {
    int id;
    String bnID;
    String title;
    String content;


    public VumikaModel(int id, String bnID, String title, String content) {
        this.id = id;
        this.bnID = bnID;
        this.title = title;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public String getBnID() {
        return bnID;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}

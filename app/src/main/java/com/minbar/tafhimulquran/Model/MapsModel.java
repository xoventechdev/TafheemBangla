package com.minbar.tafhimulquran.Model;

public class MapsModel {
    int id;
    String bnID;
    String title;
    String ref;

    public MapsModel(int id, String bnID, String title, String ref) {
        this.id = id;
        this.bnID = bnID;
        this.title = title;
        this.ref = ref;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBnID() {
        return bnID;
    }

    public void setBnID(String bnID) {
        this.bnID = bnID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }



    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }
}

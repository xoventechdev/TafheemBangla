package com.minbar.tafhimulquran.Model;

public class DarsModel {
    int id;
    String bnID;
    String title;
    String author;
    String ayat;

    public DarsModel(int id, String bnID, String title, String author, String ayat) {
        this.id = id;
        this.bnID = bnID;
        this.title = title;
        this.author = author;
        this.ayat = ayat;
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

    public String getAuthor() {
        return author;
    }

    public String getAyat() {
        return ayat;
    }
}

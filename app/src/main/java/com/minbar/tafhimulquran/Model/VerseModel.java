package com.minbar.tafhimulquran.Model;

public class VerseModel {

    int Id;
    int surahID;
    int verseID;
    String arabic;
    String trans;
    String bangla;
    String english;

    public VerseModel(int id, int surahID, int verseID, String arabic, String trans, String bangla, String english) {
        Id = id;
        this.surahID = surahID;
        this.verseID = verseID;
        this.arabic = arabic;
        this.trans = trans;
        this.bangla = bangla;
        this.english = english;
    }

    public int getId() {
        return Id;
    }

    public int getSurahID() {
        return surahID;
    }

    public int getVerseID() {
        return verseID;
    }

    public String getArabic() {
        return arabic;
    }

    public String getTrans() {
        return trans;
    }

    public String getBangla() {
        return bangla;
    }

    public String getEnglish() {
        return english;
    }
}

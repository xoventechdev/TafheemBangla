package com.minbar.tafhimulquran.Model;

public class HadithModel {

    int hadith_no;
    String hadith_arabic;
    String hadith_bangla;
    String hadith_english;
    String hadith_note;
    int hadith_status;

    public HadithModel(int hadith_no, String hadith_arabic, String hadith_bangla, String hadith_english, String hadith_note, int hadith_status) {
        this.hadith_no = hadith_no;
        this.hadith_arabic = hadith_arabic;
        this.hadith_bangla = hadith_bangla;
        this.hadith_english = hadith_english;
        this.hadith_note = hadith_note;
        this.hadith_status = hadith_status;
    }

    public int getHadith_no() {
        return hadith_no;
    }

    public void setHadith_no(int hadith_no) {
        this.hadith_no = hadith_no;
    }

    public String getHadith_arabic() {
        return hadith_arabic;
    }

    public void setHadith_arabic(String hadith_arabic) {
        this.hadith_arabic = hadith_arabic;
    }

    public String getHadith_bangla() {
        return hadith_bangla;
    }

    public void setHadith_bangla(String hadith_bangla) {
        this.hadith_bangla = hadith_bangla;
    }

    public String getHadith_english() {
        return hadith_english;
    }

    public void setHadith_english(String hadith_english) {
        this.hadith_english = hadith_english;
    }

    public String getHadith_note() {
        return hadith_note;
    }

    public void setHadith_note(String hadith_note) {
        this.hadith_note = hadith_note;
    }

    public int getHadith_status() {
        return hadith_status;
    }

    public void setHadith_status(int hadith_status) {
        this.hadith_status = hadith_status;
    }
}

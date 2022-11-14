package com.minbar.tafhimulquran.Model;

public class WordModel {

    int surah_id;
    int verse_id;
    int word_id;
    String bangla;
    String arabic;

    public WordModel(int surah_id, int verse_id, int word_id, String bangla, String arabic) {
        this.surah_id = surah_id;
        this.verse_id = verse_id;
        this.word_id = word_id;
        this.bangla = bangla;
        this.arabic = arabic;
    }

    public int getSurah_id() {
        return surah_id;
    }

    public int getVerse_id() {
        return verse_id;
    }

    public int getWord_id() {
        return word_id;
    }

    public String getBangla() {
        return bangla;
    }

    public String getArabic() {
        return arabic;
    }
}

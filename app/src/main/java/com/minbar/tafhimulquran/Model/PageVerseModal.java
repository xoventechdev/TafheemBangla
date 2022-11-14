package com.minbar.tafhimulquran.Model;

public class PageVerseModal {

    int surahID;
    int verseId;
    String content;

    public PageVerseModal(int surahID, int verseId, String content) {
        this.surahID = surahID;
        this.verseId = verseId;
        this.content = content;
    }

    public int getSurahID() {
        return surahID;
    }

    public void setSurahID(int surahID) {
        this.surahID = surahID;
    }

    public int getVerseId() {
        return verseId;
    }

    public void setVerseId(int verseId) {
        this.verseId = verseId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

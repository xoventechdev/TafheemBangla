package com.minbar.tafhimulquran.Model;

public class NoteModel {
    private int id;
    private String surahId;
    private String verseId;
    private String note;
    private String surahName;

    public NoteModel(int id, String surahId, String verseId, String note, String surahName) {
        this.id = id;
        this.surahId = surahId;
        this.verseId = verseId;
        this.note = note;
        this.surahName = surahName;
    }

    public int getId() { return id; }

    public String getSurahId() { return surahId; }

    public String getVerseId() { return verseId; }

    public String getNote() { return note; }

    public void setNote(String note) { this.note = note; }

    public String getSurahName() { return surahName; }
}
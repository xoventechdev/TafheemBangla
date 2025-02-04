package com.minbar.tafhimulquran.Model;

public class HadithListModel {
    private int id;
    private int chapterId;
    private String arabicText;
    private String banglaText;
    private String grade;

    public HadithListModel(int id, int chapterId, String arabicText, String banglaText, String grade) {
        this.id = id;
        this.chapterId = chapterId;
        this.arabicText = arabicText;
        this.banglaText = banglaText;
        this.grade = grade;
    }

    public int getId() { return id; }
    public int getChapterId() { return chapterId; }
    public String getArabicText() { return arabicText; }
    public String getBanglaText() { return banglaText; }
    public String getGrade() { return grade; }
}

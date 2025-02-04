package com.minbar.tafhimulquran.Model;

public class HadithChapter {

    private int id;
    private int chapterId;
    private int bookId;
    private String title;
    private String number;
    private String hadisRange;
    private String bookName;

    public HadithChapter(int id, int chapterId, int bookId, String title, String number, String hadisRange, String bookName) {
        this.id = id;
        this.chapterId = chapterId;
        this.bookId = bookId;
        this.title = title;
        this.number = number;
        this.hadisRange = hadisRange;
        this.bookName = bookName;
    }

    // Getters and Setters
    public int getId() { return id; }
    public int getChapterId() { return chapterId; }
    public int getBookId() { return bookId; }
    public String getTitle() { return title; }
    public String getNumber() { return number; }
    public String getHadisRange() { return hadisRange; }
    public String getBookName() { return bookName; }
}

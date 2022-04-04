package com.example.braintrainer.ui.records;

public class RecordItem {

    private String title;

    private long id;

    private int correctAnswers;

    private int time;

    public RecordItem(String title, long id, int correctAnswers, int time)
    {
        this.title = title;
        this.id = id;
        this.correctAnswers = correctAnswers;
        this.time = time;
    }

    public String getTitle()
    {
        return this.title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public long getId()
    {
        return this.id;
    }

    public void setCorrectAnswers(int correctAnswers)
    {
        this.correctAnswers = correctAnswers;
    }

    public int getCorrectAnswers()
    {
        return this.correctAnswers;
    }

    public int getTime()
    {
        return this.time;
    }

    void setTime(int time)
    {
        this.time = time;
    }
}

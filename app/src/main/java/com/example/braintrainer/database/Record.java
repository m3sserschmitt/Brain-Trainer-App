package com.example.braintrainer.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "records")
public class Record {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private String title = "";

    private int time;

    private int correctAnswers;

    public void setId(long id)
    {
        this.id = id;
    }

    public long getId()
    {
        return this.id;
    }

    @NonNull
    public String getTitle()
    {
        return this.title;
    }

    public void setTitle(@NonNull String title)
    {
        this.title = title;
    }

    public void setScore(int correctAnswers, int time)
    {
        this.correctAnswers = correctAnswers;
        this.time = time;
    }

    public void setTime(int time)
    {
        this.time = time;
    }

    public int getTime()
    {
        return this.time;
    }

    public boolean smallerThan(int correctAnswers, int time)
    {
        if(this.time != time)
        {
            return false;
        }

        return this.correctAnswers < correctAnswers;
    }

    public int getCorrectAnswers()
    {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers)
    {
        this.correctAnswers = correctAnswers;
    }

}

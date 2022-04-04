package com.example.braintrainer.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RecordDao {

    @Query("SELECT * FROM records")
    List<Record> getAll();

    @Insert
    void insertAll(Record... records);

    @Delete
    void delete(Record record);

    @Query("DELETE FROM records WHERE id=:id")
    void deleteOne(long id);

    @Query("SELECT * FROM records WHERE time=:time ORDER BY correctAnswers DESC LIMIT 1")
    Record getBest(int time);
}

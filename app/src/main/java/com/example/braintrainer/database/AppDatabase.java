package com.example.braintrainer.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Record.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static final String databaseName = "brain_trainer.db";
    private static volatile AppDatabase instance;

    @NonNull
    private static AppDatabase create(final Context context)
    {
        return Room.databaseBuilder(context, AppDatabase.class, databaseName).build();
    }

    public static synchronized AppDatabase getInstance(Context context)
    {
        if(instance == null)
        {
            instance = create(context);
        }

        return instance;
    }

    protected AppDatabase() {};

    public abstract RecordDao recordDao();

}

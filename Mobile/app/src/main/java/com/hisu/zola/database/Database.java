package com.hisu.zola.database;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Database  {
    public static volatile Database INSTANCE;
    public static final int NUMBER_OF_THREADS = 4;

    static final ExecutorService dbExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

//    public static synchronized Database getDatabase(Context context) {
//        if (INSTANCE == null)
//            INSTANCE = Room.databaseBuilder(
//                    context.getApplicationContext(), Database.class, "Zola_Local_DB"
//            ).build();
//
//        return INSTANCE;
//    }

}
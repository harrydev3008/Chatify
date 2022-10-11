package com.hisu.zola.database;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.hisu.zola.database.dao.ConversationDAO;
import com.hisu.zola.entity.ConversationHolder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@androidx.room.Database(entities = ConversationHolder.class, version = 1, exportSchema = false)
public abstract class Database extends RoomDatabase {

    private static volatile Database INSTANCE;
    /**
     * NUMBER_OF_THREADS is for 4 simple operations: Insert, update, delete & read
     */
    private static final int NUMBER_OF_THREADS = 4;
    public abstract ConversationDAO conversationDAO();

    public static final ExecutorService dbExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static synchronized Database getDatabase(Context context) {
        if (INSTANCE == null)
            INSTANCE = Room.databaseBuilder(
                    context.getApplicationContext(), Database.class, "Zola_Local_DB"
            ).build();

        return INSTANCE;
    }
}
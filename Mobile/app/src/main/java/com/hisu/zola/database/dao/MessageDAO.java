package com.hisu.zola.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.hisu.zola.database.entity.Message;

import java.util.List;

@Dao
public interface MessageDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Message... messages);

    @Query("select * from messages where _id = :messageID")
    Message getMessageById(String messageID);

    @Query("select * from messages where conversation = :conversationID")
    LiveData<List<Message>> getMessages(String conversationID);

    @Query("update messages set text = :text where _id = :id")
    void updateMessage(String id, String text);
}
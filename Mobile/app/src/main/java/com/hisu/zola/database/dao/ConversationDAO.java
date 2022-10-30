package com.hisu.zola.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.hisu.zola.database.entity.Conversation;

import java.util.List;

@Dao
public interface ConversationDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Conversation...conversations);

    @Query("select * from conversations where _id = :conversationID")
    Conversation getConversationById(String conversationID);

    @Query("Select * from conversations")
    LiveData<List<Conversation>> getConversation();

    @Query("update conversations set label = :label where _id = :id")
    void updateConversationName(String id, String label);

    @Update
    void update(Conversation conversation);
}
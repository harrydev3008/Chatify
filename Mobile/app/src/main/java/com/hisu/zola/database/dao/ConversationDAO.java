package com.hisu.zola.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.hisu.zola.entity.ConversationHolder;

import java.util.List;

@Dao
public interface ConversationDAO {
    @Insert
    void insert(ConversationHolder... holder);

    @Query("select * from conversations")
    LiveData<List<ConversationHolder>> getConversation();

    @Query("Update conversations set unreadMessages = :unreadMessages where id = :id")
    void update(int unreadMessages, String id);
}
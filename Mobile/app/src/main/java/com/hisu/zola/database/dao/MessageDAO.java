package com.hisu.zola.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.hisu.zola.entity.Message;

import java.util.List;

@Dao
public interface MessageDAO {
//    @Insert
//    void insert(Message... messages);

//    @Query("select * from messages where conversation = :conversationID")
//    LiveData<List<Message>> getMessages(String conversationID);
}
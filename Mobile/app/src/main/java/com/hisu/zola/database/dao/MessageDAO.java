package com.hisu.zola.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.hisu.zola.database.entity.Message;

import java.util.List;

@Dao
public interface MessageDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Message... messages);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<Message> messages);

    @Query("select * from messages where _id = :messageID")
    Message getMessageById(String messageID);

    @Query("select * from messages where conversation = :conversationID order by createdAt")
    LiveData<List<Message>> getMessages(String conversationID);

    @Query("select * from messages where conversation = :conversationID and isDeleted = 0 and type  LIKE '%' || :type || '%' order by createdAt")
    LiveData<List<Message>> getMessagesImage(String conversationID, String type);

    @Update
    void updateMessage(Message message);

    @Query("update messages set isDeleted = :isDelete where _id = :id")
    void unsent(String id, boolean isDelete);

    @Query("delete from messages where conversation = :conversation")
    void removeAllMessageOfConversation(String conversation);

    @Query("delete from messages")
    void dropMessageTable();
}
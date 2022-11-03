package com.hisu.zola.database.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.hisu.zola.database.Database;
import com.hisu.zola.database.dao.ConversationDAO;
import com.hisu.zola.database.dao.MessageDAO;
import com.hisu.zola.database.entity.Conversation;
import com.hisu.zola.database.entity.Message;
import com.hisu.zola.database.entity.User;

import java.util.List;

public class MessageRepository {
    private final MessageDAO messageDAO;
    private final ConversationDAO conversationDAO;

    public MessageRepository(Application application) {
        Database database = Database.getDatabase(application);
        messageDAO = database.messageDAO();
        conversationDAO = database.conversationDAO();
    }

    public LiveData<List<Message>> getData(String conversation) {
        return messageDAO.getMessages(conversation);
    }

    public void insertOrUpdate(Message message) {
        Database.dbExecutor.execute(() -> {
            if(messageDAO.getMessageById(message.getId()) == null)
                insert(message);
            else
                update(message);
        });
    }

    private void insert(Message message) {
        Database.dbExecutor.execute(() -> {
            messageDAO.insert(message);
        });
    }

    private void update(Message message) {
        Database.dbExecutor.execute(() -> {
            messageDAO.updateMessage(message);
        });
    }

    public void unsent(Message message) {
        Database.dbExecutor.execute(() -> {
            messageDAO.unsent(message.getId(), message.getDeleted());
        });
    }

    public LiveData<Conversation> getConversationInfo(String id) {
        return conversationDAO.getConversationInfoById(id);
    }
}
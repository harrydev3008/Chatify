package com.hisu.zola.database.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.hisu.zola.database.Database;
import com.hisu.zola.database.dao.ConversationDAO;
import com.hisu.zola.database.dao.MessageDAO;
import com.hisu.zola.database.entity.Conversation;
import com.hisu.zola.database.entity.Message;

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

    public LiveData<List<Message>> getImageMessage(String conversation, String type) {
        return messageDAO.getMessagesImage(conversation, type);
    }

    public void insertOrUpdate(Message message) {
        Database.dbExecutor.execute(() -> {
            if (messageDAO.getMessageById(message.getId()) == null)
                insert(message);
            else
                update(message);
        });
    }

    public void insertAll(List<Message> messages) {
        Database.dbExecutor.execute(() -> {
            messages.forEach(message -> {
                if (messageDAO.getMessageById(message.getId()) == null) {
                    messageDAO.insert(message);
                } else {
                    messageDAO.unsent(message.getId(), message.getDeleted());
                }
            });
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
            messageDAO.unsent(message.getId(), true);
        });
    }

    public void deleteAllMessage(String conversationID) {
        Database.dbExecutor.execute(() -> {
            messageDAO.removeAllMessageOfConversation(conversationID);
        });
    }

    public LiveData<Conversation> getConversationInfo(String id) {
        return conversationDAO.getConversationInfoById(id);
    }
}
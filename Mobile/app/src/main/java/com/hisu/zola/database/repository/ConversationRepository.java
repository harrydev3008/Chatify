package com.hisu.zola.database.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.hisu.zola.database.Database;
import com.hisu.zola.database.dao.ConversationDAO;
import com.hisu.zola.database.entity.Conversation;

import java.util.List;

public class ConversationRepository {
    private final ConversationDAO conversationDAO;
    private final LiveData<List<Conversation>> data;

    public ConversationRepository(Application application) {
        Database database = Database.getDatabase(application);
        conversationDAO = database.conversationDAO();
        data = conversationDAO.getConversation();
    }

    public LiveData<List<Conversation>> getData() {
        return data;
    }

    public void insertOrUpdate(Conversation conversation) {
        Database.dbExecutor.execute(() -> {
            if (conversationDAO.getConversationById(conversation.getId()) == null)
                insert(conversation);
            else
                update(conversation);
        });
    }

    public void insertAll(List<Conversation> conversations) {
        Database.dbExecutor.execute(() -> {
            conversationDAO.insertAll(conversations);
        });
    }

    private void insert(Conversation conversation) {
        Database.dbExecutor.execute(() -> {
            conversationDAO.insert(conversation);
        });
    }

    private void update(Conversation conversation) {
        Database.dbExecutor.execute(() -> {
            conversationDAO.update(conversation);
        });
    }

    public void setDisbandGroup(Conversation conversation, String disband) {
        Database.dbExecutor.execute(() -> {
            conversationDAO.setDisband(conversation.getId(), disband);
        });
    }

    public void delete(String conversationID) {
        Database.dbExecutor.execute(() -> {
            conversationDAO.delete(conversationID);
        });
    }

    public void changeGroupName(Conversation conversation) {
        Database.dbExecutor.execute(() -> {
            conversationDAO.updateConversationName(conversation.getId(), conversation.getLabel());
        });
    }

    public LiveData<Conversation> getConversationInfo(String id) {
        return conversationDAO.getConversationInfoById(id);
    }
}
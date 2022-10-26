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
            if(conversationDAO.getConversationById(conversation.getId()) == null)
                insert(conversation);
            else
                update(conversation.getId(), conversation.getLabel());

        });
    }

    private void insert(Conversation conversation) {
        Database.dbExecutor.execute(() -> {
            conversationDAO.insert(conversation);
        });
    }

    private void update(String id, String label) {
        Database.dbExecutor.execute(() -> {
            conversationDAO.updateConversationName(id, label);
        });
    }
}
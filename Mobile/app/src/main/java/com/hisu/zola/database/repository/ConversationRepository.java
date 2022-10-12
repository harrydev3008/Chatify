package com.hisu.zola.database.repository;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;

import com.hisu.zola.database.Database;
import com.hisu.zola.database.dao.ConversationDAO;
import com.hisu.zola.entity.ConversationHolder;

import java.util.List;

public class ConversationRepository {
    private ConversationDAO dao;
    private LiveData<List<ConversationHolder>> data;

    public ConversationRepository(Application application) {
        Database database = Database.getDatabase(application);
        dao = database.conversationDAO();
        data = dao.getConversation();
    }

    public LiveData<List<ConversationHolder>> getData() {
        return data;
    }

    public void insert(ConversationHolder holder) {
        Database.dbExecutor.execute(() -> {
            dao.insert(holder);
        });
    }

    public void update(int newQuan, String id) {
        Database.dbExecutor.execute(() -> {
            dao.update(newQuan,id);
        });
    }
}
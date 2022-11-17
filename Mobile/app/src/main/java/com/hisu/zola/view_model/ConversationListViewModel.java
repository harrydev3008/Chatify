package com.hisu.zola.view_model;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.hisu.zola.database.entity.Conversation;
import com.hisu.zola.database.repository.ConversationRepository;

import java.util.List;

public class ConversationListViewModel extends AndroidViewModel {
    private final ConversationRepository repository;
    private final LiveData<List<Conversation>> data;

    public ConversationListViewModel(Application application) {
        super(application);
        repository = new ConversationRepository(application);
        data = repository.getData();
    }

    public LiveData<List<Conversation>> getData() {
        return data;
    }

    public void insertOrUpdate(Conversation conversation) {
        repository.insertOrUpdate(conversation);
    }

    public void insertAll(List<Conversation> conversations) {
        repository.insertAll(conversations);
    }

    public void delete(Conversation conversation) {
        repository.delete(conversation.getId());
    }

    public void disbandGroup(Conversation conversation, String disband) {
        repository.setDisbandGroup(conversation, disband);
    }
}
package com.hisu.zola.view_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.hisu.zola.database.entity.Conversation;
import com.hisu.zola.database.entity.Message;
import com.hisu.zola.database.repository.MessageRepository;

import java.util.List;

public class ConversationViewModel extends AndroidViewModel {
    private final MessageRepository repository;

    public ConversationViewModel(Application application) {
        super(application);
        repository = new MessageRepository(application);
    }

    public LiveData<List<Message>> getData(String conversationId) {
        return repository.getData(conversationId);
    }

    public void insertOrUpdate(Message message) {
        repository.insertOrUpdate(message);
    }

    public void unsent(Message message) {
        repository.unsent(message);
    }
}
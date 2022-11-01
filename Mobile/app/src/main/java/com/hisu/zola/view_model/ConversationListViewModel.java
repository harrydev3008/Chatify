package com.hisu.zola.view_model;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hisu.zola.database.entity.Conversation;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.database.repository.ConversationRepository;
import com.hisu.zola.util.local.LocalDataManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
}
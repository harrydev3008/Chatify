package com.hisu.zola.view_model;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.hisu.zola.database.repository.ConversationRepository;
import com.hisu.zola.entity.ConversationHolder;

import java.util.List;

public class ConversationListViewModel extends AndroidViewModel {
    private ConversationRepository repository;
    private LiveData<List<ConversationHolder>> data;


    public ConversationListViewModel(Application application) {
        super(application);
        repository = new ConversationRepository(application);
        data = repository.getData();
    }

    public LiveData<List<ConversationHolder>> getData() {
        return data;
    }

    public void insert(ConversationHolder holder) {
        repository.insert(holder);
    }
}
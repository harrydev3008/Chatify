package com.hisu.zola.view_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.hisu.zola.database.entity.ContactUser;
import com.hisu.zola.database.repository.ContactUserRepository;

import java.util.List;

public class ContactUserViewModel extends AndroidViewModel {

    private final ContactUserRepository repository;
    private final LiveData<List<ContactUser>> data;

    public ContactUserViewModel(@NonNull Application application) {
        super(application);
        repository = new ContactUserRepository(application);
        data = repository.getData();
    }

    public LiveData<List<ContactUser>> getData() {
        return data;
    }

    public void insertOrUpdate(ContactUser contactUser) {
        repository.insertOrUpdate(contactUser);
    }

    public void insertAll(List<ContactUser> contactUserList) {
        repository.insertAll(contactUserList);
    }

    public void update(ContactUser contactUser) {
        repository.update(contactUser);
    }

    public void delete(ContactUser contactUser) {
        repository.delete(contactUser);
    }
}

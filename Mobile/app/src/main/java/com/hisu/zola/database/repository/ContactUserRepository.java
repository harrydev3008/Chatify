package com.hisu.zola.database.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.hisu.zola.database.Database;
import com.hisu.zola.database.dao.ContactUserDAO;
import com.hisu.zola.database.entity.ContactUser;

import java.util.List;

public class ContactUserRepository {

    private final ContactUserDAO contactUserDAO;
    private final LiveData<List<ContactUser>> data;

    public ContactUserRepository(Application application) {
        Database database = Database.getDatabase(application);
        contactUserDAO = database.contactUserDAO();
        data = contactUserDAO.getContactUsers();
    }

    public LiveData<List<ContactUser>> getData() {
        return data;
    }

    public void insertOrUpdate(ContactUser contactUser) {
        Database.dbExecutor.execute(() -> {
            if (contactUserDAO.getContactUserByID(contactUser.get_id()) == null)
                contactUserDAO.insert(contactUser);
            else
                contactUserDAO.update(contactUser);
        });
    }

    public void insertAll(List<ContactUser> contactUserList) {
        Database.dbExecutor.execute(() -> {
            contactUserDAO.insertAll(contactUserList);
        });
    }

    public void insert(ContactUser contactUser) {
        Database.dbExecutor.execute(() -> {
            contactUserDAO.insert(contactUser);
        });
    }

    public void update(ContactUser contactUser) {
        Database.dbExecutor.execute(() -> {
            contactUserDAO.update(contactUser);
        });
    }

    public void delete(ContactUser contactUser) {
        Database.dbExecutor.execute(() -> {
            contactUserDAO.delete(contactUser.get_id());
        });
    }

    public void updateFriend(String id, boolean isFriend) {
        Database.dbExecutor.execute(() -> {
            contactUserDAO.updateFriendContact(id, isFriend);
        });
    }
}
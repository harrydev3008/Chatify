package com.hisu.zola.database.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.hisu.zola.database.Database;
import com.hisu.zola.database.dao.UserDAO;
import com.hisu.zola.database.entity.User;

public class UserRepository {
    private final UserDAO userDAO;

    public UserRepository(Application application) {
        Database database = Database.getDatabase(application);
        userDAO = database.userDAO();
    }

    public LiveData<User> getUser(String userID) {
        return userDAO.getUser(userID);
    }

    public void insert(User user) {
        Database.dbExecutor.execute(() -> {
//            if(userDAO.getUser(user.getId()) == null)
            userDAO.insert(user);
        });
    }

    public void update(User user) {
        if (user != null)
            Database.dbExecutor.execute(() -> {
                userDAO.update(user);
            });
    }
}

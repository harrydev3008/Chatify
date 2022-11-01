package com.hisu.zola.listeners;

import com.hisu.zola.database.entity.User;

public interface IOnRemoveUserListener {
    void removeUser(User user);
}
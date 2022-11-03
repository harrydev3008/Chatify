package com.hisu.zola.listeners;

import com.hisu.zola.database.entity.User;

public interface IOnItemCheckedChangListener {
    void itemCheck(User user, boolean isCheck);
}
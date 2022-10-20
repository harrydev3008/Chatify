package com.hisu.zola.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.hisu.zola.entity.User;

@Dao
public interface UserDAO {
    @Insert
    void insert(User... user);

    @Query("select * from users where id = :id")
    User getUser(String id);
}
package com.hisu.zola.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.hisu.zola.database.entity.User;

@Dao
public interface UserDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User... user);

    @Query("select * from users where _id = :id")
    LiveData<User> getUser(String id);

    @Update
    void update(User user);

    @Query("delete from users")
    void dropUserTable();
}
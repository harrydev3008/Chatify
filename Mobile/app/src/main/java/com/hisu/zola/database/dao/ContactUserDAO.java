package com.hisu.zola.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.hisu.zola.database.entity.ContactUser;

import java.util.List;

@Dao
public interface ContactUserDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ContactUser contactUser);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ContactUser> contactUserList);

    @Query("select * from contact_users")
    LiveData<List<ContactUser>> getContactUsers();

    @Query("select * from contact_users where _id = :id")
    ContactUser getContactUserByID(String id);

    @Update
    void update(ContactUser contactUser);

    @Query("delete from contact_users where _id = :id")
    void delete(String id);

    @Query("delete from contact_users")
    void dropTableContactUser();
}
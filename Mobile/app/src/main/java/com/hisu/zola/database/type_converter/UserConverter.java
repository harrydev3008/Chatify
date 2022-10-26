package com.hisu.zola.database.type_converter;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.hisu.zola.database.entity.User;

public class UserConverter {
    @TypeConverter
    public String objectToJson(User user) {
        return new Gson().toJson(user);
    }

    @TypeConverter
    public User jsonToObject(String json) {
        return new Gson().fromJson(json, User.class);
    }
}
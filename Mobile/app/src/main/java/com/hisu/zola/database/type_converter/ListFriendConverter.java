package com.hisu.zola.database.type_converter;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hisu.zola.database.entity.User;

import java.util.List;

public class ListFriendConverter {
    @TypeConverter
    public String listToJson(List<User> friends) {
        return new Gson().toJson(friends);
    }

    @TypeConverter
    public List<User> jsonToList(String json) {
        return new Gson().fromJson(json, new TypeToken<List<User>>() {}.getType());
    }
}

package com.hisu.zola.entity;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class UserListConverter {
    @TypeConverter
    public String convertFromListToString(List<String> members){
        Gson gson = new Gson();

        Type type = new TypeToken<List<String>>(){}.getType();

        return gson.toJson(members, type);
    }

    @TypeConverter
    public List<String> convertFromStringToList(String json){
        if (json != null)
            return null;

        Gson gson = new Gson();

        Type type = new TypeToken<List<String>>(){}.getType();

        return gson.fromJson(json, type);
    }
}

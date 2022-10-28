package com.hisu.zola.database.type_converter;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hisu.zola.database.entity.Media;

import java.util.List;

public class ListMediaConverter {
    @TypeConverter
    public String listToJson(List<Media> media) {
        return new Gson().toJson(media);
    }

    @TypeConverter
    public List<Media> jsonToList(String json) {
        return new Gson().fromJson(json, new TypeToken<List<Media>>() {}.getType());
    }
}
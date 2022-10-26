package com.hisu.zola.database.type_converter;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.hisu.zola.database.entity.Message;

public class MessageConverter {
    @TypeConverter
    public String objectToJson(Message mssage) {
        return new Gson().toJson(mssage);
    }

    @TypeConverter
    public Message jsonToObject(String json) {
        return new Gson().fromJson(json, Message.class);
    }
}
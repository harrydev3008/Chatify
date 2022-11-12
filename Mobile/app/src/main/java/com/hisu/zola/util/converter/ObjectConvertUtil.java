package com.hisu.zola.util.converter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.util.local.LocalDataManager;

import retrofit2.Response;

public class ObjectConvertUtil {
    public static User getResponseUser(Response<Object> response) {
        Gson gson = new Gson();

        String json = gson.toJson(response.body());
        JsonObject obj = gson.fromJson(json, JsonObject.class);

        LocalDataManager.setUserToken(obj.get("token").getAsString());

        return gson.fromJson(obj.get("user"), User.class);
    }
}

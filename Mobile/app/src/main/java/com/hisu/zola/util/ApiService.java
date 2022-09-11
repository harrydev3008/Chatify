package com.hisu.zola.util;

import com.hisu.zola.BuildConfig;
import com.hisu.zola.entity.Conversation;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    ApiService apiService = new Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiService.class);

    @GET("api")
    Call<Conversation> getConversation(@Query("conversationID") String conversationID);
}
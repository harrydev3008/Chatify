package com.hisu.zola.util;

import com.hisu.zola.BuildConfig;
import com.hisu.zola.entity.Conversation;
import com.hisu.zola.entity.Message;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    ApiService apiService = new Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiService.class);

    @GET("api")
    Call<Conversation> getConversation(@Query("conversationID") String conversationID);

    @POST("api/conversation/add")
    Call<Message> sendMessage(
            @Query("conversationID") String conversationID, @Body Message message
    );
}
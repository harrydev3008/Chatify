package com.hisu.zola.util;

import com.hisu.zola.BuildConfig;
import com.hisu.zola.entity.Conversation;
import com.hisu.zola.entity.Message;
import com.hisu.zola.entity.User;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiService {

    ApiService apiService = new Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiService.class);

    @POST("api/auth/signin")
    Call<Object> signIn(@Body User user);

    @POST("api/auth/signup")
    Call<Object> signUp(@Body User user);

    @POST("api/user/getAllFriends")
    Call<List<User>> getAllFriendsOfUser();

    @Multipart
    @POST("/uploadFile")
    Call<Message> postImage(@Part MultipartBody.Part image);
}
package com.hisu.zola.util;

import com.hisu.zola.BuildConfig;
import com.hisu.zola.database.entity.Conversation;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.util.local.LocalDataManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {

    Interceptor INTERCEPTOR = chain -> {
        Request request = chain.request();
        Request.Builder builder = request.newBuilder();
        builder.addHeader("Authorization", "Bearer " + LocalDataManager.getUserToken());
        return chain.proceed(builder.build());
    };

    OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
            .callTimeout(2, TimeUnit.MINUTES)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(INTERCEPTOR);

    ApiService apiService = new Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(httpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiService.class);

    @POST("api/auth/signin")
    Call<Object> signIn(@Body User user);

    @POST("api/auth/signup")
    Call<Object> signUp(@Body User user);

    @POST("api/user/getAllFriends")
    Call<List<User>> getAllFriends();

    @POST("api/conversation/getAllConversations")
    Call<List<Conversation>> getConversations();

    @POST("api/message/getAllMessage")
    Call<Object> getConversationMessages(@Body RequestBody conversation);

    @POST("api/user/getUserByPhonenumber")
    Call<User> findFriendByPhoneNumber(@Body RequestBody phoneNumber);

    @POST("api/user/requestAddFriend")
    Call<Object> sendFriendRequest(@Body RequestBody friendID);

    @POST("api/message/sendMessage")
    Call<Object> sendMessage(@Body RequestBody message);

    @Multipart
    @POST("api/message/uploadFile")
    Call<Object> postImage(@Part MultipartBody.Part image);
}
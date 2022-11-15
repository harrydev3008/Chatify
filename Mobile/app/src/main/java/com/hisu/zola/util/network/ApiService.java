package com.hisu.zola.util.network;

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

    @POST(Constraints.API_LOGIN)
    Call<Object> signIn(@Body User user);

    @POST(Constraints.API_REGISTER)
    Call<Object> signUp(@Body User user);

    @POST(Constraints.API_GET_ALL_FRIENDS)
    Call<List<User>> getAllFriends();

    @POST(Constraints.API_UPDATE_PROFILE)
    Call<Object> updateUser(@Body RequestBody userInfo);

    @POST(Constraints.API_SEND_FRIEND_REQUEST)
    Call<Object> sendFriendRequest(@Body RequestBody friendID);

    @POST(Constraints.API_ACCEPT_FRIEND_REQUEST)
    Call<User> acceptFriendRequest(@Body RequestBody friendID);

    @POST(Constraints.API_DENY_FRIEND_REQUEST)
    Call<User> denyFriendRequest(@Body RequestBody friendID);

    @POST(Constraints.API_UNFRIEND_REQUEST)
    Call<User> unfriend(@Body RequestBody friendID);

    @POST(Constraints.API_CHANGE_PASSWORD)
    Call<Object> changePassword(@Body RequestBody newPassword);

    @POST(Constraints.API_CHANGE_PHONE_NUMBER)
    Call<Object> changePhoneNumber(@Body RequestBody newPhoneNumber);

    @POST(Constraints.API_CHECK_USER_BY_PHONE_NUMBER)
    Call<Object> checkUserExistByPhoneNumber(@Body RequestBody phoneNumber);

    @POST(Constraints.API_GET_USER_BY_PHONE_NUMBER)
    Call<User> findFriendByPhoneNumber(@Body RequestBody phoneNumber);

    @POST(Constraints.API_GET_ALL_MESSAGE)
    Call<Object> getConversationMessages(@Body RequestBody conversation);

    @POST(Constraints.API_SEND_MESSAGE)
    Call<Object> sendMessage(@Body RequestBody message);

    @POST(Constraints.API_DELETE_MESSAGE)
    Call<Object> unsentMessage(@Body RequestBody message);

    @Multipart
    @POST(Constraints.API_UPLOAD_FILE)
    Call<Object> postImage(@Part MultipartBody.Part image);

    @POST(Constraints.API_CREATE_CONVERSATION)
    Call<Conversation> createConversation(@Body RequestBody conversation);

    @POST(Constraints.API_GET_ALL_CONVERSATION_OF_USER)
    Call<List<Conversation>> getConversations();

    @POST(Constraints.API_CHANGE_GROUP_NAME)
    Call<Object> changeGroupName(@Body RequestBody conversation);

    @POST(Constraints.API_CHANGE_GROUP_ADMIN)
    Call<Object> changeGroupAdmin(@Body RequestBody conversation);

    @POST(Constraints.API_ADD_GROUP_MEMBER)
    Call<Object> addMemberToGroup(@Body RequestBody conversation);

    @POST(Constraints.API_REMOVE_GROUP_MEMBER)
    Call<Object> removeMemberFromGroup(@Body RequestBody conversationID);

    @POST(Constraints.API_DISBAND_GROUP)
    Call<Object> disbandGroup(@Body RequestBody conversationID);

    @POST(Constraints.API_OUT_GROUP)
    Call<Object> outGroup(@Body RequestBody conversationID);
}
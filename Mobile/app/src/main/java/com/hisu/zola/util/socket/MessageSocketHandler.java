package com.hisu.zola.util.socket;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hisu.zola.MainActivity;
import com.hisu.zola.database.entity.Conversation;
import com.hisu.zola.database.entity.Message;
import com.hisu.zola.database.repository.ConversationRepository;
import com.hisu.zola.database.repository.MessageRepository;
import com.hisu.zola.fragments.conversation.ConversationFragment;
import com.hisu.zola.util.local.LocalDataManager;
import com.hisu.zola.util.network.ApiService;
import com.hisu.zola.util.network.Constraints;

import java.util.ArrayList;

import io.socket.client.Socket;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageSocketHandler {

    private static void sendMessageViaApi(MainActivity mainActivity, Conversation conversation, String text) {

        JsonObject object = new JsonObject();
        Gson gson = new Gson();
        object.add("conversation", gson.toJsonTree(conversation));
        object.addProperty("sender", LocalDataManager.getCurrentUserInfo().getId());
        object.addProperty("text", text);
        object.addProperty("type", "notification");
            object.add("media", gson.toJsonTree(new ArrayList<>()));

        RequestBody body = RequestBody.create(MediaType.parse(Constraints.JSON_TYPE), object.toString());

        ApiService.apiService.sendMessage(body).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    String json = gson.toJson(response.body());

                    JsonObject obj = gson.fromJson(json, JsonObject.class);

                    Message message = gson.fromJson(obj.get("data"), Message.class);
                    sendMessage(mainActivity, conversation, message);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                Log.e(MessageSocketHandler.class.getName(), t.getLocalizedMessage());
            }
        });
    }

    private static void sendMessage(MainActivity mainActivity, Conversation conversation, Message message) {
        Socket mSocket = SocketIOHandler.getInstance().getSocketConnection();
        MessageRepository messageRepository = new MessageRepository(mainActivity.getApplication());
        ConversationRepository conversationRepository = new ConversationRepository(mainActivity.getApplication());

        if (!mSocket.connected()) {
            mSocket.connect();
        }

        Gson gson = new Gson();
        messageRepository.insertOrUpdate(message);
        conversation.setLastMessage(message);
        conversationRepository.insertOrUpdate(conversation);

        JsonObject emitMsg = new JsonObject();
        emitMsg.add("conversation", gson.toJsonTree(conversation));
        emitMsg.add("sender", gson.toJsonTree(LocalDataManager.getCurrentUserInfo()));

        emitMsg.addProperty("text", message.getText());
        emitMsg.addProperty("type", message.getType());
        emitMsg.add("media", gson.toJsonTree(message.getMedia()));
        emitMsg.addProperty("isDelete", message.getDeleted());
        emitMsg.addProperty("_id", message.getId());
        emitMsg.addProperty("createdAt", message.getCreatedAt());
        emitMsg.addProperty("updatedAt", message.getUpdatedAt());

        mSocket.emit(Constraints.EVT_MESSAGE_SEND, emitMsg);
    }

}
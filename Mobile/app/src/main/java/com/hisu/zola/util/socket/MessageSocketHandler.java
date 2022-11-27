package com.hisu.zola.util.socket;

import android.app.Application;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.hisu.zola.MainActivity;
import com.hisu.zola.database.entity.Conversation;
import com.hisu.zola.database.entity.Media;
import com.hisu.zola.database.entity.Message;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.database.repository.ConversationRepository;
import com.hisu.zola.database.repository.MessageRepository;
import com.hisu.zola.database.repository.UserRepository;
import com.hisu.zola.fragments.conversation.ConversationFragment;
import com.hisu.zola.util.local.LocalDataManager;
import com.hisu.zola.util.network.ApiService;
import com.hisu.zola.util.network.Constraints;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageSocketHandler {

    private static MessageSocketHandler INSTANCE;

    private ConversationRepository conversationRepository;
    private MessageRepository messageRepository;

    private MessageSocketHandler() {
    }

    private MessageSocketHandler(Application application) {
        conversationRepository = new ConversationRepository(application);
        messageRepository = new MessageRepository(application);
    }

    public static synchronized MessageSocketHandler getINSTANCE(Application application) {
        if (INSTANCE == null)
            INSTANCE = new MessageSocketHandler(application);
        return INSTANCE;
    }

    public final Emitter.Listener onMessageReceive = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];
            if (data != null) {
                try {
                    Gson gson = new Gson();

                    Conversation conversation = gson.fromJson(data.getString("conversation"), Conversation.class);

                    User sender = gson.fromJson(data.getString("sender"), User.class);

                    List<Media> media = gson.fromJson(data.get("media").toString(), new TypeToken<List<Media>>() {
                    }.getType());

                    Message message = new Message(data.getString("_id"), conversation.getId(), sender, data.getString("text"),
                            data.getString("type"), data.getString("createdAt"), data.getString("updatedAt"), media, false);

//                    conversation.setLastMessage(message);
//                    conversationRepository.insertOrUpdate(conversation);
//
//                    messageRepository.insertOrUpdate(message);

                    boolean check = false;
                    for (User user : conversation.getMember()) {
                        if (user.getId().equalsIgnoreCase(LocalDataManager.getCurrentUserInfo().getId())) {
                            check = true;
                            break;
                        }
                    }

                    if(check) {
                        conversation.setLastMessage(message);
                        conversationRepository.insertOrUpdate(conversation);
                        messageRepository.insertOrUpdate(message);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public final Emitter.Listener onMessageDeleteReceive = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            JSONObject data = (JSONObject) args[0];
            if (data != null) {
                try {
                    Gson gson = new Gson();

                    Conversation conversation = gson.fromJson(data.getString("conversation"), Conversation.class);

                    User sender = gson.fromJson(data.getString("sender"), User.class);

                    List<Media> media = gson.fromJson(data.get("media").toString(), new TypeToken<List<Media>>() {
                    }.getType());

                    Message message = new Message(data.getString("_id"), conversation.getId(), sender, data.getString("text"),
                            data.getString("type"), data.getString("createdAt"), data.getString("updatedAt"), media, true);

                    boolean check = false;
                    for (User user : conversation.getMember()) {
                        if (user.getId().equalsIgnoreCase(LocalDataManager.getCurrentUserInfo().getId())) {
                            check = true;
                            break;
                        }
                    }

                    if(check) {
                        conversation.setLastMessage(message);
                        conversationRepository.insertOrUpdate(conversation);
                        messageRepository.insertOrUpdate(message);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
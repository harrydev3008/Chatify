package com.hisu.zola.fragments.conversation;

import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.adapters.MessageAdapter;
import com.hisu.zola.database.entity.Conversation;
import com.hisu.zola.database.entity.Media;
import com.hisu.zola.database.entity.Message;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.database.repository.ConversationRepository;
import com.hisu.zola.databinding.FragmentConversationBinding;
import com.hisu.zola.util.ApiService;
import com.hisu.zola.util.NetworkUtil;
import com.hisu.zola.util.RealPathUtil;
import com.hisu.zola.util.SocketIOHandler;
import com.hisu.zola.util.local.LocalDataManager;
import com.hisu.zola.view_model.ConversationViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import gun0912.tedimagepicker.builder.TedImagePicker;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConversationFragment extends Fragment {

    public static final String CONVERSATION_ARGS = "CONVERSATION_INFO";
    public static final String CONVERSATION_NAME_ARGS = "CONVERSATION_NAME";

    private FragmentConversationBinding mBinding;
    private MainActivity mMainActivity;
    private ConversationViewModel viewModel;
    private Socket mSocket;
    private Conversation conversation;
    private String conversationName;
    private boolean isToggleEmojiButton = false;
    private MessageAdapter messageAdapter;
    private List<Message> currentMessageList;
    private ConversationRepository repository;

    public static ConversationFragment newInstance(Conversation conversation, String conversationName) {
        Bundle args = new Bundle();
        args.putSerializable(CONVERSATION_ARGS, conversation);
        args.putString(CONVERSATION_NAME_ARGS, conversationName);

        ConversationFragment fragment = new ConversationFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMainActivity = (MainActivity) getActivity();

        if (getArguments() != null) {
            conversation = (Conversation) getArguments().getSerializable(CONVERSATION_ARGS);
            conversationName = getArguments().getString(CONVERSATION_NAME_ARGS);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentConversationBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SocketIOHandler.getInstance().establishSocketConnection();
        mSocket = SocketIOHandler.getInstance().getSocketConnection();

        repository = new ConversationRepository(mMainActivity.getApplication());

        mSocket.on("msg-receive", onMessageReceive);
        mSocket.on("delete-receive", onMessageDeleteReceive);
        mSocket.on("deleteMemberGroup-receiveMobile", onGroupDeleteMember);
        mSocket.on("deleteGroup-receive", onDisbandGroup);
        mSocket.on("typing", onTyping);

        initRecyclerView();

        initEmojiKeyboard();
        initProgressBar();

        mBinding.tvLastActive.setText(getString(R.string.user_active));
        loadConversationInfo();
        addActionForBackBtn();
        addActionForAudioCallBtn();
        addActionForVideoCallBtn();
        addActionForSideMenu();

        addActionForSendMessageBtn();
        addToggleShowSendIcon();

        mBinding.btnSendImg.setOnClickListener(imgView -> openBottomImagePicker());
    }

    private void initEmojiKeyboard() {
        mBinding.btnEmoji.setOnClickListener(view -> {
            isToggleEmojiButton = !isToggleEmojiButton;

            toggleEmojiButtonIcon();
        });
    }

    private void toggleEmojiButtonIcon() {
        if (isToggleEmojiButton) {
            mBinding.btnEmoji.setImageDrawable(ContextCompat.getDrawable(mMainActivity, R.drawable.ic_keyboard));
        } else {
            mBinding.btnEmoji.setImageDrawable(ContextCompat.getDrawable(mMainActivity, R.drawable.ic_sticker));
        }
    }

    private void openBottomImagePicker() {
        TedImagePicker.with(mMainActivity)
                .title(getString(R.string.pick_img))
                .buttonText(getString(R.string.send))
                .startMultiImage(uris -> {
                    if (NetworkUtil.isConnectionAvailable(mMainActivity))
                        uris.forEach(this::uploadFileToServer);
                    else
                        new iOSDialogBuilder(mMainActivity)
                                .setTitle(getString(R.string.no_network_connection))
                                .setSubtitle(getString(R.string.no_network_connection_desc))
                                .setPositiveListener(getString(R.string.confirm), iOSDialog::dismiss).build().show();
                });
    }

    private void uploadFileToServer(Uri uri) {
        File file = new File(RealPathUtil.getRealPath(mMainActivity, uri));
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        String fileName = file.getName();
        MultipartBody.Part part = MultipartBody.Part.createFormData("media", fileName, requestBody);

        ApiService.apiService.postImage(part).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (response.isSuccessful()) {

                    Gson gson = new Gson();

                    String json = gson.toJson(response.body());
                    JsonObject obj = gson.fromJson(json, JsonObject.class);

                    sendMessageViaApi("", obj.get("data").toString().replaceAll("\"", ""), "image/" + fileName.substring(fileName.lastIndexOf('.') + 1), "image");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                Log.e(ConversationFragment.class.getName(), t.getLocalizedMessage());
            }
        });
    }

    private void initProgressBar() {
        Sprite threeBounce = new ThreeBounce();
        threeBounce.setColor(ContextCompat.getColor(mMainActivity, R.color.primary_color));
        mBinding.progressBar.setIndeterminateDrawable(threeBounce);
    }

    private void initRecyclerView() {
        currentMessageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(mMainActivity);

        viewModel = new ViewModelProvider(mMainActivity).get(ConversationViewModel.class);

        viewModel.getData(conversation.getId()).observe(mMainActivity, new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> messages) {
                currentMessageList.clear();
                currentMessageList.addAll(messages);
                messageAdapter.setMessages(messages);
                if (!currentMessageList.isEmpty())
                    mBinding.rvConversation.smoothScrollToPosition(currentMessageList.size() - 1);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mMainActivity);
        linearLayoutManager.setStackFromEnd(true);

        mBinding.rvConversation.setLayoutManager(
                linearLayoutManager
        );

        if (conversation.getLabel() != null)
            messageAdapter.setGroup(true);

        mBinding.rvConversation.setAdapter(messageAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mBinding.rvConversation);
    }

    private void loadConversationInfo() {
        if (viewModel == null) return;
        viewModel.getConversationInfo(conversation.getId()).observe(mMainActivity, new Observer<Conversation>() {
            @Override
            public void onChanged(Conversation conversation) {
                if (conversation == null) return;
                if (conversation.getLabel() != null)
                    mBinding.tvUsername.setText(conversation.getLabel());
                else
                    mBinding.tvUsername.setText(conversationName);
            }
        });
    }

    private void addActionForBackBtn() {
        mBinding.btnBack.setOnClickListener(view -> {
            mMainActivity.setBottomNavVisibility(View.VISIBLE);
            mMainActivity.getSupportFragmentManager().popBackStackImmediate();
        });
    }

    private void addActionForAudioCallBtn() {
        mBinding.btnAudioCall.setOnClickListener(view -> {
            Toast.makeText(mMainActivity, "Audio call", Toast.LENGTH_SHORT).show();
        });
    }

    private void addActionForVideoCallBtn() {
        mBinding.btnVideoCall.setOnClickListener(view -> {
            Toast.makeText(mMainActivity, "Video call", Toast.LENGTH_SHORT).show();
        });
    }

    private void addActionForSideMenu() {
        mBinding.btnConversationMenu.setOnClickListener(view -> {
            if (conversation.getLabel() == null) {
                mMainActivity.getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in_left, R.anim.slide_out_left,
                                R.anim.slide_out_right, R.anim.slide_out_right)
                        .replace(
                                mMainActivity.getViewContainerID(),
                                ConversationDetailFragment.newInstance(getFriendInfo())
                        )
                        .addToBackStack(null)
                        .commit();
            } else {
                mMainActivity.getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in_left, R.anim.slide_out_left,
                                R.anim.slide_out_right, R.anim.slide_out_right)
                        .replace(
                                mMainActivity.getViewContainerID(),
                                ConversationGroupDetailFragment.newInstance(conversation)
                        )
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private User getFriendInfo() {
        return conversation.getMember().stream()
                .filter(member -> !member.getId()
                        .equalsIgnoreCase(LocalDataManager.getCurrentUserInfo().getId()))
                .findAny().orElse(null);
    }

    private void addActionForSendMessageBtn() {
        mBinding.btnSend.setOnClickListener(view -> {
            if (NetworkUtil.isConnectionAvailable(mMainActivity))
                sendMessageViaApi(mBinding.edtChat.getText().toString().trim(), "", "", "text");
            else
                new iOSDialogBuilder(mMainActivity)
                        .setTitle(getString(R.string.no_network_connection))
                        .setSubtitle(getString(R.string.no_network_connection_desc))
                        .setPositiveListener(getString(R.string.confirm), iOSDialog::dismiss).build().show();
        });
    }

    private void sendMessageViaApi(String text, String url, String imgType, String type) {
        Executors.newSingleThreadExecutor().execute(() -> {
            JsonObject object = new JsonObject();
            Gson gson = new Gson();
            object.add("conversation", gson.toJsonTree(conversation));
            object.addProperty("sender", LocalDataManager.getCurrentUserInfo().getId());
            object.addProperty("text", text);
            object.addProperty("type", type);

            JsonObject media = new JsonObject();
            media.addProperty("url", url);
            media.addProperty("type", imgType);

            object.add("media", gson.toJsonTree(media));

            RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());

            ApiService.apiService.sendMessage(body).enqueue(new Callback<Object>() {
                @Override
                public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                    if (response.isSuccessful() && response.code() == 200) {
                        String json = gson.toJson(response.body());

                        JsonObject obj = gson.fromJson(json, JsonObject.class);

                        Message message = gson.fromJson(obj.get("data"), Message.class);
                        sendMessage(message);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                    mMainActivity.runOnUiThread(() -> {
                        new iOSDialogBuilder(mMainActivity)
                                .setTitle(getString(R.string.notification_warning))
                                .setSubtitle(getString(R.string.notification_warning_msg))
                                .setPositiveListener(getString(R.string.confirm), iOSDialog::dismiss).build().show();
                    });
                    Log.e(ConversationFragment.class.getName(), t.getLocalizedMessage());
                }
            });
        });
    }

    private void sendMessage(Message message) {
        if (!mSocket.connected()) {
            mSocket.connect();
        }

        Gson gson = new Gson();
        viewModel.insertOrUpdate(message);
//        conversation.setLastMessage(message);
//        repository.insertOrUpdate(conversation);

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

        mSocket.emit("send-msg", emitMsg);

        mBinding.edtChat.setText("");
        mBinding.edtChat.requestFocus();
    }

    private void delete(Message message) {
        if (!mSocket.connected()) {
            mSocket.connect();
        }

        Gson gson = new Gson();

        JsonObject emitMsg = new JsonObject();
        emitMsg.add("conversation", gson.toJsonTree(conversation));
        emitMsg.add("sender", gson.toJsonTree(LocalDataManager.getCurrentUserInfo()));
        emitMsg.addProperty("text", message.getText());
        emitMsg.addProperty("type", message.getType());
        emitMsg.addProperty("_id", message.getId());
        emitMsg.add("media", gson.toJsonTree(message.getMedia()));
        emitMsg.add("isDelete", gson.toJsonTree(message.getDeleted()));

        mSocket.emit("delete-msg", emitMsg);

        mBinding.edtChat.setText("");
        mBinding.edtChat.requestFocus();

        viewModel.unsent(message);
    }

    private void addToggleShowSendIcon() {
        mBinding.edtChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                int textLength = editable.toString().trim().length();

                if (textLength > 0) {
                    mBinding.btnSend.setVisibility(View.VISIBLE);
                    mBinding.btnSendImg.setVisibility(View.GONE);
                } else {
                    mBinding.btnSend.setVisibility(View.GONE);
                    mBinding.btnSendImg.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private final Emitter.Listener onMessageReceive = new Emitter.Listener() {
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

                    viewModel.insertOrUpdate(message);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private final Emitter.Listener onMessageDeleteReceive = new Emitter.Listener() {
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

                    viewModel.unsent(message);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void unsentMessage(Message message) {
        Executors.newSingleThreadExecutor().execute(() -> {
            JsonObject object = new JsonObject();
            object.addProperty("id", message.getId());
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());
            ApiService.apiService.unsentMessage(body).enqueue(new Callback<Object>() {
                @Override
                public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                    if (response.isSuccessful() && response.code() == 200) {
                        message.setDeleted(true);
                        delete(message);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                    mMainActivity.runOnUiThread(() -> {
                        new iOSDialogBuilder(mMainActivity)
                                .setTitle(getString(R.string.notification_warning))
                                .setSubtitle(getString(R.string.notification_warning_msg))
                                .setPositiveListener(getString(R.string.confirm), iOSDialog::dismiss).build().show();
                    });
                    Log.e(ConversationFragment.class.getName(), t.getLocalizedMessage());
                }
            });
        });
    }

    private void forwardMessage(Message message) {
        //Todo: forward message
    }

    private final Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mMainActivity.runOnUiThread(() -> {
                String data = (String) args[0];
                if (data != null) {
                    boolean isTyping = Boolean.parseBoolean(data.replaceAll("\"", ""));
                    if (isTyping)
                        mBinding.typing.setVisibility(View.VISIBLE);
                    else
                        mBinding.typing.setVisibility(View.GONE);
                }
            });
        }
    };

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public int getSwipeDirs(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            if (viewHolder instanceof MessageAdapter.MessageReceiveViewHolder) return 0;

            int pos = viewHolder.getBindingAdapterPosition();
            if (currentMessageList.get(pos).getDeleted()) return 0;

            return super.getSwipeDirs(recyclerView, viewHolder);
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int pos = viewHolder.getBindingAdapterPosition();
            Message message = currentMessageList.get(pos);

            if (direction == ItemTouchHelper.LEFT) {
                unsentMessage(message);
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(mMainActivity, R.color.white))
                    .addSwipeLeftActionIcon(R.drawable.ic_remove_msg_outline_rounded)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, -150f, dY, actionState, isCurrentlyActive);
        }
    };

    private final Emitter.Listener onGroupDeleteMember = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mMainActivity.runOnUiThread(() -> {
                JSONObject data = (JSONObject) args[0];
                if (data != null) {

                    try {
                        String deleteUserId = data.getString("id");

                        if (LocalDataManager.getCurrentUserInfo().getId().equalsIgnoreCase(deleteUserId)) {
                            new iOSDialogBuilder(mMainActivity)
                                    .setTitle(getString(R.string.notification_warning))
                                    .setSubtitle(getString(R.string.use_removed))
                                    .setCancelable(false)
                                    .setPositiveListener(getString(R.string.confirm), dialog -> {
                                        dialog.dismiss();
                                        repository.delete(conversation.getId());
                                        mMainActivity.setBottomNavVisibility(View.VISIBLE);
                                        mMainActivity.getSupportFragmentManager().popBackStackImmediate();
                                    }).build().show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private final Emitter.Listener onDisbandGroup = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mMainActivity.runOnUiThread(() -> {
                JSONObject data = (JSONObject) args[0];
                if (data != null) {

                    try {
                        Gson gson = new Gson();

                        Conversation conversation = gson.fromJson(data.toString(), Conversation.class);

                        new iOSDialogBuilder(mMainActivity)
                                .setTitle(getString(R.string.notification_warning))
                                .setSubtitle(getString(R.string.group_disbanded))
                                .setCancelable(false)
                                .setPositiveListener(getString(R.string.confirm), dialog -> {
                                    dialog.dismiss();
                                    repository.delete(conversation.getId());
                                    mMainActivity.setBottomNavVisibility(View.VISIBLE);
                                    mMainActivity.getSupportFragmentManager().popBackStackImmediate();
                                }).build().show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
}
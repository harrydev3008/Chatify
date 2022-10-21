package com.hisu.zola.fragments.conversation;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.google.gson.Gson;
import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.adapters.MessageAdapter;
import com.hisu.zola.databinding.FragmentConversationBinding;
import com.hisu.zola.entity.Message;
import com.hisu.zola.entity.User;
import com.hisu.zola.util.ApiService;
import com.hisu.zola.util.RealPathUtil;
import com.hisu.zola.util.SocketIOHandler;
import com.hisu.zola.util.local.LocalDataManager;
import com.vanniktech.emoji.EmojiPopup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import gun0912.tedimagepicker.builder.TedImagePicker;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConversationFragment extends Fragment {

    public static final String CONVERSATION_ID_ARGS = "CONVERSATION_ID";

    private FragmentConversationBinding mBinding;
    private MainActivity mMainActivity;
    private List<Message> messages;
    private MessageAdapter messageAdapter;
    private Socket mSocket;
    private EmojiPopup emojiPopup;
    private boolean isToggleEmojiButton = false;

    public static ConversationFragment newInstance(String conversationID) {
        Bundle args = new Bundle();
        args.putString(CONVERSATION_ID_ARGS, conversationID);

        ConversationFragment fragment = new ConversationFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainActivity = (MainActivity) getActivity();
        mBinding = FragmentConversationBinding.inflate(inflater, container, false);

        mSocket = SocketIOHandler.getInstance().getSocketConnection();

        mSocket.on("ReceiveMessage", onMessageReceive);
        mSocket.on("typing", onTyping);

        initRecyclerView();

        initEmojiKeyboard();
        initProgressBar();

        getConversationInfo();
        addActionForBackBtn();
        addActionForAudioCallBtn();
        addActionForVideoCallBtn();
        addActionForSideMenu();

        addActionForSendMessageBtn();
        addToggleShowSendIcon();

        mBinding.btnSendImg.setOnClickListener(view -> openBottomImagePicker());

        String conversationID = getArguments() != null ?
                getArguments().getString(CONVERSATION_ID_ARGS) : "";

        loadConversation(conversationID);

        return mBinding.getRoot();
    }

    private void initEmojiKeyboard() {
        emojiPopup = EmojiPopup.Builder.fromRootView(mBinding.test).build(mBinding.edtChat);
        mBinding.btnEmoji.setOnClickListener(view -> {
            isToggleEmojiButton = !isToggleEmojiButton;

            toggleEmojiButtonIcon();
            emojiPopup.toggle();
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
                    uris.forEach(this::uploadFileToServer);
                });
    }

    private void uploadFileToServer(Uri uri) {

        File file = new File(RealPathUtil.getRealPath(mMainActivity, uri));
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("image", file.getName(), requestBody);

        ApiService.apiService.postImage(part).enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                if(response.isSuccessful()) {
                    Message message = response.body();
                    if (message != null) {
                        message.setSender("1");
                        sendMessage(message);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                Log.e("ERR", t.getLocalizedMessage());
            }
        });
    }

    private void initProgressBar() {
        Sprite threeBounce = new ThreeBounce();
        threeBounce.setColor(ContextCompat.getColor(mMainActivity, R.color.primary_color));
        mBinding.progressBar.setIndeterminateDrawable(threeBounce);
    }

    private void initRecyclerView() {

        messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(messages, mMainActivity);

        mBinding.rvConversation.setLayoutManager(
                new LinearLayoutManager(
                        mMainActivity, LinearLayoutManager.VERTICAL, false
                )
        );
    }

    private void getConversationInfo() {
        User currentUser = LocalDataManager.getCurrentUserInfo();
        mBinding.tvUsername.setText(currentUser.getUsername());
        mBinding.tvLastActive.setText(getString(R.string.user_active));
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
            mMainActivity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_left, R.anim.slide_out_left,
                            R.anim.slide_out_right, R.anim.slide_out_right)
                    .replace(
                            mMainActivity.getViewContainerID(),
                            new ConversationDetailFragment()
                    )
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void addActionForSendMessageBtn() {
        mBinding.btnSend.setOnClickListener(view -> {
//            sendMessage(new Message("1", mBinding.edtChat.getText().toString().trim(), "text"));
        });
    }

    private void sendMessage(Message message) {
        if(!mSocket.connected()) {
            mSocket.connect();
        }

        mSocket.emit("NewMessage", new Gson().toJson(message));

        closeSoftKeyboard();

        mBinding.edtChat.setText("");
        mBinding.edtChat.requestFocus();

        messages.add(message);
        messageAdapter.setMessages(messages);
        messageAdapter.notifyItemInserted(messages.indexOf(message));

        smoothScrollToLatestMsg();
    }

    private void closeSoftKeyboard() {
        InputMethodManager manager = (InputMethodManager) mMainActivity.getSystemService(
                Context.INPUT_METHOD_SERVICE
        );

        manager.hideSoftInputFromWindow(mBinding.edtChat.getWindowToken(), 0);
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

    private void loadConversation(String conversationID) {

        messageAdapter.setMessages(messages);
        mBinding.rvConversation.setAdapter(messageAdapter);
    }

    private void smoothScrollToLatestMsg() {
        mBinding.rvConversation.smoothScrollToPosition(messages.size() - 1);
    }

    private final Emitter.Listener onMessageReceive = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mMainActivity.runOnUiThread(() -> {
                String data = (String) args[0];
                if (data != null) {
                    Message message = new Gson().fromJson(data, Message.class);
                    messages.add(message);
                    messageAdapter.setMessages(messages);
                    messageAdapter.notifyItemInserted(messages.indexOf(message));
                    smoothScrollToLatestMsg();
                }
            });
        }
    };

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
}
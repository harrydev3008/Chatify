package com.hisu.zola.fragments.conversation;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.adapters.MessageAdapter;
import com.hisu.zola.database.Database;
import com.hisu.zola.database.entity.Conversation;
import com.hisu.zola.database.entity.Message;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.database.repository.ConversationRepository;
import com.hisu.zola.databinding.FragmentConversationBinding;
import com.hisu.zola.databinding.LayoutChatPopupBinding;
import com.hisu.zola.util.RealPathUtil;
import com.hisu.zola.util.local.LocalDataManager;
import com.hisu.zola.util.network.ApiService;
import com.hisu.zola.util.network.Constraints;
import com.hisu.zola.util.network.NetworkUtil;
import com.hisu.zola.util.socket.SocketIOHandler;
import com.hisu.zola.view_model.ConversationViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

    public static final String CONVERSATION_ARGS = "CONVERSATION_INFO";
    public static final String CONVERSATION_NAME_ARGS = "CONVERSATION_NAME";

    private FragmentConversationBinding mBinding;
    private MainActivity mMainActivity;
    private ConversationViewModel viewModel;
    private Socket mSocket;
    private Conversation conversation;
    private String conversationName;
    private MessageAdapter messageAdapter;
    private List<Message> currentMessageList;
    private ConversationRepository repository;
    private PopupWindow popupMenu;
    private ActivityResultLauncher<Intent> filePickerLauncher;

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

        mSocket.on(Constraints.EVT_ON_TYPING_RECEIVE, onTypingReceive);
        mSocket.on(Constraints.EVT_OFF_TYPING_RECEIVE, onTypingReceive);

        initProgressBar();
        initProgressBarSending();
        initRecyclerView();

        loadConversationInfo();
        addActionForBackBtn();
        addActionForAudioCallBtn();
        addActionForVideoCallBtn();
        addActionForSideMenu();
        initPickFileLauncher();
        addActionForBtnShowAttachFile();
        addActionForSendMessageBtn();
        addToggleShowSendIcon();

        mBinding.btnSendImg.setOnClickListener(imgView -> openBottomImagePicker());
    }

    private void addActionForBtnShowAttachFile() {
        mBinding.btnAttachFile.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            filePickerLauncher.launch(intent);
        });
    }

    private void openBottomImagePicker() {
        if (isCameraPermissionGranted()) {
            TedImagePicker.with(mMainActivity)
                    .title(mMainActivity.getString(R.string.pick_img))
                    .buttonText(mMainActivity.getString(R.string.send))
                    .image()
                    .start(uri -> {
                        mMainActivity.runOnUiThread(() -> mBinding.sending.setVisibility(View.VISIBLE));
                        if (NetworkUtil.isConnectionAvailable(mMainActivity))
                            uploadImageToServer(uri);
                    });
//                .startMultiImage(uris -> { // pick & send multiple images, too bad not yet implemented...
//                    mMainActivity.runOnUiThread(() -> mBinding.sending.setVisibility(View.VISIBLE));
//                    if (NetworkUtil.isConnectionAvailable(mMainActivity))
//                        uris.forEach(this::uploadImageToServer);
//                });
        } else {
            requestCameraPermission();
        }
    }


    private boolean isCameraPermissionGranted() {
        return ContextCompat.checkSelfPermission(mMainActivity, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        String[] permissions = {Manifest.permission.CAMERA};
        ActivityCompat.requestPermissions(mMainActivity, permissions, Constraints.CAMERA_PERMISSION_CODE);
    }


    private void initPickFileLauncher() {
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null)
                        if (result.getData().getData() != null)
                            Database.dbExecutor.execute(() -> {
                                try {
                                    uploadFileToServer(result.getData().getData());
                                } catch (Exception e) {
                                    Log.e(ConversationFragment.class.getName(), e.getLocalizedMessage());
                                }
                            });
                });
    }

    private void initProgressBar() {
        Sprite threeBounce = new ThreeBounce();
        threeBounce.setColor(ContextCompat.getColor(mMainActivity, R.color.primary_color));
        mBinding.progressBar.setIndeterminateDrawable(threeBounce);
    }

    private void initProgressBarSending() {
        Sprite threeBounce = new ThreeBounce();
        threeBounce.setColor(ContextCompat.getColor(mMainActivity, R.color.primary_color));
        mBinding.progressBarSending.setIndeterminateDrawable(threeBounce);
    }

    private void initRecyclerView() {
        currentMessageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(mMainActivity, mMainActivity.getApplication());

        viewModel = new ViewModelProvider(mMainActivity).get(ConversationViewModel.class);

        viewModel.getData(conversation.getId()).observe(mMainActivity, new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> messages) {

                if (messages == null || messages.isEmpty()) {
                    messageAdapter.setMessages(new ArrayList<>());
                    mBinding.rvConversation.setVisibility(View.INVISIBLE);
                    mBinding.emptyChatContainer.setVisibility(View.VISIBLE);
                    return;
                }

                mBinding.emptyChatContainer.setVisibility(View.GONE);

                currentMessageList.clear();
                currentMessageList.addAll(messages);
                mBinding.rvConversation.setVisibility(View.VISIBLE);
                messageAdapter.setMessages(currentMessageList);
                if (!currentMessageList.isEmpty())
                    mBinding.rvConversation.smoothScrollToPosition(currentMessageList.size() - 1);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mMainActivity);
        linearLayoutManager.setStackFromEnd(true);

        mBinding.rvConversation.setLayoutManager(
                linearLayoutManager
        );

        messageAdapter.setGroup(conversation.getGroup());

        mBinding.rvConversation.setAdapter(messageAdapter);
        messageAdapter.setOnItemTouchListener((message, parent) -> {
            showChatPopup(parent, message);
        });
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
                    emitTyping(Constraints.EVT_ON_TYPING, true);
                    mBinding.btnSend.setVisibility(View.VISIBLE);
                    mBinding.btnSendImg.setVisibility(View.GONE);
                } else {
                    emitTyping(Constraints.EVT_OFF_TYPING, false);
                    mBinding.btnSend.setVisibility(View.GONE);
                    mBinding.btnSendImg.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void showChatPopup(View parent, Message message) {
        LayoutInflater inflater = (LayoutInflater)
                mMainActivity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LayoutChatPopupBinding popupBinding = LayoutChatPopupBinding.inflate(inflater, null, false);
        popupMenu = new PopupWindow(popupBinding.getRoot(), 600, RelativeLayout.LayoutParams.WRAP_CONTENT, true);

        if(message.getSender().getId().equalsIgnoreCase(LocalDataManager.getCurrentUserInfo().getId())) {
            popupBinding.tvUnsent.setVisibility(View.VISIBLE);
            popupBinding.tvUnsent.setOnClickListener(view -> {
                unsentMessage(message);
                popupMenu.dismiss();
            });
        }

        popupBinding.tvForward.setOnClickListener(view -> {
            forwardMessage(message);
            popupMenu.dismiss();
        });

        popupMenu.showAsDropDown(parent, -40, 10, Gravity.END);
        View container = (View) popupMenu.getContentView().getParent();
        WindowManager wm = (WindowManager) mMainActivity.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.3f;
        wm.updateViewLayout(container, p);
    }

    private void loadConversationInfo() {
        if (viewModel == null) return;
        viewModel.getConversationInfo(conversation.getId()).observe(mMainActivity, new Observer<Conversation>() {
            @Override
            public void onChanged(Conversation conversation) {
                if (conversation == null) return;
                if (conversation.getGroup()) {
                    mBinding.tvUsername.setText(conversation.getLabel());
                    mBinding.tvLastActive.setText(mMainActivity.getString(R.string.group_active));
                } else {
                    mBinding.tvUsername.setText(conversationName);
                    mBinding.tvLastActive.setText(mMainActivity.getString(R.string.user_active));
                }

                if (conversation.getDisband() != null) {
                    mBinding.btnAudioCall.setVisibility(View.GONE);
                    mBinding.btnVideoCall.setVisibility(View.GONE);
                    mBinding.btnConversationMenu.setVisibility(View.GONE);
                    mBinding.chatContainer.setVisibility(View.GONE);
                    mBinding.groupStatus.setVisibility(View.VISIBLE);
                    mBinding.rvConversation.setBackgroundColor(mMainActivity.getColor(R.color.gray_f1));

                    addActionForBtnRemoveConversation();

                    if (conversation.getDisband().equalsIgnoreCase("disband")) {
                        mBinding.groupStatusDesc.setText(mMainActivity.getText(R.string.group_status_disband));
                        mBinding.tvLastActive.setText(mMainActivity.getString(R.string.last_msg_disbaned));
                    } else if (conversation.getDisband().equalsIgnoreCase("kick")) {
                        mBinding.groupStatusDesc.setText(mMainActivity.getText(R.string.group_status_kick));
                        mBinding.tvLastActive.setText(mMainActivity.getString(R.string.last_msg_kicked));
                    }

                } else {
                    mBinding.btnAudioCall.setVisibility(View.VISIBLE);
                    mBinding.btnVideoCall.setVisibility(View.VISIBLE);
                    mBinding.btnConversationMenu.setVisibility(View.VISIBLE);
                    mBinding.chatContainer.setVisibility(View.VISIBLE);
                    mBinding.groupStatus.setVisibility(View.GONE);
                    mBinding.rvConversation.setBackgroundColor(mMainActivity.getColor(R.color.white));
                }
            }
        });
    }

    private void addActionForBtnRemoveConversation() {
        mBinding.groupStatusDesc.setOnClickListener(view -> {
            repository.delete(conversation.getId());
            mMainActivity.setBottomNavVisibility(View.VISIBLE);
            mMainActivity.getSupportFragmentManager().popBackStackImmediate();
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
            new iOSDialogBuilder(mMainActivity)
                    .setTitle(mMainActivity.getString(R.string.notification_warning))
                    .setSubtitle(mMainActivity.getString(R.string.audio_chat_not_available))
                    .setPositiveListener(mMainActivity.getString(R.string.confirm), iOSDialog::dismiss).build().show();
        });
    }

    private void addActionForVideoCallBtn() {
        mBinding.btnVideoCall.setOnClickListener(view -> {
            new iOSDialogBuilder(mMainActivity)
                    .setTitle(mMainActivity.getString(R.string.notification_warning))
                    .setSubtitle(mMainActivity.getString(R.string.video_chat_not_available))
                    .setPositiveListener(mMainActivity.getString(R.string.confirm), iOSDialog::dismiss).build().show();
        });
    }

    private void addActionForSideMenu() {
        mBinding.btnConversationMenu.setOnClickListener(view -> {
            if (!conversation.getGroup()) {
                mMainActivity.getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in_left, R.anim.slide_out_left,
                                R.anim.slide_out_right, R.anim.slide_out_right)
                        .replace(
                                mMainActivity.getViewContainerID(),
                                ConversationDetailFragment.newInstance(getFriendInfo(), conversation)
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
                        .setTitle(mMainActivity.getString(R.string.no_network_connection))
                        .setSubtitle(mMainActivity.getString(R.string.no_network_connection_desc))
                        .setPositiveListener(mMainActivity.getString(R.string.confirm), iOSDialog::dismiss).build().show();
        });
    }

    private void sendMessageViaApi(String text, String url, String imgType, String type) {
        mBinding.edtChat.setText("");
        mBinding.edtChat.requestFocus();

        mMainActivity.runOnUiThread(() -> {
            mBinding.sending.setVisibility(View.VISIBLE);
        });

        JsonObject object = new JsonObject();
        Gson gson = new Gson();
        object.add("conversation", gson.toJsonTree(conversation));
        object.addProperty("sender", LocalDataManager.getCurrentUserInfo().getId());
        object.addProperty("text", text);
        object.addProperty("type", type);

        if (!type.equalsIgnoreCase("text")) {
            JsonObject media = new JsonObject();
            url = url.replaceAll("\"", "");
            media.addProperty("url", url);
            media.addProperty("type", imgType);

            object.add("media", gson.toJsonTree(media));
        } else {
            object.add("media", gson.toJsonTree(new ArrayList<>()));
        }

        RequestBody body = RequestBody.create(MediaType.parse(Constraints.JSON_TYPE), object.toString());

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
                    mBinding.sending.setVisibility(View.GONE);
                    new iOSDialogBuilder(mMainActivity)
                            .setTitle(mMainActivity.getString(R.string.notification_warning))
                            .setSubtitle(mMainActivity.getString(R.string.notification_warning_msg))
                            .setPositiveListener(mMainActivity.getString(R.string.confirm), iOSDialog::dismiss).build().show();
                });
                Log.e(ConversationFragment.class.getName(), t.getLocalizedMessage());
            }
        });
    }

    private void sendMessage(Message message) {
        if (!mSocket.connected()) {
            mSocket.connect();
        }

        mMainActivity.runOnUiThread(() -> {
            mBinding.sending.setVisibility(View.GONE);
        });
        //todo: check
        Gson gson = new Gson();
        viewModel.insertOrUpdate(message);
        conversation.setLastMessage(message);
        repository.insertOrUpdate(conversation);

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

    private void delete(Message message) {
        if (!mSocket.connected()) {
            mSocket.connect();
        }

        Gson gson = new Gson();

        JsonObject emitMsg = new JsonObject();
        emitMsg.addProperty("_id", message.getId());
        emitMsg.add("conversation", gson.toJsonTree(conversation));
        emitMsg.add("sender", gson.toJsonTree(LocalDataManager.getCurrentUserInfo()));
        emitMsg.addProperty("text", message.getText());
        emitMsg.add("media", gson.toJsonTree(message.getMedia()));
        emitMsg.addProperty("type", message.getType());
        emitMsg.addProperty("isDelete", true);
        emitMsg.addProperty("createdAt", message.getCreatedAt());
        emitMsg.addProperty("updatedAt", message.getUpdatedAt());

        mSocket.emit(Constraints.EVT_DELETE_MESSAGE, emitMsg);

        mBinding.edtChat.setText("");
        mBinding.edtChat.requestFocus();

        viewModel.unsent(message);
    }

    public byte[] readBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        return byteBuffer.toByteArray();
    }

    private String getFileType(String file) {

        String mimeType = MimeTypeMap.getFileExtensionFromUrl(file);

        if (mimeType.matches("jpg|jpeg|png|JPG|JPEG|PNG"))
            return "image/" + mimeType;
        else if (mimeType.matches("mp4|mov|wmv|avi|MP4|MOV|WMV|AVI"))
            return "video/" + mimeType;

        return "application/" + mimeType;
    }

    private void uploadFileToServer(Uri uri) throws Exception {
        if (uri == null) return;
        String filePath = RealPathUtil.getFilePath(mMainActivity, uri);
        if (filePath == null) return;

        byte[] bytes = readBytes(mMainActivity.getContentResolver().openInputStream(uri));

        mMainActivity.runOnUiThread(() -> {
            mBinding.sending.setVisibility(View.VISIBLE);
        });

        File file = new File(filePath);
        RequestBody requestBody = RequestBody.create(MediaType.parse(Constraints.MULTIPART_FORM_DATA_TYPE), bytes);

        String fileName = file.getName();
        MultipartBody.Part part = MultipartBody.Part.createFormData("media", fileName, requestBody);

        ApiService.apiService.postImage(part).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (response.isSuccessful()) {

                    Gson gson = new Gson();

                    JsonElement json = gson.toJsonTree(response.body());
                    JsonObject obj = gson.fromJson(json, JsonObject.class);

                    String fileType = getFileType(fileName);

                    sendMessageViaApi(fileName, obj.get("data").toString(), fileType, fileType);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                mMainActivity.runOnUiThread(() -> {
                    mBinding.sending.setVisibility(View.GONE);
                    new iOSDialogBuilder(mMainActivity)
                            .setTitle(mMainActivity.getString(R.string.notification_warning))
                            .setSubtitle(mMainActivity.getString(R.string.notification_warning_msg))
                            .setPositiveListener(mMainActivity.getString(R.string.confirm), iOSDialog::dismiss).build().show();
                });
                Log.e(ConversationFragment.class.getName(), t.getLocalizedMessage());
            }
        });
    }

    private void uploadImageToServer(Uri uri) {
        File file = new File(RealPathUtil.getRealPath(mMainActivity, uri));
        RequestBody requestBody = RequestBody.create(MediaType.parse(Constraints.MULTIPART_FORM_DATA_TYPE), file);
        String fileName = file.getName();
        MultipartBody.Part part = MultipartBody.Part.createFormData("media", fileName, requestBody);

        mBinding.sending.setVisibility(View.VISIBLE);

        ApiService.apiService.postImage(part).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (response.isSuccessful()) {

                    Gson gson = new Gson();

                    JsonElement json = gson.toJsonTree(response.body());
                    JsonObject obj = gson.fromJson(json, JsonObject.class);

                    String fileType = getFileType(fileName);
                    sendMessageViaApi(fileName, obj.get("data").toString(), fileType, fileType);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                mMainActivity.runOnUiThread(() -> {
                    mBinding.sending.setVisibility(View.GONE);
                    new iOSDialogBuilder(mMainActivity)
                            .setTitle(mMainActivity.getString(R.string.notification_warning))
                            .setSubtitle(mMainActivity.getString(R.string.notification_warning_msg))
                            .setPositiveListener(mMainActivity.getString(R.string.confirm), iOSDialog::dismiss).build().show();
                });
                Log.e(ConversationFragment.class.getName(), t.getLocalizedMessage());
            }
        });
    }

    private void unsentMessage(Message message) {
        JsonObject object = new JsonObject();
        object.addProperty("id", message.getId());
        RequestBody body = RequestBody.create(MediaType.parse(Constraints.JSON_TYPE), object.toString());
        ApiService.apiService.unsentMessage(body).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (response.isSuccessful() && response.code() == 200) {

                    Gson gson = new Gson();

                    String json = gson.toJson(response.body());
                    JsonObject obj = gson.fromJson(json, JsonObject.class);

                    Message updatedMsg = gson.fromJson(obj.get("data"), Message.class);
                    delete(updatedMsg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                mMainActivity.runOnUiThread(() -> {
                    new iOSDialogBuilder(mMainActivity)
                            .setTitle(mMainActivity.getString(R.string.notification_warning))
                            .setSubtitle(mMainActivity.getString(R.string.notification_warning_msg))
                            .setPositiveListener(mMainActivity.getString(R.string.confirm), iOSDialog::dismiss).build().show();
                });
                Log.e(ConversationFragment.class.getName(), t.getLocalizedMessage());
            }
        });
    }

    private void forwardMessage(Message message) {
        mMainActivity.addFragmentToBackStack(ForwardMessageFragment.newInstance(message));
    }

    private void emitTyping(String emit, boolean typing) {
        if (!mSocket.connected()) {
            mSocket.connect();
        }

        Gson gson = new Gson();

        JsonObject emitMsg = new JsonObject();
        emitMsg.addProperty("conversationId", conversation.getId());
        emitMsg.addProperty("sender", LocalDataManager.getCurrentUserInfo().getUsername());
        emitMsg.add("member", gson.toJsonTree(conversation.getMember()));
        emitMsg.addProperty("isTyping", typing);

        mSocket.emit(emit, emitMsg);
    }

    private final Emitter.Listener onTypingReceive = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];
            if (data != null) {
                mMainActivity.runOnUiThread(() -> {
                    try {
                        String sender = data.getString("sender") + " " + mMainActivity.getString(R.string.typing);
                        boolean isTyping = data.getBoolean("isTyping");

                        if (isTyping) {

                            mBinding.typing.setVisibility(View.VISIBLE);
                            mBinding.textView.setText(sender);

                        } else {
                            mBinding.typing.setVisibility(View.GONE);
                            mBinding.textView.setText("");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    };
}
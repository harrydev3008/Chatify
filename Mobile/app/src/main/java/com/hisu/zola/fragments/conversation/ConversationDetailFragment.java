package com.hisu.zola.fragments.conversation;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.google.gson.JsonObject;
import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.database.entity.Conversation;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.database.repository.UserRepository;
import com.hisu.zola.databinding.FragmentConversationDetailBinding;
import com.hisu.zola.util.local.LocalDataManager;
import com.hisu.zola.util.network.ApiService;
import com.hisu.zola.util.network.Constraints;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConversationDetailFragment extends Fragment {

    public static final String USER_ARGS = "USER_DETAIL";
    public static final String CONVERSATION_ARGS = "CONVERSATION_ARGS";
    private FragmentConversationDetailBinding mBinding;
    private MainActivity mainActivity;
    private User user;
    private Conversation conversation;
    private UserRepository userRepository;

    public static ConversationDetailFragment newInstance(User user, Conversation conversation) {
        Bundle args = new Bundle();
        args.putSerializable(USER_ARGS, user);
        args.putSerializable(CONVERSATION_ARGS, conversation);
        ConversationDetailFragment fragment = new ConversationDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) getActivity();

        if (getArguments() != null) {
            user = (User) getArguments().getSerializable(USER_ARGS);
            conversation = (Conversation) getArguments().getSerializable(CONVERSATION_ARGS);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentConversationDetailBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userRepository = new UserRepository(mainActivity.getApplication());

        loadUserDetail(user);
        addActionForBackBtn();
        addActionForEventChangeNickName();
        addActionForEventViewSentFiles();
        addActionForEventDeleteConversation();
        addActionForEventUnfriend();
        addActionForEventAddFriend();
    }

    private void loadUserDetail(User user) {
        mBinding.tvFriendName.setText(user.getUsername());
        Glide.with(mainActivity).load(user.getAvatarURL()).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(mBinding.imvFriendPfp);
        checkFriendInfo();
    }

    private void addActionForBackBtn() {
        mBinding.iBtnBack.setOnClickListener(view -> {
            mainActivity.getSupportFragmentManager().popBackStackImmediate();
        });
    }

    private void addActionForEventChangeNickName() {
        mBinding.tvChangeNickName.setOnClickListener(view -> {
            ChangeNickNameBottomSheetFragment bottomSheetFragment = new ChangeNickNameBottomSheetFragment();
            bottomSheetFragment.setButtonClickListener(bottomSheetFragment::dismiss);
            bottomSheetFragment.show(mainActivity.getSupportFragmentManager(), bottomSheetFragment.getTag());
        });
    }

    private void addActionForEventViewSentFiles() {
        mBinding.tvSentFile.setOnClickListener(view -> {
            mainActivity.addFragmentToBackStack(SentFilesFragment.newInstance(conversation));
        });
    }

    private void addActionForEventDeleteConversation() {
        mBinding.tvDeleteConversation.setOnClickListener(view -> {
//            String url = "https://docs.google.com/gview?embedded=true&url=https://d2w6fysp0et4n5.cloudfront.net/77db10637eb598a5fadc120e034824f2.pdf";
//            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//            startActivity(browserIntent);
        });
    }

    private void addActionForEventUnfriend() {
        mBinding.tvUnfriend.setOnClickListener(view -> {
            new iOSDialogBuilder(mainActivity)
                    .setTitle(mainActivity.getString(R.string.confirm))
                    .setSubtitle(mainActivity.getString(R.string.unfriend_confirm))
                    .setCancelable(false)
                    .setNegativeListener(mainActivity.getString(R.string.no), iOSDialog::dismiss)
                    .setPositiveListener(mainActivity.getString(R.string.yes), dialog -> {
                        dialog.dismiss();
                        unfriend();
                    }).build().show();
        });
    }

    private void addActionForEventAddFriend() {
        mBinding.tvAddFriend.setOnClickListener(view -> {
            addFriend(user.getId());
        });
    }

    private void addFriend(String friendID) {
        JsonObject object = new JsonObject();
        object.addProperty("userId", friendID);
        RequestBody body = RequestBody.create(MediaType.parse(Constraints.JSON_TYPE), object.toString());

        ApiService.apiService.sendFriendRequest(body).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    mainActivity.runOnUiThread(() -> {
                        new iOSDialogBuilder(mainActivity)
                                .setTitle(mainActivity.getString(R.string.notification_warning))
                                .setSubtitle(mainActivity.getString(R.string.friend_request_sent_success))
                                .setCancelable(false)
                                .setPositiveListener(mainActivity.getString(R.string.confirm), dialog -> {
                                    dialog.dismiss();
                                    mainActivity.getSupportFragmentManager().popBackStackImmediate();
                                    mainActivity.getSupportFragmentManager().popBackStackImmediate();
                                })
                                .build().show();
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e(ConversationDetailFragment.class.getName(), t.getLocalizedMessage());
            }
        });
    }

    private void checkFriendInfo() {
        if (isFriend()) {
            mBinding.tvUnfriend.setVisibility(View.VISIBLE);
            mBinding.tvAddFriend.setVisibility(View.GONE);
        } else {
            mBinding.tvUnfriend.setVisibility(View.GONE);
            mBinding.tvAddFriend.setVisibility(View.VISIBLE);
        }
    }

    private boolean isFriend() {
        List<User> friends = LocalDataManager.getCurrentUserInfo().getFriends();

        for (User friend : friends) {
            if (friend.getId().equalsIgnoreCase(user.getId()))
                return true;
        }

        return false;
    }

    private void unfriend() {
        JsonObject object = new JsonObject();
        object.addProperty("deleteFriendId", user.getId());
        RequestBody body = RequestBody.create(MediaType.parse(Constraints.JSON_TYPE), object.toString());
        ApiService.apiService.unfriend(body).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    User curUser = response.body();
                    userRepository.update(curUser);

                    mainActivity.runOnUiThread(() -> {
                        new iOSDialogBuilder(mainActivity)
                                .setTitle(mainActivity.getString(R.string.notification_warning))
                                .setSubtitle(mainActivity.getString(R.string.unfriend_success))
                                .setCancelable(false)
                                .setPositiveListener(mainActivity.getString(R.string.confirm), dialog -> {
                                    dialog.dismiss();
                                    mainActivity.getSupportFragmentManager().popBackStackImmediate();
                                    mainActivity.getSupportFragmentManager().popBackStackImmediate();
                                }).build().show();
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e(ConversationDetailFragment.class.getName(), t.getLocalizedMessage());
            }
        });
    }
}
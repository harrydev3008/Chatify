package com.hisu.zola.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.JsonObject;
import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.database.repository.UserRepository;
import com.hisu.zola.databinding.FragmentSplashScreenBinding;
import com.hisu.zola.fragments.conversation.ConversationListFragment;
import com.hisu.zola.util.local.LocalDataManager;
import com.hisu.zola.util.network.ApiService;
import com.hisu.zola.util.network.Constraints;
import com.hisu.zola.util.network.NetworkUtil;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("CustomSplashScreen")
public class SplashScreenFragment extends Fragment {

    private FragmentSplashScreenBinding mBinding;
    private MainActivity mMainActivity;
    private UserRepository userRepository;

    public static final long DELAY_TIME = 2 * 1000; //2 secs

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentSplashScreenBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userRepository = new UserRepository(mMainActivity.getApplication());

        TextPaint paint = mBinding.tvSloganDesc.getPaint();
        mBinding.tvSlogan.setTextColor(Color.rgb(96, 120, 234));
        mBinding.tvSloganDesc.setTextColor(Color.rgb(23, 234, 217));

        float width = paint.measureText(mMainActivity.getString(R.string.app_name_greet));
        Shader textShader = new LinearGradient(0, 0, width, mBinding.tvSloganDesc.getTextSize(),
                new int[]{
                        Color.rgb(23, 234, 217),
                        Color.rgb(96, 120, 234),
                }, null, Shader.TileMode.MIRROR);

        mBinding.tvSloganDesc.getPaint().setShader(textShader);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            mMainActivity.setBottomNavVisibility(View.GONE);

            if (isUserLoggedIn()) {
                mMainActivity.setBottomNavVisibility(View.VISIBLE);
                mMainActivity.addFragmentToBackStack(new ConversationListFragment());

                if (NetworkUtil.isConnectionAvailable(mMainActivity))
                    updateUserInfo();
            } else
                mMainActivity.setFragment(new StartScreenFragment());

        }, DELAY_TIME);
    }

    private boolean isUserLoggedIn() {
        return LocalDataManager.getUserLoginState();
    }

    private void updateUserInfo() {
        JsonObject object = new JsonObject();
        object.addProperty("phoneNumber", LocalDataManager.getCurrentUserInfo().getPhoneNumber());
        RequestBody body = RequestBody.create(MediaType.parse(Constraints.JSON_TYPE), object.toString());
        ApiService.apiService.findFriendByPhoneNumber(body).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    User user = response.body();
                    LocalDataManager.setCurrentUserInfo(user);
                    userRepository.insertOrUpdate(user);
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e(SplashScreenFragment.class.getName(), t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}
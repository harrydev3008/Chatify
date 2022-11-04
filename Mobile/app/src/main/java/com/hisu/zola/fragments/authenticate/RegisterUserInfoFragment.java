package com.hisu.zola.fragments.authenticate;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.databinding.FragmentRegisterUserInfoBinding;
import com.hisu.zola.fragments.greet_new_user.WelcomeOnBoardingFragment;
import com.hisu.zola.util.ApiService;
import com.hisu.zola.util.RealPathUtil;
import com.hisu.zola.util.converter.ImageConvertUtil;
import com.hisu.zola.util.converter.ObjectConvertUtil;
import com.hisu.zola.util.dialog.LoadingDialog;
import com.hisu.zola.util.local.LocalDataManager;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterUserInfoFragment extends Fragment {

    public static final String REGISTER_KEY = "NEW_USER";
    private User user;
    private LoadingDialog loadingDialog;

    private FragmentRegisterUserInfoBinding mBinding;
    private MainActivity mainActivity;
    private Uri avatarUri;
    private ActivityResultLauncher<Intent> resultLauncher;

    public static RegisterUserInfoFragment newInstance(User user) {
        Bundle args = new Bundle();
        args.putSerializable(REGISTER_KEY, user);
        RegisterUserInfoFragment fragment = new RegisterUserInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) getActivity();

        if (getArguments() != null)
            user = (User) getArguments().getSerializable(REGISTER_KEY);
        else
            user = new User();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentRegisterUserInfoBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e("USer data", user.toString());
        init();
    }

    private void init() {
        loadingDialog = new LoadingDialog(mainActivity, Gravity.CENTER);

        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        avatarUri = result.getData().getData();
                        mBinding.cimvAvatar.setImageURI(avatarUri);
                    }
                });

        generateDefaultPfp();
        addActionForBtnSkip();
        addActionForChangeAvatarButton();
        addActionForBtnSave();
    }

    private void generateDefaultPfp() {
        Bitmap bitmap = ImageConvertUtil.createImageFromText(mainActivity, 150, 150, user.getUsername());
        mBinding.cimvAvatar.setImageBitmap(bitmap);
    }

    private void addActionForBtnSkip() {
        mBinding.btnSkip.setOnClickListener(view -> {
            new AlertDialog.Builder(mainActivity)
                    .setIcon(R.drawable.ic_alert)
                    .setTitle(getString(R.string.confirm))
                    .setMessage(getString(R.string.confirm_skip))
                    .setPositiveButton(getString(R.string.confirm),
                            (dialogInterface, i) -> {
                                updateProfile();
                            })
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show();
        });
    }

    private void addActionForChangeAvatarButton() {
        mBinding.cimvAvatar.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            resultLauncher.launch(intent);
        });
    }

    private void addActionForBtnSave() {
        mBinding.btnSave.setOnClickListener(view -> {
            updateProfile();
        });
    }

    private void updateProfile() {
        loadingDialog.showDialog();

        if (avatarUri != null) {
            Toast.makeText(mainActivity, "here", Toast.LENGTH_SHORT).show();
            File file = new File(RealPathUtil.getRealPath(mainActivity, avatarUri));
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
                        String avatarURL = obj.get("data").toString().replaceAll("\"", "");

                        User user = LocalDataManager.getCurrentUserInfo();
                        user.setGender(mBinding.rBtnGenderMale.isChecked());
                        user.setAvatarURL(avatarURL);

                        updateUserProfile(user);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                    Log.e("ERR post img", t.getLocalizedMessage());
                }
            });
        } else {
            user.setGender(mBinding.rBtnGenderMale.isChecked());
            user.setAvatarURL("");
            updateUserProfile(user);
        }
    }

    private void updateUserProfile(User user) {
        ApiService.apiService.signUp(user).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {

                if (response.isSuccessful() && response.code() == 200) {

                    LocalDataManager.setUserLoginState(true);
                    LocalDataManager.setCurrentUserInfo(ObjectConvertUtil.getResponseUser(response));

                    mainActivity.runOnUiThread(() -> {
                        loadingDialog.dismissDialog();
                        mainActivity.setBottomNavVisibility(View.GONE);
                        mainActivity.addFragmentToBackStack(new WelcomeOnBoardingFragment());
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                Log.e(RegisterUserInfoFragment.class.getName(), t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}
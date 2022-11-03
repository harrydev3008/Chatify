package com.hisu.zola.fragments.profile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.databinding.FragmentEditProfileBinding;
import com.hisu.zola.util.ApiService;
import com.hisu.zola.util.RealPathUtil;
import com.hisu.zola.util.converter.ImageConvertUtil;
import com.hisu.zola.util.dialog.LoadingDialog;
import com.hisu.zola.util.local.LocalDataManager;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileFragment extends Fragment {

    private FragmentEditProfileBinding mBinding;
    private MainActivity mainActivity;
    private Calendar mCalendar;
    private Uri newAvatarUri;
    private ActivityResultLauncher<Intent> resultLauncher;
    private User currentUser;
    private boolean isGenderChanged;
    private LoadingDialog loadingDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        currentUser = LocalDataManager.getCurrentUserInfo();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentEditProfileBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        loadUserInfo();
        addActionForBtnBackToPrevPage();
        addActionToPickImageFromGallery();
        addActionForEditTextDateOfBirth();
        addActionForChangeAvatarButton();
        addActionForBtnSave();
        genderRadioGroupEvent();
    }

    private void init() {
        loadingDialog = new LoadingDialog(mainActivity, Gravity.CENTER);
        mainActivity.setBottomNavVisibility(View.GONE);
        mCalendar = Calendar.getInstance();

        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        newAvatarUri = result.getData().getData();
                        mBinding.imvAvatar.setImageURI(newAvatarUri);
                    }
                });
    }

    private void loadUserInfo() {
        if(currentUser.getAvatarURL() == null || currentUser.getAvatarURL().isEmpty())
            mBinding.imvAvatar.setImageBitmap(ImageConvertUtil.createImageFromText(mainActivity, 150, 150, currentUser.getUsername()));
        else
            Glide.with(mainActivity).load(currentUser.getAvatarURL())
                    .placeholder(R.drawable.bg_profile).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(mBinding.imvAvatar);

        mBinding.edtDisplayName.setText(currentUser.getUsername());

        try {
            Date dateObj = parseDate(currentUser.getDob());
            if (dateObj != null) {
                mCalendar.setTime(dateObj);
                updateDateOfBirthEditText();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (currentUser.isGender())
            mBinding.rBtnGenderM.setChecked(true);
        else
            mBinding.rBtnGenderF.setChecked(true);

        isGenderChanged = currentUser.isGender();
    }

    private Date parseDate(String date) throws ParseException {
        return new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(date);
    }

    private void addActionForBtnBackToPrevPage() {
        mBinding.iBtnBack.setOnClickListener(view -> {
            if (!isDataChanged()) {
                backToPrevPage();
            } else {
                new AlertDialog.Builder(mainActivity)
                        .setMessage(getString(R.string.changes_not_save))
                        .setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> backToPrevPage())
                        .setNegativeButton(getString(R.string.no), null).show();
            }
        });
    }

    private void backToPrevPage() {
        mainActivity.setBottomNavVisibility(View.VISIBLE);
        mainActivity.getSupportFragmentManager().popBackStackImmediate();
    }

    private boolean isDataChanged() {

        if (!mBinding.edtDisplayName.getText().toString().equalsIgnoreCase(currentUser.getUsername()))
            return true;

        if(!mBinding.edtDob.getText().toString().equalsIgnoreCase(currentUser.getDob()))
            return true;

        if (newAvatarUri != null)
            return true;

        return isGenderChanged != currentUser.isGender();
    }

    private void genderRadioGroupEvent() {
        mBinding.radioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            int checkedId = radioGroup.getCheckedRadioButtonId();

            if (checkedId == mBinding.rBtnGenderM.getId())
                isGenderChanged = true;
            else if (checkedId == mBinding.rBtnGenderF.getId())
                isGenderChanged = false;
        });
    }

    private void addActionForChangeAvatarButton() {
        mBinding.imvAvatar.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            resultLauncher.launch(intent);
        });
    }

    private void addActionToPickImageFromGallery() {
        mBinding.imvAvatar.setOnClickListener(view -> {
            Toast.makeText(mainActivity, "pick img", Toast.LENGTH_SHORT).show();
        });
    }

    private void addActionForEditTextDateOfBirth() {
        mBinding.edtDob.setOnClickListener(view -> {

            Locale locale = new Locale("vi");
            Locale.setDefault(locale);

            DatePickerDialog datePickerDialog = new DatePickerDialog(mainActivity,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                    mCalendar.set(Calendar.YEAR, year);
                    mCalendar.set(Calendar.MONTH, month);
                    mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateOfBirthEditText();
                }
            }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                    mCalendar.get(Calendar.DAY_OF_MONTH)
            );

            datePickerDialog.setTitle(getString(R.string.dob));
            datePickerDialog.setIcon(R.drawable.ic_calendar);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        });
    }

    private void updateDateOfBirthEditText() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        mBinding.edtDob.setText(dateFormat.format(mCalendar.getTime()));
    }

    private void addActionForBtnSave() {
        mBinding.btnSave.setOnClickListener(view -> {
            confirmUpdateProfile();
        });
    }

    private void confirmUpdateProfile() {

        if (validateUserUpdateData()) {
            new AlertDialog.Builder(getContext())
                    .setTitle(getString(R.string.confirm_update_profile))
                    .setMessage(getString(R.string.confirm_update_profile_desc))
                    .setIcon(R.drawable.ic_alert)
                    .setPositiveButton(getString(R.string.confirm), (dialogInterface, i) ->
                            updateProfile()
                    )
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show();
        }
    }


    private String getDobFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        return dateFormat.format(mCalendar.getTime());
    }

    private void updateProfile() {

        loadingDialog.showDialog();

        if(newAvatarUri != null) {
            File file = new File(RealPathUtil.getRealPath(mainActivity, newAvatarUri));
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
                        user.setUsername(mBinding.edtDisplayName.getText().toString().trim());
                        user.setDob(getDobFormat());
                        user.setGender(mBinding.rBtnGenderM.isChecked());
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
            User user = LocalDataManager.getCurrentUserInfo();
            user.setUsername(mBinding.edtDisplayName.getText().toString().trim());
            user.setDob(getDobFormat());
            user.setGender(mBinding.rBtnGenderM.isChecked());
            user.setAvatarURL("");
            updateUserProfile(user);
        }
    }

    private void updateUserProfile(User user) {

        JsonObject userJson = new JsonObject();
        userJson.addProperty("username", user.getUsername());
        userJson.addProperty("avatarURL", user.getAvatarURL());
        userJson.addProperty("gender", user.isGender());
        userJson.addProperty("dob", user.getDob());

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), userJson.toString());
        ApiService.apiService.updateUser(body).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if(response.isSuccessful() && response.code() == 200) {
                    loadingDialog.dismissDialog();
                    LocalDataManager.setCurrentUserInfo(user);

                    new AlertDialog.Builder(mainActivity)
                            .setMessage("Cập nhật thành công!")
                            .setPositiveButton("Xác nhận", (dialogInterface, i) -> {
                                currentUser = LocalDataManager.getCurrentUserInfo();
                                loadUserInfo();
                                backToPrevPage();
                            })
                            .setCancelable(false).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
            }
        });
    }

    /**
     * @author Huy
     */
    private boolean validateUserUpdateData() {

        if (TextUtils.isEmpty(mBinding.edtDisplayName.getText().toString().trim())) {
            mBinding.edtDisplayName.setError(getString(R.string.empty_err_displayname));
            mBinding.edtDisplayName.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(mBinding.edtDob.getText().toString().trim())) {
            new androidx.appcompat.app.AlertDialog.Builder(mainActivity)
                    .setIcon(R.drawable.ic_alert)
                    .setMessage(getString(R.string.empty_dob_err))
                    .setPositiveButton(getString(R.string.confirm), null)
                    .show();
            return false;
        }

        if (calculateAge(mBinding.edtDob.getText().toString()) < 15) {
            new androidx.appcompat.app.AlertDialog.Builder(mainActivity)
                    .setIcon(R.drawable.ic_alert)
                    .setMessage(getString(R.string.err_age))
                    .setPositiveButton(getString(R.string.confirm), null)
                    .show();

            return false;
        }

        return true;
    }

    private static int calculateAge(String dobStr) {

        String[] dates = dobStr.trim().split("/");

        Date dob = new Date(
                Integer.parseInt(dates[2]),
                Integer.parseInt(dates[1]),
                Integer.parseInt(dates[0])
        );

        LocalDate today = LocalDate.now();
        Calendar birthDate = Calendar.getInstance();
        birthDate.setTime(dob);

        int age = today.getYear() - birthDate.get(Calendar.YEAR) + 1900;
        if (((birthDate.get(Calendar.MONTH)) > today.getMonthValue())) {
            age--;
        } else if ((birthDate.get(Calendar.MONTH) == today.getMonthValue()) &&
                (birthDate.get(Calendar.DAY_OF_MONTH) > today.getDayOfMonth())) {
            age--;
        }

        return age;
    }
}
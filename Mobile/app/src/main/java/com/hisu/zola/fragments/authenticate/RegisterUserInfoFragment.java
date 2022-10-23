package com.hisu.zola.fragments.authenticate;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.databinding.FragmentRegisterUserInfoBinding;
import com.hisu.zola.entity.User;
import com.hisu.zola.fragments.conversation.ConversationListFragment;
import com.hisu.zola.util.ApiService;
import com.hisu.zola.util.ImageConvertUtil;
import com.hisu.zola.util.ObjectConvertUtil;
import com.hisu.zola.util.local.LocalDataManager;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterUserInfoFragment extends Fragment {

    public static final String REGISTER_KEY  = "NEW_USER";
    private User user;

    private FragmentRegisterUserInfoBinding mBinding;
    private MainActivity mainActivity;
    private Calendar mCalendar;
    private Uri avatarUri;
    private ActivityResultLauncher<Intent> resultLauncher;
    private ProgressDialog dialog;
    public static RegisterUserInfoFragment newInstance(User user) {
        Bundle args = new Bundle();
        args.putSerializable(REGISTER_KEY, user);
        RegisterUserInfoFragment fragment = new RegisterUserInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainActivity = (MainActivity) getActivity();
        mBinding = FragmentRegisterUserInfoBinding.inflate(inflater, container, false);

        if (getArguments() != null)
            user = (User) getArguments().getSerializable(REGISTER_KEY);
        else
            user = new User();

        init();

        return mBinding.getRoot();
    }


    private void init() {
        mCalendar = Calendar.getInstance();

        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        avatarUri = result.getData().getData();
                        mBinding.cimvAvatar.setImageURI(avatarUri);
                    }
                });

        generateDefaultPfp();

        addActionForEditTextDateOfBirth();
        addActionForBtnSkip();
        addActionForChangeAvatarButton();
        addActionForBtnSave();
    }

    private void generateDefaultPfp() {
        Bitmap bitmap = ImageConvertUtil.createImageFromText(mainActivity,150,150, user.getUsername());
        mBinding.cimvAvatar.setImageBitmap(bitmap);
    }

    private void addActionForEditTextDateOfBirth() {
        mBinding.edtDob.setOnClickListener(view -> {

            DatePickerDialog datePickerDialog = new DatePickerDialog(mainActivity,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    (datePicker, year, month, dayOfMonth) -> {
                        mCalendar.set(Calendar.YEAR, year);
                        mCalendar.set(Calendar.MONTH, month);
                        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateDateOfBirthEditText();
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

    private void addActionForBtnSkip() {
        mBinding.btnSkip.setOnClickListener(view -> {
            new AlertDialog.Builder(mainActivity)
                    .setIcon(R.drawable.ic_alert)
                    .setTitle(getString(R.string.confirm))
                    .setMessage(getString(R.string.confirm_skip))
                    .setPositiveButton(getString(R.string.confirm),
                            (dialogInterface, i) -> {
                                saveUserInfo();
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
            saveUserInfo();
        });
    }

    private void saveUserInfo() {
        dialog = new ProgressDialog(mainActivity);

        if(validateUserInfo()){
            Log.e("TETS", user.toString());


//        Executors.newSingleThreadExecutor().execute(() -> {

            mainActivity.runOnUiThread(() -> {
                dialog.setMessage(getString(R.string.pls_wait));
                dialog.show();
            });

//            ApiService.apiService.signUp(user).enqueue(new Callback<Object>() {
//                @Override
//                public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
//
//                    if (response.isSuccessful() && response.code() == 200) {
//
//                        LocalDataManager.setUserLoginState(true);
//                        LocalDataManager.setCurrentUserInfo(ObjectConvertUtil.getResponseUser(response));
//
//                        mainActivity.runOnUiThread(() -> {
//                            dialog.dismiss();
            mainActivity.setBottomNavVisibility(View.VISIBLE);
            mainActivity.addFragmentToBackStack(new ConversationListFragment());
//                        });
//                    }
//              }

//                @Override
//                public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
//                    Log.e("API_ERR", t.getLocalizedMessage());
//                }
//            });
//        });
        }
    }

    private boolean validateUserInfo() {
        if (TextUtils.isEmpty(mBinding.edtDob.getText().toString().trim())) {
            dialog.dismiss();
            mBinding.edtDob.setError(getString(R.string.empty_err_dob));
            mBinding.edtDob.requestFocus();
            return false;
        }
        //Todo: Verify user info => Huy => xử lý xong ngày sinh
        String[] tests = mBinding.edtDob.getText().toString().trim().split("/");
        Date bd = new Date(Integer.parseInt(tests[2]), Integer.parseInt(tests[1]),
                Integer.parseInt(tests[0]));
        if(getAge(bd)<15){
            dialog.dismiss();
            mBinding.edtDob.setError(getString(R.string.err_age));
            mBinding.edtDob.requestFocus();
            return false;
        }

        return true;
    }
    private static int getAge(Date dateOfBirth) {
        LocalDate today = LocalDate.now();
        Calendar birthDate = Calendar.getInstance();
        birthDate.setTime(dateOfBirth);
        int age = today.getYear() - birthDate.get(Calendar.YEAR)+1900;
        if (((birthDate.get(Calendar.MONTH)) > today.getMonthValue())){
            age--;
        }
        else if ((birthDate.get(Calendar.MONTH) == today.getMonthValue()) &&
                (birthDate.get(Calendar.DAY_OF_MONTH) > today.getDayOfMonth())){
            age--;
        }
//        Log.e("now",today+"");
//        Log.e("db",birthDate.get(Calendar.YEAR)+" "+ birthDate.get(Calendar.MONTH)+"  "
//                +birthDate.get(Calendar.DAY_OF_MONTH) ) ;
//        Log.e("age",age+"");
        return age;
    }
}
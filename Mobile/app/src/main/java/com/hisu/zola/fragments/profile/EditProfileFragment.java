package com.hisu.zola.fragments.profile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.databinding.FragmentEditProfileBinding;
import com.hisu.zola.entity.User;
import com.hisu.zola.util.local.LocalDataManager;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

public class EditProfileFragment extends Fragment {

    private FragmentEditProfileBinding mBinding;
    private MainActivity mainActivity;
    private Calendar mCalendar;
    private Uri newAvatarUri;
    private ActivityResultLauncher<Intent> resultLauncher;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainActivity = (MainActivity) getActivity();
        mBinding = FragmentEditProfileBinding.inflate(inflater, container, false);

        init();
        loadUserInfo();
        addActionForBtnBackToPrevPage();
        addActionToPickImageFromGallery();
        addActionForEditTextDateOfBirth();
        addActionForChangeAvatarButton();
        addActionForBtnSave();

        return mBinding.getRoot();
    }

    private void init() {
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
        User user = LocalDataManager.getCurrentUserInfo();
        Glide.with(mainActivity).load(user.getAvatarURL()).into(mBinding.imvAvatar);

        mBinding.edtDisplayName.setText(user.getUsername());

        if (user.isVerifyOTP())
            mBinding.rBtnGenderM.setChecked(true);
        else
            mBinding.rBtnGenderF.setChecked(true);
    }

    private void addActionForBtnBackToPrevPage() {
        mBinding.iBtnBack.setOnClickListener(view -> {
            mainActivity.setBottomNavVisibility(View.VISIBLE);
            mainActivity.getSupportFragmentManager().popBackStackImmediate();
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

    private void updateProfile() {
        //Todo: call update api
        uploadAvatar();
        User user = LocalDataManager.getCurrentUserInfo();
        user.setUsername(mBinding.edtDisplayName.getText().toString().trim());
        user.setDob(getDobFormat());

        Log.e("user update", user.toString());
        mBinding.iBtnBack.performClick();
    }

    private String getDobFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        return dateFormat.format(mCalendar.getTime());
    }

    private void uploadAvatar() {
        //Todo: upload default generated pfp
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
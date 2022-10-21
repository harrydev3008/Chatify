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
import java.util.Calendar;
import java.util.Locale;

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

        if(user.isVerifyOTP())
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


    private void updateProfile() {
        ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage(getString(R.string.pls_wait));
        dialog.show();

        if(validateUserUpdateData()) {
            dialog.dismiss();
            mBinding.iBtnBack.performClick();
        }
    }

    private boolean validateUserUpdateData() {
        //Todo: validate user data before save => Huy
        return true;
    }
}
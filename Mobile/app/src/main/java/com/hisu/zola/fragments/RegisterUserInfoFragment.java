package com.hisu.zola.fragments;

import android.app.Activity;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.databinding.FragmentRegisterUserInfoBinding;
import com.hisu.zola.fragments.conversation.ConversationListFragment;
import com.hisu.zola.util.local.LocalDataManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RegisterUserInfoFragment extends Fragment {

    private FragmentRegisterUserInfoBinding mBinding;
    private MainActivity mainActivity;
    private Calendar mCalendar;
    private Uri avatarUri;
    private ActivityResultLauncher<Intent> resultLauncher;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainActivity = (MainActivity) getActivity();
        mBinding = FragmentRegisterUserInfoBinding.inflate(inflater, container, false);

        init();

        addActionForEditTextDateOfBirth();
        addActionForBtnSkip();
        addActionForChangeAvatarButton();
        addActionForBtnSave();

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
            datePickerDialog.setIcon(R.drawable.ic_happy);
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
                                LocalDataManager.setUserLoginState(true);
                                mainActivity.setBottomNavVisibility(View.VISIBLE);
                                mainActivity.addFragmentToBackStack(
                                        new ConversationListFragment()
                                );
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
        ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage(getString(R.string.pls_wait));
        dialog.show();

        if(validateUserInfo()) {
            dialog.dismiss();
            LocalDataManager.setUserLoginState(true);
            mainActivity.setBottomNavVisibility(View.VISIBLE);
            mainActivity.addFragmentToBackStack(new ConversationListFragment());
        }
    }

    private boolean validateUserInfo() {
        //Verify user info
        return true;
    }
}
package com.hisu.zola.fragments;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.databinding.FragmentEditProfileBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditProfileFragment extends Fragment {

    private FragmentEditProfileBinding mBinding;
    private MainActivity mainActivity;
    private Calendar mCalendar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainActivity = (MainActivity) getActivity();
        mBinding = FragmentEditProfileBinding.inflate(inflater, container, false);

        init();

        addActionForBtnBackToPrevPage();
        addActionToPickImageFromGallery();
        addActionForEditTextDateOfBirth();
        addActionForBtnSave();

        return mBinding.getRoot();
    }

    private void init() {
        mCalendar = Calendar.getInstance();
    }

    private void addActionForBtnBackToPrevPage() {
        mBinding.iBtnBack.setOnClickListener(view -> {
            mainActivity.getSupportFragmentManager().popBackStack();
            mainActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            mainActivity.getViewContainerID(),
                            HomeFragment.newInstance(HomeFragment.BACK_FROM_EDIT_ARGS)
                    )
                    .addToBackStack("Home")
                    .commit();
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
            datePickerDialog.setIcon(R.drawable.ic_happy);
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
            Toast.makeText(mainActivity, "Update user data", Toast.LENGTH_SHORT).show();
        });
    }

    private boolean validateUserUpdateData() {
        //Todo: validate user data before save
        return true;
    }
}
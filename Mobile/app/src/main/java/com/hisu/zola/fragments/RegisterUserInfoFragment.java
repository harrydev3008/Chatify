package com.hisu.zola.fragments;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.databinding.FragmentRegisterUserInfoBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RegisterUserInfoFragment extends Fragment {

    private FragmentRegisterUserInfoBinding mBinding;
    private MainActivity mainActivity;
    private Calendar mCalendar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainActivity = (MainActivity) getActivity();
        mBinding = FragmentRegisterUserInfoBinding.inflate(inflater, container, false);

        init();

        addActionForEditTextDateOfBirth();

        mBinding.btnSkip.setOnClickListener(view -> {
            new AlertDialog.Builder(mainActivity)
                    .setIcon(R.drawable.ic_alert)
                    .setTitle(getString(R.string.confirm))
                    .setMessage(getString(R.string.confirm_skip))
                    .setPositiveButton(getString(R.string.confirm),
                            (dialogInterface, i) -> mainActivity.setFragment(
                                    HomeFragment.newInstance(HomeFragment.NORMAL_ARGS)
                            ))
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show();
        });

        return mBinding.getRoot();
    }

    private void init() {
        mCalendar = Calendar.getInstance();
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
}
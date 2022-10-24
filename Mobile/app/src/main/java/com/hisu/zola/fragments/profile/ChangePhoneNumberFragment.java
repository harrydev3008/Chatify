package com.hisu.zola.fragments.profile;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.databinding.FragmentChangePhoneNumberBinding;
import com.hisu.zola.util.dialog.ConfirmSendOTPDialog;

import java.util.regex.Pattern;

public class ChangePhoneNumberFragment extends Fragment {

    private FragmentChangePhoneNumberBinding mBinding;
    private MainActivity mainActivity;
    private ConfirmSendOTPDialog dialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainActivity = (MainActivity) getActivity();
        mBinding = FragmentChangePhoneNumberBinding.inflate(inflater, container, false);

        backToPrevPage();
        clearTextOnSearchEditText();
        phoneNumberOnChangeEvent();
        addActionForBtnContinue();

        return mBinding.getRoot();
    }

    private void backToPrevPage() {
        mBinding.iBtnBack.setOnClickListener(view -> {
            mainActivity.getSupportFragmentManager().popBackStackImmediate();
        });
    }

    private void phoneNumberOnChangeEvent() {
        mBinding.edtNewPhoneNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() > 0) {
                    mBinding.btnContinue.setBackgroundColor(ContextCompat.getColor(mainActivity, R.color.primary_color));
                    mBinding.btnContinue.setClickable(true);
                } else {
                    mBinding.btnContinue.setBackgroundColor(ContextCompat.getColor(mainActivity, R.color.gray_bf));
                    mBinding.btnContinue.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0)
                    mBinding.edtNewPhoneNo.setCompoundDrawablesWithIntrinsicBounds(
                            null, null,
                            ContextCompat.getDrawable(mainActivity, R.drawable.ic_close), null
                    );
                else
                    mBinding.edtNewPhoneNo.setCompoundDrawablesWithIntrinsicBounds(
                            null, null, null, null
                    );
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void clearTextOnSearchEditText() {
        mBinding.edtNewPhoneNo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mBinding.edtNewPhoneNo.getCompoundDrawables()[2] == null) return false;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (mBinding.edtNewPhoneNo.getRight() -
                            mBinding.edtNewPhoneNo.getCompoundDrawables()[2]
                                    .getBounds().width())) {

                        mBinding.edtNewPhoneNo.setText("");
                        return true;
                    }
                }

                return false;
            }
        });
    }

    private void initDialog() {
        dialog = new ConfirmSendOTPDialog(mainActivity, Gravity.CENTER, getString(R.string.otp_change_phone_no));

        dialog.addActionForBtnChange(view_change -> {
            dialog.dismissDialog();
            mBinding.edtNewPhoneNo.requestFocus();
        });

        dialog.addActionForBtnConfirm(view_confirm -> {
            Toast.makeText(mainActivity, "confirm", Toast.LENGTH_SHORT).show();
        });
    }

    private void addActionForBtnContinue() {
        mBinding.btnContinue.setOnClickListener(view -> {
            if (dialog == null)
                initDialog();

            if(verifyPhoneNumber(mBinding.edtNewPhoneNo.getText().toString())) {
                dialog.setNewPhoneNumber(mBinding.edtNewPhoneNo.getText().toString());
                dialog.showDialog();
            }
        });
    }

    /*
    * @author Huy
    * */
    private boolean verifyPhoneNumber(String phoneNumber) {
        Pattern patternPhoneNumber = Pattern.compile("^(032|033|034|035|036|037|038|039|086|096|097|098|" +
                "070|079|077|076|078|089|090|093|" +
                "083|084|085|081|082|088|091|094|" +
                "056|058|092|" +
                "059|099)[0-9]{7}$");

        if (!patternPhoneNumber.matcher(phoneNumber).matches()){
            mBinding.edtNewPhoneNo.setError(getString(R.string.invalid_phone_format_err));
            mBinding.edtNewPhoneNo.requestFocus();
            return false;
        }
        return true;
    }
}
package com.hisu.zola.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.databinding.FragmentAddFriendBinding;
import com.hisu.zola.fragments.contact.FriendFromContactFragment;
import com.hisu.zola.fragments.contact.FriendRequestFragment;

public class AddFriendFragment extends Fragment {

    private FragmentAddFriendBinding mBinding;
    private MainActivity mainActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainActivity = (MainActivity) getActivity();
        mBinding = FragmentAddFriendBinding.inflate(inflater, container, false);

        mainActivity.setBottomNavVisibility(View.GONE);

        backToPrevPage();
        showFriendRequestList();
        showFriendFromContact();
        phoneNumberOnChangeEvent();
        addActionForBtnFind();

        return mBinding.getRoot();
    }

    private void showFriendRequestList() {
        mBinding.acFriendRequest.setOnClickListener(view -> {
            mainActivity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_left, R.anim.slide_out_left,
                            R.anim.slide_out_right, R.anim.slide_out_right)
                    .replace(
                            mainActivity.getViewContainerID(),
                            new FriendRequestFragment()
                    )
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void showFriendFromContact() {
        mBinding.acPhoneContact.setOnClickListener(view -> {
            mainActivity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_left, R.anim.slide_out_left,
                            R.anim.slide_out_right, R.anim.slide_out_right)
                    .replace(
                            mainActivity.getViewContainerID(),
                            new FriendFromContactFragment()
                    )
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void backToPrevPage() {
        mBinding.iBtnBack.setOnClickListener(view -> {
            mainActivity.setBottomNavVisibility(View.VISIBLE);
            mainActivity.getSupportFragmentManager().popBackStackImmediate();
        });
    }

    private void phoneNumberOnChangeEvent() {
        mBinding.edtPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() > 0) {
                    mBinding.btnFind.setBackgroundColor(ContextCompat.getColor(mainActivity, R.color.primary_color));
                    mBinding.btnFind.setClickable(true);
                } else {
                    mBinding.btnFind.setBackgroundColor(ContextCompat.getColor(mainActivity, R.color.gray_bf));
                    mBinding.btnFind.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void addActionForBtnFind() {
        mBinding.btnFind.setOnClickListener(view -> {
            Toast.makeText(mainActivity, "find", Toast.LENGTH_SHORT).show();
        });
    }
}
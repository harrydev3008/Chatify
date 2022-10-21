package com.hisu.zola.fragments.conversation;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.hisu.zola.R;
import com.hisu.zola.databinding.LayoutChangeFriendNickNameBinding;
import com.hisu.zola.listeners.IOnBottomSheetFragmentButtonClickListener;

public class ChangeNickNameBottomSheetFragment extends BottomSheetDialogFragment {

    private LayoutChangeFriendNickNameBinding mBinding;
    private IOnBottomSheetFragmentButtonClickListener buttonClickListener;

    public ChangeNickNameBottomSheetFragment() {
    }

    public void setButtonClickListener(IOnBottomSheetFragmentButtonClickListener buttonClickListener) {
        this.buttonClickListener = buttonClickListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        mBinding = LayoutChangeFriendNickNameBinding.inflate(
                LayoutInflater.from(getContext()), null, false
        );

        addActionForBtnDismiss();
        addActionForBtnSave();

        dialog.setContentView(mBinding.getRoot());
        dialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);

        return dialog;
    }

    private void addActionForBtnDismiss() {
        mBinding.iBtnDismiss.setOnClickListener(view -> {
            buttonClickListener.dismiss();
        });
    }

    private void addActionForBtnSave() {
        //Todo: call api to update nickname
        mBinding.btnSave.setOnClickListener(view -> {
            if (validateNickname(mBinding.edtNickName.getText().toString())) {
                buttonClickListener.dismiss();
            }
        });
    }

    private boolean validateNickname(String nickname) {
        //Todo: validate nickname => Huy => bắt rỗng, chưa có regex
        if (TextUtils.isEmpty(nickname)) {
            mBinding.edtNickName.setError(getString(R.string.empty_nickname_err));
            mBinding.edtNickName.requestFocus();
            return false;
        }
        return true;
    }
}
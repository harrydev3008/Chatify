package com.hisu.zola.fragments.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.database.repository.UserRepository;
import com.hisu.zola.databinding.FragmentSettingBinding;
import com.hisu.zola.fragments.authenticate.ResetPasswordFragment;
import com.hisu.zola.util.local.LocalDataManager;

public class SettingFragment extends Fragment {

    private FragmentSettingBinding mBinding;
    private MainActivity mainActivity;
    private UserRepository repository;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentSettingBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainActivity.setBottomNavVisibility(View.GONE);
        repository = new UserRepository(mainActivity.getApplication());

        loadUserInfo();
        addActionForBtnBackToPrevPage();
        addActionForBtnLogout();
        addActionForBtnChangePwd();
        addActionForBtnChangePhoneNumber();
    }

    private void loadUserInfo() {
        User localUser = LocalDataManager.getCurrentUserInfo();
        repository.getUser(localUser.getId()).observe(mainActivity, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user == null) return;
                mBinding.tvPhoneNo.setText(user.getPhoneNumber());
            }
        });

    }

    private void addActionForBtnLogout() {
        mBinding.tvLogout.setOnClickListener(view -> {
            new iOSDialogBuilder(mainActivity)
                    .setTitle(getString(R.string.logout))
                    .setSubtitle(getString(R.string.logout_confirm))
                    .setPositiveListener(getString(R.string.logout), dialog -> mainActivity.logOut())
                    .setNegativeListener(getString(R.string.cancel), iOSDialog::dismiss).build().show();
        });
    }

    private void addActionForBtnChangePwd() {
        mBinding.tvChangePassword.setOnClickListener(view -> {
            mainActivity.addFragmentToBackStack(ResetPasswordFragment.newInstance(ResetPasswordFragment.RESET_PWD_ARGS));
        });
    }

    private void addActionForBtnChangePhoneNumber() {
        mBinding.acChangePhoneNumber.setOnClickListener(view -> {
            mainActivity.addFragmentToBackStack(new ConfirmChangePhoneNumberFragment());
        });
    }

    private void addActionForBtnBackToPrevPage() {
        mBinding.iBtnBack.setOnClickListener(view -> {
            mainActivity.setBottomNavVisibility(View.VISIBLE);
            mainActivity.getSupportFragmentManager().popBackStackImmediate();
        });
    }
}
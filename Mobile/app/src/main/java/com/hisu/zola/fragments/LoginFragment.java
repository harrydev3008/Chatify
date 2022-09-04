package com.hisu.zola.fragments;

import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private MainActivity mainActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        mainActivity = (MainActivity) getActivity();

        addChangeBackgroundColorOnFocusForUserNameEditText();
        addChangeBackgroundColorOnFocusForPasswordEditText();

        addToggleShowPasswordEvent();
        addSwitchToRegisterEvent();

        return binding.getRoot();
    }

    private void addChangeBackgroundColorOnFocusForUserNameEditText() {
        binding.edtUsername.setOnFocusChangeListener((view, isFocus) -> {
            if (isFocus)
                binding.edtUsername.setBackground(
                        ContextCompat.getDrawable(mainActivity.getApplicationContext(),
                                R.drawable.edit_text_outline_focus));
            else
                binding.edtUsername.setBackground(
                        ContextCompat.getDrawable(mainActivity.getApplicationContext(),
                                R.drawable.edit_text_outline));
        });
    }

    private void addChangeBackgroundColorOnFocusForPasswordEditText() {
        binding.edtPassword.setOnFocusChangeListener((view, isFocus) -> {
            if (isFocus)
                binding.linearLayout.setBackground(
                        ContextCompat.getDrawable(mainActivity.getApplicationContext(),
                                R.drawable.edit_text_outline_focus));
            else
                binding.linearLayout.setBackground(
                        ContextCompat.getDrawable(mainActivity.getApplicationContext(),
                                R.drawable.edit_text_outline));
        });
    }

    private void addToggleShowPasswordEvent() {
        String showText = getString(R.string.show);
        String hideText = getString(R.string.hide);

        binding.tvTogglePassword.setOnClickListener(view -> {

            if (binding.tvTogglePassword.getText().toString().equalsIgnoreCase(showText)) {
                binding.tvTogglePassword.setText(hideText);
                binding.edtPassword.setTransformationMethod(null);
            } else {
                binding.tvTogglePassword.setText(showText);
                binding.edtPassword.setTransformationMethod(new PasswordTransformationMethod());
            }

            binding.edtPassword.setSelection(binding.edtPassword.getText().length());
        });
    }

    private void addSwitchToRegisterEvent() {
        binding.tvSwitchToRegister.setOnClickListener(view -> {
            mainActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left)
                    .replace(mainActivity.getViewContainerID(), new RegisterFragment())
                    .commit();
        });
    }

    private void addLoginEvent() {
        String username = binding.edtUsername.getText().toString();
        String password = binding.edtPassword.getText().toString();

        if (validateUserAccount(username, password))
            mainActivity.setFragment(new HomeFragment());
    }

    /*
        Todo: Write method to fetch user account info then validate before logging in
     */
    private boolean validateUserAccount(String username, String password) {
        return true;
    }
}
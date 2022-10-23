package com.hisu.zola.fragments.conversation;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.databinding.FragmentSentFilesBinding;

public class SentFilesFragment extends Fragment {

    private FragmentSentFilesBinding mBinding;
    private MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainActivity = (MainActivity) getActivity();
        mBinding = FragmentSentFilesBinding.inflate(inflater, container, false);

        return mBinding.getRoot();
    }
}
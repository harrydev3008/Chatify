package com.hisu.zola.fragments.contact;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hisu.zola.MainActivity;
import com.hisu.zola.adapters.FriendFromContactAdapter;
import com.hisu.zola.databinding.FragmentFriendFromContactBinding;
import com.hisu.zola.database.entity.ContactUser;

import java.util.ArrayList;
import java.util.List;

import jagerfield.mobilecontactslibrary.ImportContactsAsync;

public class FriendFromContactFragment extends Fragment {

    private FragmentFriendFromContactBinding mBinding;
    private MainActivity mainActivity;
    public static final int CONTACT_PERMISSION_CODE = 1;
    private FriendFromContactAdapter adapter;
    private List<ContactUser> contactUsers;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainActivity = (MainActivity) getActivity();
        mBinding = FragmentFriendFromContactBinding.inflate(inflater, container, false);

        init();
        backToPrevPage();
        addActionForBtnSyncFromContact();

        return mBinding.getRoot();
    }

    private void init() {
        contactUsers = new ArrayList<>();
        adapter = new FriendFromContactAdapter();
        mBinding.rvFriendsFromContact.setAdapter(adapter);
        mBinding.rvFriendsFromContact.setLayoutManager(new LinearLayoutManager(mainActivity));
    }

    private void backToPrevPage() {
        mBinding.iBtnBack.setOnClickListener(view -> {
            mainActivity.setBottomNavVisibility(View.VISIBLE);
            mainActivity.getSupportFragmentManager().popBackStackImmediate();
        });
    }

    private void addActionForBtnSyncFromContact() {
        mBinding.iBtnSyncContact.setOnClickListener(view -> {
            if (isReadContactPermissionGranted()) {
                getContacts();
            } else {
                requestReadContactPermission();
            }
        });
    }

    private boolean isReadContactPermissionGranted() {
        return ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestReadContactPermission() {
        String[] permissions = {Manifest.permission.READ_CONTACTS};
        ActivityCompat.requestPermissions(mainActivity, permissions, CONTACT_PERMISSION_CODE);
    }

    public void getContacts() {
        new ImportContactsAsync(mainActivity, contacts -> {
            contactUsers.clear();

//            String res = "";


//            contacts.forEach(contact -> {

//                if (contact.getNumbers() != null) {
//                    if (contact.getNumbers().get(0) != null)
//                        Log.e("test", contact.getDisplaydName() + " - " + contact.getNumbers().get(0));
//                }


//                res += contact.toString();

//
//
//
////                ContactUser contactUser = new ContactUser(
////                        contact.getDisplaydName(),
////                        contact.getNumbers().size()+"!", ""
////                );
////
////                if (contact.getPhotoUri() != null)
////                    contactUser.setAvatar(contact.getPhotoUri());
////                else
////                    contactUser.setImageBitmap(
////                            ImageConvertUtil.createImageFromText(mainActivity,
////                                    150, 150, contactUser.getName())
////                    );
//
////                contactUsers.add(contactUser);
//            });
            Log.e("test", contacts.size() + "!");
//            adapter.setContactUsers(contactUsers);
        }).execute();
    }
}
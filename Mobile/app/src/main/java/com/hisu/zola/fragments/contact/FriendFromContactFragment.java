package com.hisu.zola.fragments.contact;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hisu.zola.MainActivity;
import com.hisu.zola.adapters.FriendFromContactAdapter;
import com.hisu.zola.database.entity.ContactUser;
import com.hisu.zola.databinding.FragmentFriendFromContactBinding;
import com.tomash.androidcontacts.contactgetter.entity.ContactData;
import com.tomash.androidcontacts.contactgetter.main.contactsGetter.ContactsGetterBuilder;

import java.util.ArrayList;
import java.util.List;

public class FriendFromContactFragment extends Fragment {

    private FragmentFriendFromContactBinding mBinding;
    private MainActivity mainActivity;
    public static final int CONTACT_PERMISSION_CODE = 1;
    private FriendFromContactAdapter adapter;
    private List<ContactUser> contactUsers;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentFriendFromContactBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
        backToPrevPage();
        addActionForBtnSyncFromContact();
    }

    private void init() {
        contactUsers = new ArrayList<>();
        adapter = new FriendFromContactAdapter(mainActivity);
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
        List<ContactData> contactDataList = new ContactsGetterBuilder(mainActivity)
                .allFields()
                .buildList();

        for (ContactData contactData : contactDataList)
            if (contactData.getPhoneList().size() != 0)
                contactUsers.add(new ContactUser(contactData.getCompositeName(),
                        contactData.getPhoneList().get(0).getMainData(),
                        contactData.getPhotoUri())
                );

        adapter.setContactUsers(contactUsers);
    }

}
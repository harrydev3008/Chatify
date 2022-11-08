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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.adapters.FriendFromContactAdapter;
import com.hisu.zola.database.entity.ContactUser;
import com.hisu.zola.databinding.FragmentFriendFromContactBinding;
import com.hisu.zola.util.NetworkUtil;
import com.hisu.zola.view_model.ContactUserViewModel;
import com.tomash.androidcontacts.contactgetter.entity.ContactData;
import com.tomash.androidcontacts.contactgetter.main.contactsGetter.ContactsGetterBuilder;

import java.util.ArrayList;
import java.util.List;

public class FriendFromContactFragment extends Fragment {

    private FragmentFriendFromContactBinding mBinding;
    private MainActivity mainActivity;
    public static final int CONTACT_PERMISSION_CODE = 1;
    private FriendFromContactAdapter adapter;
    private ContactUserViewModel viewModel;

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
        adapter = new FriendFromContactAdapter(mainActivity);
        mBinding.rvFriendsFromContact.setAdapter(adapter);

        viewModel = new ViewModelProvider(mainActivity).get(ContactUserViewModel.class);

        viewModel.getData().observe(mainActivity, new Observer<List<ContactUser>>() {
            @Override
            public void onChanged(List<ContactUser> contactUserList) {
                if (contactUserList == null) return;

                adapter.setContactUsers(contactUserList);
            }
        });

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
                if (NetworkUtil.isConnectionAvailable(mainActivity))
                    getContacts();
                else
                    new iOSDialogBuilder(mainActivity)
                            .setTitle(getString(R.string.no_network_connection))
                            .setSubtitle(getString(R.string.no_network_connection_to_sync))
                            .setPositiveListener(getString(R.string.confirm), iOSDialog::dismiss).build().show();
            } else
                requestReadContactPermission();
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
        List<ContactUser> contactUsers = new ArrayList<>();
        List<ContactData> contactDataList = new ContactsGetterBuilder(mainActivity)
                .allFields()
                .buildList();

        for (ContactData contactData : contactDataList)
            if (contactData.getPhoneList().size() != 0) {
                String phoneNumber = contactData.getPhoneList().get(0).getMainData().replaceAll("[^0-9]", "");
                contactUsers.add(new ContactUser(contactData.getPhoneList().get(0).getMainData(), contactData.getCompositeName(),
                        phoneNumber));
            }

        viewModel.insertAll(contactUsers);
    }
}
package com.hisu.zola.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hisu.zola.database.entity.ContactUser;
import com.hisu.zola.databinding.LayoutFriendFromContactBinding;
import com.hisu.zola.util.converter.ImageConvertUtil;

import java.util.List;

public class FriendFromContactAdapter extends
        RecyclerView.Adapter<FriendFromContactAdapter.FriendFromContactViewHolder> {

    private List<ContactUser> contactUsers;
    private final Context context;

    public FriendFromContactAdapter(Context context) {
        this.context = context;
    }

    public void setContactUsers(List<ContactUser> contactUsers) {
        this.contactUsers = contactUsers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FriendFromContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FriendFromContactViewHolder(LayoutFriendFromContactBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull FriendFromContactViewHolder holder, int position) {
        ContactUser contactUser = contactUsers.get(position);
        holder.setContactData(context, contactUser);
    }

    @Override
    public int getItemCount() {
        return contactUsers != null ? contactUsers.size() : 0;
    }

    public static class FriendFromContactViewHolder extends RecyclerView.ViewHolder {

        private final LayoutFriendFromContactBinding binding;

        public FriendFromContactViewHolder(LayoutFriendFromContactBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void setContactData(Context context, ContactUser contactUser) {
//            if (contactUser.getImageUri() != null)
//                binding.imvContactAvatar.setImageURI(contactUser.getImageUri());
//            else {
            Bitmap imageFromText = ImageConvertUtil.createImageFromText(context,
                    150, 150, contactUser.getName());
            binding.imvContactAvatar.setImageBitmap(imageFromText);
//            }

            binding.tvContactName.setText(contactUser.getName());
            binding.tvAppName.setText(contactUser.getPhoneNumber());
        }
    }
}
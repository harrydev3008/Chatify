package com.hisu.zola.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hisu.zola.databinding.LayoutFriendFromContactBinding;
import com.hisu.zola.database.entity.ContactUser;

import java.util.List;

public class FriendFromContactAdapter extends
        RecyclerView.Adapter<FriendFromContactAdapter.FriendFromContactViewHolder> {

    private List<ContactUser> contactUsers;
    private Context context;

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
        holder.setContactData(contactUser);
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

        private void setContactData(ContactUser contactUser) {
            if (!contactUser.getAvatar().isEmpty())
                binding.imvContactAvatar.setImageURI(Uri.parse(contactUser.getAvatar()));
            else
                binding.imvContactAvatar.setImageBitmap(contactUser.getImageBitmap());

            binding.tvContactName.setText(contactUser.getName());
            binding.tvAppName.setText(contactUser.getPhoneNumber());
        }
    }
}
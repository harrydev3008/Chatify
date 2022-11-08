package com.hisu.zola.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hisu.zola.R;
import com.hisu.zola.database.entity.ContactUser;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.databinding.LayoutFriendFromContactBinding;
import com.hisu.zola.util.converter.ImageConvertUtil;
import com.hisu.zola.util.local.LocalDataManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FriendFromContactAdapter extends
        RecyclerView.Adapter<FriendFromContactAdapter.FriendFromContactViewHolder> {

    private List<ContactUser> contactUsers;
    private final Context context;

    public FriendFromContactAdapter(Context context) {
        setHasStableIds(true);
        this.context = context;
        contactUsers = new ArrayList<>();
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

        List<User> friends = LocalDataManager.getCurrentUserInfo().getFriends();

        Map<String, User> temp = friends.stream().collect(Collectors.toMap(User::getPhoneNumber, user -> user));

        for (ContactUser contact : contactUsers) {
            if (temp.containsKey(contact.getPhoneNumber())) {
                holder.binding.btnIsFriend.setText(context.getString(R.string.is_a_friend));
                holder.binding.btnIsFriend.setTextColor(ContextCompat.getColor(context, R.color.gray));
                holder.binding.btnIsFriend.setBackground(null);
            }
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
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
            if (contactUser.getAvatarURL() == null) {
                Bitmap imageFromText = ImageConvertUtil.createImageFromText(context,
                        150, 150, contactUser.getUsername());
                binding.imvContactAvatar.setImageBitmap(imageFromText);
            }

            binding.tvContactName.setText(contactUser.getUsername());
            binding.tvAppName.setText(contactUser.getPhoneNumber());
        }
    }
}
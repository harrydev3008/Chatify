package com.hisu.zola.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.hisu.zola.R;
import com.hisu.zola.database.entity.ContactUser;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.databinding.LayoutFriendFromContactBinding;
import com.hisu.zola.listeners.IOnContractUserClickListener;
import com.hisu.zola.listeners.IOnUserClickListener;
import com.hisu.zola.util.converter.ImageConvertUtil;
import com.hisu.zola.util.local.LocalDataManager;

import java.util.ArrayList;
import java.util.List;

public class FriendFromContactAdapter extends
        RecyclerView.Adapter<FriendFromContactAdapter.FriendFromContactViewHolder> {

    private List<ContactUser> contactUsers;
    private final Context context;
    private IOnContractUserClickListener onContractUserClickListener;

    public FriendFromContactAdapter(Context context) {
        setHasStableIds(true);
        this.context = context;
        contactUsers = new ArrayList<>();
    }

    public void setContactUsers(List<ContactUser> contactUsers) {
        this.contactUsers = contactUsers;
        notifyDataSetChanged();
    }

    public void setOnContractUserClickListener(IOnContractUserClickListener onContractUserClickListener) {
        this.onContractUserClickListener = onContractUserClickListener;
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

        if (contactUser.isFriend()) {
            holder.binding.btnIsFriend.setText(context.getString(R.string.is_a_friend));
            holder.binding.btnIsFriend.setTextColor(ContextCompat.getColor(context, R.color.gray));
            holder.binding.btnIsFriend.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.white));
            String textPlaceHolder = context.getString(R.string.name_in_app) + " " + contactUser.getAppName();
            holder.binding.tvAppName.setText(textPlaceHolder);
        } else {
            holder.binding.btnIsFriend.setText(context.getString(R.string.not_a_friend));
            holder.binding.btnIsFriend.setTextColor(ContextCompat.getColor(context, R.color.primary_color));
            holder.binding.btnIsFriend.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.lightBlue));
            String textPlaceHolder = context.getString(R.string.name_in_app) + " " + contactUser.getAppName();
            holder.binding.tvAppName.setText(textPlaceHolder);

            if (!isSentOrReceive(contactUser.get_id()))
                holder.binding.btnIsFriend.setOnClickListener(view -> {
                    onContractUserClickListener.onClick(contactUser);
                });
            else {
                holder.binding.btnIsFriend.setOnClickListener(view -> {
                    Toast.makeText(context, "ok not send", Toast.LENGTH_SHORT).show();
                });
            }
        }
    }

    private boolean isSentOrReceive(String id) {
        User currentUserInfo = LocalDataManager.getCurrentUserInfo();
        for (User user : currentUserInfo.getSendRequestQueue()) {
            if (user.getId().equalsIgnoreCase(id))
                return true;
        }

        for (User user : currentUserInfo.getFriendsQueue()) {
            if (user.getId().equalsIgnoreCase(id))
                return true;
        }

        return false;
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
            if (contactUser.getAvatarURL() == null || contactUser.getAvatarURL().isEmpty()) {
                Bitmap imageFromText = ImageConvertUtil.createImageFromText(context,
                        150, 150, contactUser.getUsername());
                binding.imvContactAvatar.setImageBitmap(imageFromText);
            } else {
                Glide.with(context).asBitmap()
                        .load(contactUser.getAvatarURL())
                        .placeholder(AppCompatResources.getDrawable(context, R.drawable.ic_img_place_holder))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                binding.imvContactAvatar.setImageBitmap(resource);
                                binding.imvContactAvatar.setVisibility(View.VISIBLE);
                            }
                        });
            }

            binding.tvContactName.setText(contactUser.getUsername());
        }
    }
}
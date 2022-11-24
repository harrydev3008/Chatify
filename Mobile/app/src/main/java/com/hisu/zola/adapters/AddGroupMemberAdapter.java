package com.hisu.zola.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.hisu.zola.R;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.databinding.LayoutAddMemberBinding;
import com.hisu.zola.listeners.IOnItemCheckedChangListener;

import java.util.List;

public class AddGroupMemberAdapter extends RecyclerView.Adapter<AddGroupMemberAdapter.AddGroupMemberViewHolder> {

    private List<User> friends;
    private Context context;
    private IOnItemCheckedChangListener onItemCheckedChangListener;

    public AddGroupMemberAdapter(List<User> friends, Context context) {
        this.friends = friends;
        this.context = context;
        notifyDataSetChanged();
    }

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
        notifyDataSetChanged();
    }

    public void setOnItemCheckedChangListener(IOnItemCheckedChangListener onItemCheckedChangListener) {
        this.onItemCheckedChangListener = onItemCheckedChangListener;
    }

    @NonNull
    @Override
    public AddGroupMemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AddGroupMemberViewHolder(
                LayoutAddMemberBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent, false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull AddGroupMemberViewHolder holder, int position) {
        User friend = friends.get(position);
        Glide.with(context)
                .asBitmap().load(friend.getAvatarURL())
                .placeholder(AppCompatResources.getDrawable(context, R.drawable.ic_img_place_holder))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        holder.binding.cimvFriendAvatar.setImageBitmap(resource);
                        holder.binding.cimvFriendAvatar.setVisibility(View.VISIBLE);
                    }
                });
        holder.binding.tvFriendName.setText(friend.getUsername());
        holder.binding.cbSelect.setOnCheckedChangeListener((compoundButton, isCheck) -> {
            onItemCheckedChangListener.itemCheck(friend, isCheck);
        });
    }

    @Override
    public int getItemCount() {
        return friends != null ? friends.size() : 0;
    }

    public static class AddGroupMemberViewHolder extends RecyclerView.ViewHolder {

        private LayoutAddMemberBinding binding;

        public AddGroupMemberViewHolder(@NonNull LayoutAddMemberBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
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
import com.hisu.zola.databinding.LayoutViewMemberBinding;
import com.hisu.zola.listeners.IOnRemoveUserListener;
import com.hisu.zola.util.local.LocalDataManager;

import java.util.List;

public class ViewFriendAdapter extends RecyclerView.Adapter<ViewFriendAdapter.ViewFriendViewHolder> {

    private List<User> members;
    private Context context;
    private IOnRemoveUserListener onRemoveUserListener;
    private boolean isAdmin;
    private User admin;

    public ViewFriendAdapter(Context context) {
        this.context = context;
        isAdmin = false;
    }

    public void setOnRemoveUserListener(IOnRemoveUserListener onRemoveUserListener) {
        this.onRemoveUserListener = onRemoveUserListener;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public void setMembers(List<User> members) {
        this.members = members;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewFriendViewHolder(
                LayoutViewMemberBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent, false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewFriendViewHolder holder, int position) {
        User member = members.get(position);

        Glide.with(context).asBitmap().load(member.getAvatarURL())
                .placeholder(AppCompatResources.getDrawable(context, R.drawable.ic_img_place_holder))
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        holder.binding.imageView4.setImageBitmap(resource);
                        holder.binding.imageView4.setVisibility(View.VISIBLE);
                    }
                });
        holder.binding.tvMemberName.setText(member.getUsername());

        if (isAdmin) {
            holder.binding.iBtnRemove.setVisibility(View.VISIBLE);
            holder.binding.iBtnRemove.setOnClickListener(view -> onRemoveUserListener.removeUser(member));
        } else {
            holder.binding.iBtnRemove.setVisibility(View.GONE);
        }

        if (member.getId().equalsIgnoreCase(admin.getId())) {
            holder.binding.tvRole.setVisibility(View.VISIBLE);
            holder.binding.iBtnRemove.setVisibility(View.GONE);

            if (member.getId().equalsIgnoreCase(LocalDataManager.getCurrentUserInfo().getId()))
                holder.binding.tvMemberName.setText(context.getText(R.string.user));
            else

                holder.binding.tvMemberName.setText(member.getUsername());

        } else {
            holder.binding.tvRole.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return members != null ? members.size() : 0;
    }

    public static class ViewFriendViewHolder extends RecyclerView.ViewHolder {

        private final LayoutViewMemberBinding binding;

        public ViewFriendViewHolder(@NonNull LayoutViewMemberBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
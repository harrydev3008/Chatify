package com.hisu.zola.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.databinding.LayoutChangeAdminBinding;
import com.hisu.zola.listeners.IOnRemoveUserListener;
import com.hisu.zola.util.local.LocalDataManager;

import java.util.List;

public class ChangeAdminAdapter extends RecyclerView.Adapter<ChangeAdminAdapter.ChangeAdminViewHolder> {

    private List<User> members;
    private Context context;
    private IOnRemoveUserListener onRemoveUserListener;

    public ChangeAdminAdapter(Context context) {
        this.context = context;
    }

    public void setMembers(List<User> members) {
        this.members = members;
        notifyDataSetChanged();
    }

    public void setOnRemoveUserListener(IOnRemoveUserListener onRemoveUserListener) {
        this.onRemoveUserListener = onRemoveUserListener;
    }

    @NonNull
    @Override
    public ChangeAdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChangeAdminViewHolder(
                LayoutChangeAdminBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent, false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ChangeAdminViewHolder holder, int position) {
        User member = members.get(position);
        Glide.with(context).load(member.getAvatarURL()).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(holder.binding.imageView4);
        holder.binding.tvMemberName.setText(member.getUsername());

        if(!member.getId().equalsIgnoreCase(LocalDataManager.getCurrentUserInfo().getId())) {
            holder.binding.iBtnChange.setVisibility(View.VISIBLE);
            holder.binding.iBtnChange.setOnClickListener(view -> {
                onRemoveUserListener.removeUser(member);
            });
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ChangeAdminViewHolder extends RecyclerView.ViewHolder {

        private LayoutChangeAdminBinding binding;

        public ChangeAdminViewHolder(@NonNull LayoutChangeAdminBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
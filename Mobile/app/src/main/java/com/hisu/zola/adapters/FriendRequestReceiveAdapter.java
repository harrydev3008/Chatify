package com.hisu.zola.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.databinding.LayoutFriendRequestReceiveBinding;

import java.util.List;

public class FriendRequestReceiveAdapter extends
        RecyclerView.Adapter<FriendRequestReceiveAdapter.RequestReceiveViewHolder> {

    private List<User> requestList;
    private final Context context;

    public FriendRequestReceiveAdapter(List<User> requestList, Context context) {
        this.requestList = requestList;
        this.context = context;
        notifyDataSetChanged();
    }

    public List<User> getRequestList() {
        return requestList;
    }

    public void setRequestList(List<User> requestList) {
        this.requestList = requestList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RequestReceiveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RequestReceiveViewHolder(
                LayoutFriendRequestReceiveBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent, false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull RequestReceiveViewHolder holder, int position) {
        User friendRequest = requestList.get(position);
        holder.mBinding.tvRequestName.setText(friendRequest.getUsername());
        Glide.with(context).load(friendRequest.getAvatarURL()).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(holder.mBinding.cimvRequestAvatar);
    }

    @Override
    public int getItemCount() {
        return requestList != null ? requestList.size() : 0;
    }

    public static class RequestReceiveViewHolder extends RecyclerView.ViewHolder {

        private final LayoutFriendRequestReceiveBinding mBinding;

        public RequestReceiveViewHolder(@NonNull LayoutFriendRequestReceiveBinding mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;
        }
    }
}
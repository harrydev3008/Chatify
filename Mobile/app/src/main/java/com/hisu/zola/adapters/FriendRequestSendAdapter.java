package com.hisu.zola.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.databinding.LayoutFriendRequestSendBinding;
import com.hisu.zola.listeners.IOnUserClickListener;

import java.util.List;

public class FriendRequestSendAdapter extends
        RecyclerView.Adapter<FriendRequestSendAdapter.RequestSendViewHolder> {

    private List<User> requestList;
    private final Context context;
    private IOnUserClickListener onUserClickListener;

    public FriendRequestSendAdapter(List<User> requestList, Context context) {
        this.requestList = requestList;
        this.context = context;
        notifyDataSetChanged();
    }

    public FriendRequestSendAdapter(Context context) {
        this.context = context;
    }

    public List<User> getRequestList() {
        return requestList;
    }

    public void setRequestList(List<User> requestList) {
        this.requestList = requestList;
        notifyDataSetChanged();
    }

    public void setOnUserClickListener(IOnUserClickListener onUserClickListener) {
        this.onUserClickListener = onUserClickListener;
    }

    @NonNull
    @Override
    public RequestSendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RequestSendViewHolder(
                LayoutFriendRequestSendBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent, false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull RequestSendViewHolder holder, int position) {
        User sendReq = requestList.get(position);

        Glide.with(context).asBitmap().load(sendReq.getAvatarURL()).diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        holder.mBinding.cimvRequestAvatar.setImageBitmap(resource);
                        holder.mBinding.cimvRequestAvatar.setVisibility(View.VISIBLE);
                    }
                });

        holder.mBinding.tvRequestName.setText(sendReq.getUsername());
        holder.mBinding.btnCancel.setOnClickListener(view -> {
            onUserClickListener.onClick(sendReq);
        });
    }

    @Override
    public int getItemCount() {
        return requestList != null ? requestList.size() : 0;
    }

    public static class RequestSendViewHolder extends RecyclerView.ViewHolder {

        private final LayoutFriendRequestSendBinding mBinding;

        public RequestSendViewHolder(@NonNull LayoutFriendRequestSendBinding mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;
        }
    }
}
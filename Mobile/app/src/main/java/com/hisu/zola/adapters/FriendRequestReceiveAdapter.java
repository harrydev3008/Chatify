package com.hisu.zola.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.databinding.LayoutFriendRequestReceiveBinding;
import com.hisu.zola.listeners.IOnUserClickListener;

import java.util.List;

public class FriendRequestReceiveAdapter extends
        RecyclerView.Adapter<FriendRequestReceiveAdapter.RequestReceiveViewHolder> {

    private List<User> requestList;
    private final Context context;
    private IOnUserClickListener acceptClickListener, denyClickListener;

    public FriendRequestReceiveAdapter(Context context) {
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

    public void setAcceptClickListener(IOnUserClickListener acceptClickListener) {
        this.acceptClickListener = acceptClickListener;
    }

    public void setDenyClickListener(IOnUserClickListener denyClickListener) {
        this.denyClickListener = denyClickListener;
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
        Glide.with(context).asBitmap().load(friendRequest.getAvatarURL()).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        holder.mBinding.cimvRequestAvatar.setImageBitmap(resource);
                        holder.mBinding.cimvRequestAvatar.setVisibility(View.VISIBLE);
                    }
                });

        holder.mBinding.btnAccept.setOnClickListener(view -> acceptClickListener.onClick(friendRequest));
        holder.mBinding.btnDecline.setOnClickListener(view -> denyClickListener.onClick(friendRequest));
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
package com.hisu.zola.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hisu.zola.databinding.LayoutFriendRequestReceiveBinding;

import java.util.List;

public class FriendRequestReceiveAdapter extends
        RecyclerView.Adapter<FriendRequestReceiveAdapter.RequestReceiveViewHolder> {

    private List<String> requestList;
    private Context context;

    public FriendRequestReceiveAdapter(List<String> requestList, Context context) {
        this.requestList = requestList;
        this.context = context;
        notifyDataSetChanged();
    }

    public List<String> getRequestList() {
        return requestList;
    }

    public void setRequestList(List<String> requestList) {
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
        holder.mBinding.tvRequestName.setText(requestList.get(position));
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
package com.hisu.zola.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hisu.zola.databinding.LayoutFriendRequestSendBinding;

import java.util.List;

public class FriendRequestSendAdapter extends
        RecyclerView.Adapter<FriendRequestSendAdapter.RequestSendViewHolder> {

    private List<String> requestList;
    private Context context;

    public FriendRequestSendAdapter(List<String> requestList, Context context) {
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
    public RequestSendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RequestSendViewHolder(
                LayoutFriendRequestSendBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent, false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull RequestSendViewHolder holder, int position) {
        holder.mBinding.tvRequestName.setText(requestList.get(position));
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
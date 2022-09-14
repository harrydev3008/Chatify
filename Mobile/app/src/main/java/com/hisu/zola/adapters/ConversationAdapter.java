package com.hisu.zola.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hisu.zola.R;
import com.hisu.zola.databinding.LayoutConversationBinding;
import com.hisu.zola.entity.ConversationHolder;
import com.hisu.zola.listeners.IOnConversationItemSelectedListener;

import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {

    private List<ConversationHolder> mConversations;
    private Context mContext;
    private IOnConversationItemSelectedListener onConversationItemSelectedListener;

    public void setOnConversationItemSelectedListener(IOnConversationItemSelectedListener onConversationItemSelectedListener) {
        this.onConversationItemSelectedListener = onConversationItemSelectedListener;
    }

    public ConversationAdapter(List<ConversationHolder> mConversations, Context mContext) {
        this.mConversations = mConversations;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversationViewHolder(
                LayoutConversationBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent, false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        ConversationHolder conversation = mConversations.get(position);

        holder.binding.ivConversationCoverPhoto.setImageResource(conversation.getCoverPhoto());
        holder.binding.tvConversationName.setText(conversation.getName());
        holder.binding.tvLastMsg.setText(conversation.getLastMessage());

        int unreadMsgQuantity = conversation.getUnreadMessages();

        if(unreadMsgQuantity > 0) {
            holder.binding.tvUnreadMsgQuantity.setVisibility(View.VISIBLE);
            holder.binding.tvUnreadMsgQuantity.setText(String.valueOf(unreadMsgQuantity));
            holder.binding.tvConversationActiveTime.setTextColor(
                    ContextCompat.getColor(mContext, R.color.black)
            );
            holder.binding.tvLastMsg.setTextColor(
                    ContextCompat.getColor(mContext, R.color.black)
            );
        }

        holder.binding.conversationParent.setOnClickListener(view -> {
            onConversationItemSelectedListener.openConversation(conversation.getId());
        });
    }

    @Override
    public int getItemCount() {
        return mConversations != null ? mConversations.size() : 0;
    }

    public static class ConversationViewHolder extends RecyclerView.ViewHolder {

        private LayoutConversationBinding binding;

        public ConversationViewHolder(@NonNull LayoutConversationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
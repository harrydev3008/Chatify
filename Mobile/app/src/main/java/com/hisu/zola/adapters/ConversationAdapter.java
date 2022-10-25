package com.hisu.zola.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hisu.zola.R;
import com.hisu.zola.databinding.LayoutConversationBinding;
import com.hisu.zola.entity.Conversation;
import com.hisu.zola.entity.ConversationHolder;
import com.hisu.zola.entity.User;
import com.hisu.zola.listeners.IOnConversationItemSelectedListener;
import com.hisu.zola.util.local.LocalDataManager;

import java.util.List;
import java.util.stream.Collectors;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {

    private List<Conversation> conversations;
    private Context mContext;
    private IOnConversationItemSelectedListener onConversationItemSelectedListener;

    public void setOnConversationItemSelectedListener(IOnConversationItemSelectedListener onConversationItemSelectedListener) {
        this.onConversationItemSelectedListener = onConversationItemSelectedListener;
    }

    public ConversationAdapter(List<Conversation> conversations, Context mContext) {
        this.conversations = conversations;
        this.mContext = mContext;
    }

    public ConversationAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setConversations(List<Conversation> conversations) {
        this.conversations = conversations;
        notifyDataSetChanged();
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
        Conversation conversation = conversations.get(position);

        User conUser = getConversationAvatar(conversation.getMember());

        if(conversation.getLabel() == null) {
            Glide.with(mContext).load(conUser.getAvatarURL()).into(holder.binding.ivConversationCoverPhoto);
            holder.binding.tvConversationName.setText(conUser.getUsername());
        }
        else {
            holder.binding.tvConversationName.setText(conversation.getLabel());
        }

        holder.binding.tvLastMsg.setText(conUser.getUsername());

        int unreadMsgQuantity = 0;

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
            if(conversation.getLabel() != null)
                onConversationItemSelectedListener.openConversation(conversation.getId(),conversation.getLabel());
            else
                onConversationItemSelectedListener.openConversation(conversation.getId(), conUser.getUsername());
        });
    }

    private User getConversationAvatar(List<User> members) {
        User currentUser = LocalDataManager.getCurrentUserInfo();
        for (User member : members) {
            if(!member.getId().equalsIgnoreCase(currentUser.getId()))
                return member;
        }
        return currentUser;
    }

    @Override
    public int getItemCount() {
        return conversations != null ? conversations.size() : 0;
    }

    public static class ConversationViewHolder extends RecyclerView.ViewHolder {

        private LayoutConversationBinding binding;

        public ConversationViewHolder(@NonNull LayoutConversationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
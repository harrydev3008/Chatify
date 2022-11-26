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
import com.hisu.zola.database.entity.Conversation;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.databinding.LayoutForwardMessageBinding;
import com.hisu.zola.listeners.IOnForwardMessageCheckChangeListener;
import com.hisu.zola.util.converter.ImageConvertUtil;
import com.hisu.zola.util.local.LocalDataManager;

import java.util.List;

public class ForwardMessageAdapter extends RecyclerView.Adapter<ForwardMessageAdapter.ForwardMessageViewHolder> {

    private List<Conversation> conversations;
    private final Context context;
    private IOnForwardMessageCheckChangeListener onItemCheckedChangListener;

    public void setOnItemCheckedChangListener(IOnForwardMessageCheckChangeListener onItemCheckedChangListener) {
        this.onItemCheckedChangListener = onItemCheckedChangListener;
    }

    public ForwardMessageAdapter(Context context) {
        setHasStableIds(true);
        this.context = context;
    }

    public void setConversations(List<Conversation> conversations) {
        this.conversations = conversations;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ForwardMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ForwardMessageViewHolder(
                LayoutForwardMessageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ForwardMessageViewHolder holder, int position) {

        Conversation conversation = conversations.get(position);

        if (conversation.getGroup()) {
            holder.binding.tvName.setText(conversation.getLabel());
            holder.binding.cimvAvatar.setImageBitmap(ImageConvertUtil.createImageFromText(context, 150, 150, conversation.getLabel()));
        } else {
            User user = getConversationUser(conversation.getMember());
            holder.binding.tvName.setText(user.getUsername());
            Glide.with(context).asBitmap().load(user.getAvatarURL())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            holder.binding.cimvAvatar.setImageBitmap(resource);
                            holder.binding.cimvAvatar.setVisibility(View.VISIBLE);
                        }
                    });
        }

        holder.binding.cbSelect.setOnCheckedChangeListener((compoundButton, isCheck) -> {
            onItemCheckedChangListener.itemCheck(conversation, isCheck);
        });
    }

    private User getConversationUser(List<User> members) {
        User currentUser = LocalDataManager.getCurrentUserInfo();
        for (User member : members) {
            if (!member.getId().equalsIgnoreCase(currentUser.getId()))
                return member;
        }
        return currentUser;
    }

    @Override
    public int getItemCount() {
        return conversations != null ? conversations.size() : 0;
    }

    public static class ForwardMessageViewHolder extends RecyclerView.ViewHolder {

        private LayoutForwardMessageBinding binding;

        public ForwardMessageViewHolder(@NonNull LayoutForwardMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
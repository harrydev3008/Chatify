package com.hisu.zola.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hisu.zola.R;
import com.hisu.zola.database.entity.Media;
import com.hisu.zola.database.entity.Message;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.databinding.LayoutChatReceiveBinding;
import com.hisu.zola.databinding.LayoutChatSendBinding;
import com.hisu.zola.util.local.LocalDataManager;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int MSG_SEND_TYPE = 0;
    public static final int MSG_RECEIVE_TYPE = 1;

    private List<Message> messages;
    private final Context mContext;
    private boolean isGroup;

    public MessageAdapter(Context mContext) {
        this.mContext = mContext;
        messages = new ArrayList<>();
    }

    public MessageAdapter(List<Message> messages, Context context) {
        this.messages = messages;
        this.mContext = context;
        notifyDataSetChanged();
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> mMessages) {
        this.messages.clear();
        this.messages.addAll(mMessages);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_SEND_TYPE)
            return new MessageSendViewHolder(LayoutChatSendBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false
            ));

        return new MessageReceiveViewHolder(LayoutChatReceiveBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Message message = messages.get(position);

        if (holder.getItemViewType() == MSG_SEND_TYPE) {

            final MessageSendViewHolder sendViewHolder = ((MessageSendViewHolder) holder);
            sendViewHolder.displayMessageContent(mContext, message);

        } else if (holder.getItemViewType() == MSG_RECEIVE_TYPE) {

            final MessageReceiveViewHolder receiveViewHolder = ((MessageReceiveViewHolder) holder);

            Glide.with(mContext).load(message.getSender().getAvatarURL()).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(receiveViewHolder.binding.ivUserPfp);

            if (position == 0)
                receiveViewHolder.binding.ivUserPfp.setVisibility(View.VISIBLE);
            else if (position == messages.size() - 1)
                receiveViewHolder.binding.ivUserPfp.setVisibility(View.VISIBLE);
            else if (messages.get(position - 1).getSender().getId().equalsIgnoreCase(message.getSender().getId())
                    && !messages.get(position + 1).getSender().getId().equalsIgnoreCase(message.getSender().getId()))
                receiveViewHolder.binding.ivUserPfp.setVisibility(View.VISIBLE);
            else if (!messages.get(position - 1).getSender().getId().equalsIgnoreCase(message.getSender().getId())
                    && !messages.get(position + 1).getSender().getId().equalsIgnoreCase(message.getSender().getId()))
                receiveViewHolder.binding.ivUserPfp.setVisibility(View.VISIBLE);
            else
                receiveViewHolder.binding.ivUserPfp.setVisibility(View.INVISIBLE);

            if(isGroup) {
                receiveViewHolder.binding.tvMemberName.setVisibility(View.VISIBLE);
                receiveViewHolder.binding.tvMemberName.setText(message.getSender().getUsername());
            }

            receiveViewHolder.displayMessageContent(mContext, message);
        }
    }

    @Override
    public int getItemViewType(int position) {
        User currentUser = LocalDataManager.getCurrentUserInfo();
        User sender = messages.get(position).getSender();
        return currentUser.getId().equalsIgnoreCase(sender.getId()) ?
                MSG_SEND_TYPE : MSG_RECEIVE_TYPE;
    }

    @Override
    public int getItemCount() {
        return messages != null ? messages.size() : 0;
    }

    public static class MessageSendViewHolder extends RecyclerView.ViewHolder {

        private final LayoutChatSendBinding binding;

        public MessageSendViewHolder(LayoutChatSendBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void displayMessageContent(Context context, Message message) {
            if (message.getDeleted()) {
                binding.imgMsgSend.setVisibility(View.GONE);
                binding.tvMsgSend.setVisibility(View.VISIBLE);
                binding.tvMsgSend.setTextColor(context.getColor(R.color.gray));
                binding.tvMsgSend.setBackground(ContextCompat.getDrawable(context, R.drawable.message_removed));
                binding.tvMsgSend.setText(context.getString(R.string.message_removed));
            } else if (message.getType().equalsIgnoreCase("text")) {
                binding.imgMsgSend.setVisibility(View.GONE);
                binding.tvMsgSend.setVisibility(View.VISIBLE);
                binding.tvMsgSend.setTextColor(context.getColor(R.color.white));
                binding.tvMsgSend.setBackground(ContextCompat.getDrawable(context, R.drawable.message_send));
                binding.tvMsgSend.setText(message.getText());
            } else {
                if (message.getMedia().size() == 1) {
                    binding.tvMsgSend.setVisibility(View.GONE);
                    binding.imgMsgSend.setVisibility(View.VISIBLE);
                    binding.groupImg.setVisibility(View.GONE);
                    Media media = message.getMedia().get(0);
                    Glide.with(context).load(media.getUrl()).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(binding.imgMsgSend);
                } else {
                    binding.tvMsgSend.setVisibility(View.GONE);
                    binding.imgMsgSend.setVisibility(View.GONE);
                    binding.groupImg.setVisibility(View.VISIBLE);
                    binding.groupImg.setLayoutManager(new GridLayoutManager(context, 2));
                    binding.groupImg.setAdapter(new ImageGroupAdapter(message.getMedia(), context));
                }
            }
        }
    }

    public static class MessageReceiveViewHolder extends RecyclerView.ViewHolder {

        private final LayoutChatReceiveBinding binding;

        public MessageReceiveViewHolder(LayoutChatReceiveBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void displayMessageContent(Context context, Message message) {
            if (message.getDeleted()) {
                binding.imgMsgReceive.setVisibility(View.GONE);
                binding.msgWrapper.setVisibility(View.VISIBLE);
                binding.tvMsgReceive.setTextColor(context.getColor(R.color.gray));
                binding.msgWrapper.setBackground(ContextCompat.getDrawable(context, R.drawable.message_removed));
                binding.tvMsgReceive.setText(context.getString(R.string.message_removed));
            } else if (message.getType().equalsIgnoreCase("text")) {
                binding.imgMsgReceive.setVisibility(View.GONE);
                binding.tvMsgReceive.setVisibility(View.VISIBLE);
                binding.tvMsgReceive.setTextColor(context.getColor(R.color.chat_text_color));
                binding.tvMsgReceive.setBackground(ContextCompat.getDrawable(context, R.drawable.message_receive));
                binding.tvMsgReceive.setText(message.getText());
            } else {
                if (message.getMedia().size() == 1) {
                    binding.tvMsgReceive.setVisibility(View.GONE);
                    binding.msgWrapper.setVisibility(View.VISIBLE);
                    binding.groupImg.setVisibility(View.GONE);
                    Media media = message.getMedia().get(0);
                    Glide.with(context).load(media.getUrl()).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(binding.imgMsgReceive);
                } else {
                    binding.msgWrapper.setVisibility(View.GONE);
                    binding.imgMsgReceive.setVisibility(View.GONE);
                    binding.groupImg.setVisibility(View.VISIBLE);
                    binding.groupImg.setLayoutManager(new GridLayoutManager(context, 2));
                    binding.groupImg.setAdapter(new ImageGroupAdapter(message.getMedia(), context));
                }
            }
        }
    }
}
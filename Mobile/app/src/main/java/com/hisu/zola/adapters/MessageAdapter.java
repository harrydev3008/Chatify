package com.hisu.zola.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hisu.zola.databinding.LayoutChatReceiveBinding;
import com.hisu.zola.databinding.LayoutChatSendBinding;
import com.hisu.zola.entity.Message;
import com.hisu.zola.entity.User;
import com.hisu.zola.util.local.LocalDataManager;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int MSG_SEND_TYPE = 0;
    public static final int MSG_RECEIVE_TYPE = 1;

    private List<Message> messages;
    private final Context mContext;

    public MessageAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public MessageAdapter(List<Message> messages, Context context) {
        this.messages = messages;
        this.mContext = context;
        notifyDataSetChanged();
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> mMessages) {
        this.messages = mMessages;
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
            ((MessageSendViewHolder) holder).displayMessageContent(mContext, message);

        } else if (holder.getItemViewType() == MSG_RECEIVE_TYPE) {

            Glide.with(mContext).load(message.getSender().getAvatarURL()).into(((MessageReceiveViewHolder) holder).binding.ivUserPfp);

            if(position == 0)
                ((MessageReceiveViewHolder) holder).binding.ivUserPfp.setVisibility(View.VISIBLE);
            else if (position == messages.size() - 1)
                ((MessageReceiveViewHolder) holder).binding.ivUserPfp.setVisibility(View.VISIBLE);
            else if(messages.get(position - 1).getSender().getId().equalsIgnoreCase(message.getSender().getId())
            && !messages.get(position + 1).getSender().getId().equalsIgnoreCase(message.getSender().getId()))
                ((MessageReceiveViewHolder) holder).binding.ivUserPfp.setVisibility(View.VISIBLE);
            else if(!messages.get(position - 1).getSender().getId().equalsIgnoreCase(message.getSender().getId())
                    && !messages.get(position + 1).getSender().getId().equalsIgnoreCase(message.getSender().getId()))
                ((MessageReceiveViewHolder) holder).binding.ivUserPfp.setVisibility(View.VISIBLE);
            else
                ((MessageReceiveViewHolder) holder).binding.ivUserPfp.setVisibility(View.INVISIBLE);

            ((MessageReceiveViewHolder) holder).displayMessageContent(mContext, message);
        }
    }

    @Override
    public int getItemViewType(int position) {
        User currentUser = LocalDataManager.getCurrentUserInfo();
        return messages.get(position).getSender().getId().equalsIgnoreCase(currentUser.getId()) ?
                MSG_SEND_TYPE : MSG_RECEIVE_TYPE;
    }

    @Override
    public int getItemCount() {
        return messages != null ? messages.size() : 0;
    }

    private static class MessageSendViewHolder extends RecyclerView.ViewHolder {

        private final LayoutChatSendBinding binding;

        public MessageSendViewHolder(LayoutChatSendBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void displayMessageContent(Context context, Message message) {
//            if (message.getType().equalsIgnoreCase("text")) {

            binding.imgMsgSendHolder.setVisibility(View.GONE);
            binding.tvMsgSend.setVisibility(View.VISIBLE);
            binding.tvMsgSend.setText(message.getText());
//            } else {
//                binding.tvMsgSend.setVisibility(View.GONE);
//                binding.imgMsgSendHolder.setVisibility(View.VISIBLE);
//                Glide.with(context).load(message.getText()).into(binding.imgMsgSend);
//            }
        }
    }

    private static class MessageReceiveViewHolder extends RecyclerView.ViewHolder {

        private final LayoutChatReceiveBinding binding;

        public MessageReceiveViewHolder(LayoutChatReceiveBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void displayMessageContent(Context context, Message message) {
//            if (message.getType().equalsIgnoreCase("text")) {

            binding.imgMsgReceiveHolder.setVisibility(View.GONE);
            binding.tvMsgReceive.setVisibility(View.VISIBLE);
            binding.tvMsgReceive.setText(message.getText());
//            } else {
//                binding.tvMsgReceive.setVisibility(View.GONE);
//                binding.imgMsgReceiveHolder.setVisibility(View.VISIBLE);
//                Glide.with(context).load(message.getText()).into(binding.imgMsgReceive);
//            }
        }
    }
}
package com.hisu.zola.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hisu.zola.databinding.LayoutChatReceiveBinding;
import com.hisu.zola.databinding.LayoutChatSendBinding;
import com.hisu.zola.entity.Message;

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

        if(holder.getItemViewType() == MSG_SEND_TYPE)
            ((MessageSendViewHolder) holder).binding.tvMsgSend.setText(message.getContent());
        else if(holder.getItemViewType() == MSG_RECEIVE_TYPE){
//          If multiple messages were from the same user then display cover photo only once
            if(position != 0) {
                if(messages.get(position - 1).getFrom().equalsIgnoreCase(message.getFrom()))
                    ((MessageReceiveViewHolder) holder).binding.ivUserPfp.setVisibility(View.INVISIBLE);
            }

            ((MessageReceiveViewHolder) holder).binding.tvMsgReceive.setText(message.getContent());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getFrom().equalsIgnoreCase("1") ?
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
    }

    private static class MessageReceiveViewHolder extends RecyclerView.ViewHolder {

        private final LayoutChatReceiveBinding binding;

        public MessageReceiveViewHolder(LayoutChatReceiveBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
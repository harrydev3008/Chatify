package com.hisu.zola.adapters;

import android.content.Context;
import android.view.LayoutInflater;
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

    private final List<Message> messages;
    private final Context context;

    public MessageAdapter(List<Message> messages, Context context) {
        this.messages = messages;
        this.context = context;
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

        if(holder.getItemViewType() == MSG_SEND_TYPE) {
            ((MessageSendViewHolder) holder).binding.tvMsgSend.setText(message.getContent());
        }
        else {
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
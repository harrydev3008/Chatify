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

    private List<Message> messages;
    private Context context;

    public MessageAdapter(List<Message> messages, Context context) {
        this.messages = messages;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1)
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

        if(holder.getItemViewType() == 1) {
            ((MessageSendViewHolder) holder).binding.tvMsgSend.setText(message.getContent());
        }
        else {
            ((MessageReceiveViewHolder) holder).binding.tvMsgReceive.setText(message.getContent());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getSender().equalsIgnoreCase("harry") ? 1 : 0;
    }

    @Override
    public int getItemCount() {
        return messages != null ? messages.size() : 0;
    }

    public class MessageSendViewHolder extends RecyclerView.ViewHolder {

        private LayoutChatSendBinding binding;

        public MessageSendViewHolder(LayoutChatSendBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public class MessageReceiveViewHolder extends RecyclerView.ViewHolder {

        private LayoutChatReceiveBinding binding;

        public MessageReceiveViewHolder(LayoutChatReceiveBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
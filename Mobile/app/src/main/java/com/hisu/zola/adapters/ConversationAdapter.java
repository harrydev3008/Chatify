package com.hisu.zola.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hisu.zola.R;
import com.hisu.zola.database.entity.Message;
import com.hisu.zola.databinding.LayoutConversationBinding;
import com.hisu.zola.database.entity.Conversation;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.listeners.IOnConversationItemSelectedListener;
import com.hisu.zola.util.converter.ImageConvertUtil;
import com.hisu.zola.util.converter.TimeConverterUtil;
import com.hisu.zola.util.local.LocalDataManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {

    private List<Conversation> conversations;
    private final Context mContext;
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

        if (conversation.getLabel() == null) {
            Glide.with(mContext).load(conUser.getAvatarURL()).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(holder.binding.ivConversationCoverPhoto);
            holder.binding.tvConversationName.setText(conUser.getUsername());
        } else {
//            holder.binding.ivConversationCoverPhoto.setImageBitmap(ImageConvertUtil.createImageFromText(mContext, 150,150, conversation.getLabel()));
            holder.binding.tvConversationName.setText(conversation.getLabel());
        }

        if (conversation.getLastMessage() != null) {
            Message lastMessage = conversation.getLastMessage();
            if (lastMessage.getSender().getId().equalsIgnoreCase(LocalDataManager.getCurrentUserInfo().getId())) {
                if (lastMessage.getDeleted())
                    holder.binding.tvLastMsg.setText(mContext.getString(R.string.user_message_removed));
                else {
                    if (lastMessage.getType().equalsIgnoreCase("text")) {
                        String textPlaceHolder = "Bạn: " + lastMessage.getText();
                        holder.binding.tvLastMsg.setText(textPlaceHolder);//I wrote like this to avoid warning :)
                    } else {
                        holder.binding.tvLastMsg.setText(mContext.getString(R.string.user_message_sent_image));
                    }
                }
            } else {
//                if(conversation.getLabel() != null) todo: if group -> display sender name
                    holder.binding.tvLastMsg.setText(lastMessage.getText());
            }

            Date date = TimeConverterUtil.getDateFromString(lastMessage.getUpdatedAt());

            if(date != null) {
                Date today = new Date();
                Duration duration = Duration.between(date.toInstant(), today.toInstant());

                if(duration.toDays() < 1) {
                    if(duration.toHours() > 0 && duration.toHours() < 24)
                        holder.binding.tvConversationActiveTime.setText(duration.toHours() + " giờ");
                    else if(duration.toHours() < 1 && (duration.toMinutes() > 0 && duration.toMinutes() < 60))
                        holder.binding.tvConversationActiveTime.setText(duration.toMinutes() + " phút");
                    else if(duration.toMinutes() == 0 && duration.toMinutes() == 0)
                        holder.binding.tvConversationActiveTime.setText(mContext.getString(R.string.just_now));
                } else {
                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM", Locale.ENGLISH);
                    holder.binding.tvConversationActiveTime.setText(outputFormat.format(date));
                }
            }
        }

        int unreadMsgQuantity = 0;

        if (unreadMsgQuantity > 0) {
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
            if (conversation.getLabel() != null)
                onConversationItemSelectedListener.openConversation(conversation, conversation.getLabel());
            else
                onConversationItemSelectedListener.openConversation(conversation, conUser.getUsername());
        });
    }

    private User getConversationAvatar(List<User> members) {
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

    public static class ConversationViewHolder extends RecyclerView.ViewHolder {

        private final LayoutConversationBinding binding;

        public ConversationViewHolder(@NonNull LayoutConversationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
package com.hisu.zola.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.hisu.zola.R;
import com.hisu.zola.database.entity.Conversation;
import com.hisu.zola.database.entity.Message;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.databinding.LayoutConversationBinding;
import com.hisu.zola.listeners.IOnConversationItemSelectedListener;
import com.hisu.zola.util.converter.ImageConvertUtil;
import com.hisu.zola.util.local.LocalDataManager;
import com.hisu.zola.util.network.Constraints;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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

        if (!conversation.getGroup()) {
            if (conUser.getAvatarURL() == null || conUser.getAvatarURL().isEmpty())
                holder.binding.ivConversationCoverPhoto.setImageBitmap(ImageConvertUtil.createImageFromText(mContext, 150, 150, conUser.getUsername()));
            else
                Glide.with(mContext).asBitmap()
                        .load(conUser.getAvatarURL())
                        .placeholder(AppCompatResources.getDrawable(mContext, R.drawable.ic_img_place_holder))
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                holder.binding.ivConversationCoverPhoto.setImageBitmap(resource);
                            }
                        });
            holder.binding.tvConversationName.setText(conUser.getUsername());
        } else {
            holder.binding.ivConversationCoverPhoto.setImageBitmap(ImageConvertUtil.createImageFromText(mContext, 150, 150, conversation.getLabel()));
            holder.binding.tvConversationName.setText(conversation.getLabel());
        }

        if (conversation.getLastMessage() != null) {
            Message lastMessage = conversation.getLastMessage();
            if (lastMessage.getSender().getId().equalsIgnoreCase(LocalDataManager.getCurrentUserInfo().getId())) {
                if (lastMessage.getDeleted())
                    holder.binding.tvLastMsg.setText(mContext.getString(R.string.user_message_removed));
                else {
                    if (lastMessage.getType().contains(Constraints.TEXT_TYPE_GENERAL)) {
                        String textPlaceHolder = "Bạn: " + lastMessage.getText();
                        holder.binding.tvLastMsg.setText(textPlaceHolder);
                    } else if (lastMessage.getType().contains(Constraints.FILE_TYPE_GENERAL)) {
                        holder.binding.tvLastMsg.setText(mContext.getString(R.string.user_message_sent_file));
                    } else if (lastMessage.getType().contains(Constraints.VIDEO_TYPE_GENERAL)) {
                        holder.binding.tvLastMsg.setText(mContext.getString(R.string.user_message_sent_video));
                    } else if (lastMessage.getType().contains(Constraints.CALL_TYPE_GENERAL)) {
                        holder.binding.tvLastMsg.setText(mContext.getString(R.string.out_going_call_holder));
                    } else if (lastMessage.getType().contains(Constraints.GROUP_NOTIFICATION_TYPE_GENERAL)) {
                        holder.binding.tvLastMsg.setText(mContext.getString(R.string.group_notification_holder));
                    } else {
                        holder.binding.tvLastMsg.setText(mContext.getString(R.string.user_message_sent_image));
                    }
                }
            } else {
                if (conversation.getGroup()) {
                    String textPlaceHolder = "";
                    if (lastMessage.getDeleted()) {
                        textPlaceHolder = lastMessage.getSender().getUsername() + ": Đã thu hồi tin nhắn.";
                    } else {
                        if (lastMessage.getType().contains(Constraints.TEXT_TYPE_GENERAL)) {
                            textPlaceHolder = lastMessage.getSender().getUsername() + ": " + lastMessage.getText();
                        } else if (lastMessage.getType().contains(Constraints.FILE_TYPE_GENERAL)) {
                            textPlaceHolder = lastMessage.getSender().getUsername() + ": " + mContext.getString(R.string.user_message_sent_file);
                        } else if (lastMessage.getType().contains(Constraints.VIDEO_TYPE_GENERAL)) {
                            textPlaceHolder = lastMessage.getSender().getUsername() + ": " + mContext.getString(R.string.user_message_sent_video);
                        } else if (lastMessage.getType().contains(Constraints.CALL_TYPE_GENERAL)) {
                            textPlaceHolder = lastMessage.getSender().getUsername() + ": " + mContext.getString(R.string.out_going_call_holder);
                        } else if (lastMessage.getType().contains(Constraints.GROUP_NOTIFICATION_TYPE_GENERAL)) {
                            textPlaceHolder = mContext.getString(R.string.group_notification_holder);
                        } else {
                            textPlaceHolder = lastMessage.getSender().getUsername() + ": " + mContext.getString(R.string.user_message_sent_image);
                        }
                    }
                    holder.binding.tvLastMsg.setText(textPlaceHolder);
                } else {
                    if (lastMessage.getDeleted()) {
                        holder.binding.tvLastMsg.setText(mContext.getString(R.string.message_removed));
                    } else {
                        if (lastMessage.getType().contains(Constraints.TEXT_TYPE_GENERAL)) {
                            String textPlaceHolder = lastMessage.getText();
                            holder.binding.tvLastMsg.setText(textPlaceHolder);
                        } else if (lastMessage.getType().contains(Constraints.FILE_TYPE_GENERAL)) {
                            holder.binding.tvLastMsg.setText(mContext.getString(R.string.user_message_sent_file));
                        } else if (lastMessage.getType().contains(Constraints.VIDEO_TYPE_GENERAL)) {
                            holder.binding.tvLastMsg.setText(mContext.getString(R.string.user_message_sent_video));
                        } else if (lastMessage.getType().contains(Constraints.CALL_TYPE_GENERAL)) {
                            holder.binding.tvLastMsg.setText(mContext.getString(R.string.out_going_call_holder));
                        } else {
                            holder.binding.tvLastMsg.setText(mContext.getString(R.string.user_message_sent_image));
                        }
                    }
                }
            }

            Date date = Date.from(Instant.parse(lastMessage.getCreatedAt()));
            Date today = Calendar.getInstance(TimeZone.getTimeZone("GMT+7")).getTime();

            Duration duration = Duration.between(date.toInstant(), today.toInstant());
            String textPlaceHolder = "";
            if (duration.toDays() < 1) {
                if (duration.toHours() > 0 && duration.toHours() < 24)
                    textPlaceHolder = duration.toHours() + " giờ";
                else if (duration.toHours() < 1 && (duration.toMinutes() > 0 && duration.toMinutes() < 60))
                    textPlaceHolder = duration.toMinutes() + " phút";
                else if (duration.toMinutes() < 1)
                    textPlaceHolder = mContext.getString(R.string.just_now);

                holder.binding.tvConversationActiveTime.setText(textPlaceHolder);
            } else {
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
                outputFormat.setTimeZone(TimeZone.getTimeZone("GMT+7"));
                holder.binding.tvConversationActiveTime.setText(outputFormat.format(date));
            }

        } else {
            holder.binding.tvLastMsg.setText("");
            holder.binding.tvConversationActiveTime.setText("");
        }

        if (conversation.getDisband() != null) {
            if (conversation.getDisband().equalsIgnoreCase("kick"))
                holder.binding.tvLastMsg.setText(R.string.last_msg_kicked);
            else
                holder.binding.tvLastMsg.setText(R.string.last_msg_disbaned);
        }

//        int unreadMsgQuantity = 0;
//
//        if (unreadMsgQuantity > 0) {
//            holder.binding.tvUnreadMsgQuantity.setVisibility(View.VISIBLE);
//            holder.binding.tvUnreadMsgQuantity.setText(String.valueOf(unreadMsgQuantity));
//            holder.binding.tvConversationActiveTime.setTextColor(
//                    ContextCompat.getColor(mContext, R.color.black)
//            );
//            holder.binding.tvLastMsg.setTextColor(
//                    ContextCompat.getColor(mContext, R.color.black)
//            );
//        }

        holder.binding.conversationParent.setOnClickListener(view -> {
            if (conversation.getGroup())
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
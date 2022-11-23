package com.hisu.zola.adapters;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.database.entity.Media;
import com.hisu.zola.database.entity.Message;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.databinding.LayoutChatReceiveBinding;
import com.hisu.zola.databinding.LayoutChatSendBinding;
import com.hisu.zola.listeners.IOnItemTouchListener;
import com.hisu.zola.util.converter.TimeConverterUtil;
import com.hisu.zola.util.local.LocalDataManager;
import com.hisu.zola.util.network.Constraints;
import com.hisu.zola.util.network.NetworkUtil;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int MSG_SEND_TYPE = 0;
    public static final int MSG_RECEIVE_TYPE = 1;

    private final List<Message> messages;
    private final Context mContext;
    private boolean isGroup;
    private IOnItemTouchListener onItemTouchListener;

    public void setOnItemTouchListener(IOnItemTouchListener onItemTouchListener) {
        this.onItemTouchListener = onItemTouchListener;
    }

    public MessageAdapter(Context mContext, Application application) {
        setHasStableIds(true);
        this.mContext = mContext;
        messages = new ArrayList<>();
    }

    public MessageAdapter(List<Message> messages, Context context) {
        setHasStableIds(true);
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

    private boolean isWriteExternalStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return true;
        }

        int write = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int read = ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE);

        return write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED;
    }

    private void requestWriteExternalStoragePermission() {
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions((MainActivity) mContext, permissions, Constraints.STORAGE_PERMISSION_CODE);

    }

    private boolean isFileExisted(Message message) {
        File checkFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                getRealFileName(message));
        return checkFile.exists();
    }

    private String getRealFileName(Message message) {
        String[] fileExtension = message.getText().split("\\.");
        return fileExtension[0] + "_" + message.getId() + "." + fileExtension[1];
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);

        Message message = messages.get(position);

        if (holder.getItemViewType() == MSG_SEND_TYPE) {

            final MessageSendViewHolder sendViewHolder = ((MessageSendViewHolder) holder);
            sendViewHolder.displayMessageContent(mContext, message, onItemTouchListener);

            if (message.getType().contains(Constraints.FILE_TYPE_GENERAL)) {
                sendViewHolder.binding.tvMsgSend.setOnClickListener(view -> {
                    if (isWriteExternalStoragePermission()) {
                        if (!isFileExisted(message)) {
                            NetworkUtil.downloadFile(mContext, message, message.getMedia().get(0).getUrl(), sendViewHolder.binding.tvMsgSend);
                        } else {
                            File checkFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                                    getRealFileName(message));
                            NetworkUtil.openDocument(mContext, checkFile);
                        }
                    } else {
                        requestWriteExternalStoragePermission();
                    }
                });
            }

            if (position == 0) {
                sendViewHolder.binding.tvSentTime.setVisibility(View.VISIBLE);
                sendViewHolder.binding.tvSentTime.setText(TimeConverterUtil.getDateAsString(message.getCreatedAt()));
            } else {
                Date first = TimeConverterUtil.getDateFromString(message.getCreatedAt());
                Date second = TimeConverterUtil.getDateFromString(messages.get(position - 1).getCreatedAt());

                Duration duration = Duration.between(second.toInstant(), first.toInstant());

                if (Math.abs(duration.toHours()) >= 1) {
                    sendViewHolder.binding.tvSentTime.setVisibility(View.VISIBLE);
                    sendViewHolder.binding.tvSentTime.setText(TimeConverterUtil.getDateAsString(message.getCreatedAt()));
                }
            }

            if (!message.getDeleted()) {
                sendViewHolder.binding.tvMsgSend.setOnLongClickListener(view -> {
                    onItemTouchListener.longPress(message, sendViewHolder.binding.tvMsgSend);
                    return true;
                });

                sendViewHolder.binding.videoSend.setOnLongClickListener(view -> {
                    onItemTouchListener.longPress(message, sendViewHolder.binding.videoSend);
                    return true;
                });

                sendViewHolder.binding.groupImg.setOnLongClickListener(view -> {
                    onItemTouchListener.longPress(message, sendViewHolder.binding.groupImg);
                    return true;
                });
            }

        } else if (holder.getItemViewType() == MSG_RECEIVE_TYPE) {

            final MessageReceiveViewHolder receiveViewHolder = ((MessageReceiveViewHolder) holder);

            Glide.with(mContext)
                    .asBitmap().load(message.getSender().getAvatarURL()).diskCacheStrategy(DiskCacheStrategy.ALL).into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            receiveViewHolder.binding.ivUserPfp.setImageBitmap(resource);
                        }
                    });

            if (message.getType().contains(Constraints.FILE_TYPE_GENERAL)) {
                receiveViewHolder.binding.tvMsgReceive.setOnClickListener(view -> {
                    if (isWriteExternalStoragePermission()) {
                        if (!isFileExisted(message)) {
                            NetworkUtil.downloadFile(mContext, message, message.getMedia().get(0).getUrl(), receiveViewHolder.binding.tvMsgReceive);
                        } else {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constraints.GOOGLE_DOCS_URL + message.getMedia().get(0).getUrl()));
                            mContext.startActivity(browserIntent);
                        }
                    } else {
                        requestWriteExternalStoragePermission();
                    }
                });
            }

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

            if (isGroup) {
                receiveViewHolder.binding.tvMemberName.setVisibility(View.VISIBLE);

                if (position == 0)
                    receiveViewHolder.binding.tvMemberName.setText(message.getSender().getUsername());
                else if (position == messages.size() - 1 && !messages.get(position - 1).getSender().getId().equalsIgnoreCase(message.getSender().getId()))
                    receiveViewHolder.binding.tvMemberName.setText(message.getSender().getUsername());
                else if (!messages.get(position - 1).getSender().getId().equalsIgnoreCase(message.getSender().getId())
                        && messages.get(position + 1).getSender().getId().equalsIgnoreCase(message.getSender().getId()))
                    receiveViewHolder.binding.tvMemberName.setText(message.getSender().getUsername());
                else if (!messages.get(position - 1).getSender().getId().equalsIgnoreCase(message.getSender().getId())
                        && !messages.get(position + 1).getSender().getId().equalsIgnoreCase(message.getSender().getId()))
                    receiveViewHolder.binding.tvMemberName.setText(message.getSender().getUsername());
                else
                    receiveViewHolder.binding.tvMemberName.setVisibility(View.GONE);
            }

            if (position == 0) {
                receiveViewHolder.binding.tvReceiveTime.setVisibility(View.VISIBLE);
                receiveViewHolder.binding.tvReceiveTime.setText(TimeConverterUtil.getDateAsString(message.getCreatedAt()));
            } else {
                Date first = TimeConverterUtil.getDateFromString(message.getCreatedAt());
                Date second = TimeConverterUtil.getDateFromString(messages.get(position - 1).getCreatedAt());

                Duration duration = Duration.between(second.toInstant(), first.toInstant());

                if (Math.abs(duration.toHours()) >= 1) {
                    receiveViewHolder.binding.tvReceiveTime.setVisibility(View.VISIBLE);
                    receiveViewHolder.binding.tvReceiveTime.setText(TimeConverterUtil.getDateAsString(message.getCreatedAt()));
                }
            }

            receiveViewHolder.displayMessageContent(mContext, message);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
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

        private void displayMessageContent(Context context, Message message, IOnItemTouchListener onItemTouchListener) {
            binding.tvMsgSend.setTypeface(null, Typeface.NORMAL);
            binding.tvMsgSend.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            if (message.getDeleted()) {
                binding.tvMsgSend.setVisibility(View.VISIBLE);
                binding.videoSend.setVisibility(View.GONE);
                binding.groupImg.setVisibility(View.GONE);
                binding.tvMsgSend.setTextColor(context.getColor(R.color.gray));
                binding.tvMsgSend.setBackground(ContextCompat.getDrawable(context, R.drawable.message_removed));
                binding.tvMsgSend.setText(context.getString(R.string.message_removed));
                binding.tvMsgSend.setTypeface(null, Typeface.ITALIC);
            } else if (message.getType().equalsIgnoreCase(Constraints.TEXT_TYPE_GENERAL)) {
                binding.tvMsgSend.setVisibility(View.VISIBLE);
                binding.videoSend.setVisibility(View.GONE);
                binding.groupImg.setVisibility(View.GONE);
                binding.tvMsgSend.setTextColor(context.getColor(R.color.white));
                binding.tvMsgSend.setBackground(ContextCompat.getDrawable(context, R.drawable.message_send));
                binding.tvMsgSend.setText(message.getText());
            } else if (message.getType().equalsIgnoreCase(Constraints.VIDEO_TYPE_GENERAL)) {
                binding.tvMsgSend.setVisibility(View.GONE);
                binding.groupImg.setVisibility(View.GONE);
                binding.videoSend.setVisibility(View.VISIBLE);

                Uri uri = Uri.parse(message.getMedia().get(0).getUrl());

                ExoPlayer player = new ExoPlayer.Builder(context).build();
                binding.videoSend.setPlayer(player);
                MediaItem mediaItem = MediaItem.fromUri(uri);
                player.setMediaItem(mediaItem);
                player.prepare();
            } else if (message.getType().contains(Constraints.FILE_TYPE_GENERAL)) {
                binding.tvMsgSend.setVisibility(View.VISIBLE);
                binding.videoSend.setVisibility(View.GONE);
                binding.groupImg.setVisibility(View.GONE);

                binding.tvMsgSend.setCompoundDrawablesWithIntrinsicBounds(
                        ContextCompat.getDrawable(context, R.drawable.ic_file_document), null, null, null);

                binding.tvMsgSend.setText(message.getText());
            } else if (message.getType().contains(Constraints.CALL_TYPE_GENERAL)) {
                binding.tvMsgSend.setVisibility(View.VISIBLE);
                binding.videoSend.setVisibility(View.GONE);
                binding.groupImg.setVisibility(View.GONE);
                binding.tvMsgSend.setBackground(ContextCompat.getDrawable(context, R.drawable.message_receive));
                binding.tvMsgSend.setText(context.getString(R.string.out_going_call));
                binding.tvMsgSend.setTextColor(context.getColor(R.color.black));
                binding.tvMsgSend.setCompoundDrawablesWithIntrinsicBounds(
                        ContextCompat.getDrawable(context, R.drawable.ic_incoming_call), null, null, null);
            }else if (message.getType().contains(Constraints.GROUP_NOTIFICATION_TYPE_GENERAL)) {
                binding.tvMsgSend.setVisibility(View.GONE);
                binding.videoSend.setVisibility(View.GONE);
                binding.groupImg.setVisibility(View.GONE);
                binding.tvChatNotification.setVisibility(View.VISIBLE);
                binding.tvChatNotification.setText(message.getText());
            }  else {
                binding.tvMsgSend.setVisibility(View.GONE);
                binding.videoSend.setVisibility(View.GONE);
                binding.groupImg.setVisibility(View.VISIBLE);

                List<Media> media = message.getMedia();

                int spanCount = media.size() < 2 ? 1 : 2;
                ImageGroupAdapter imageGroupAdapter = new ImageGroupAdapter(media, context, ImageGroupAdapter.SEND_MODE);
                imageGroupAdapter.setOnItemTouchListener((message1, view) -> {
                    onItemTouchListener.longPress(message, binding.groupImg);
                });


                binding.groupImg.setLayoutManager(new GridLayoutManager(context, spanCount));
                binding.groupImg.setAdapter(imageGroupAdapter);
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
                binding.msgWrapper.setVisibility(View.VISIBLE);
                binding.groupImg.setVisibility(View.GONE);
                binding.videoReceive.setVisibility(View.GONE);
                binding.tvMsgReceive.setTextColor(context.getColor(R.color.gray));
                binding.msgWrapper.setBackground(ContextCompat.getDrawable(context, R.drawable.message_removed));
                binding.tvMsgReceive.setText(context.getString(R.string.message_removed));
                binding.tvMsgReceive.setTypeface(null, Typeface.ITALIC);
            } else {

                binding.tvMsgReceive.setTypeface(null, Typeface.NORMAL);

                if (message.getType().equalsIgnoreCase(Constraints.TEXT_TYPE_GENERAL)) {
                    binding.msgWrapper.setVisibility(View.VISIBLE);
                    binding.groupImg.setVisibility(View.GONE);
                    binding.videoReceive.setVisibility(View.GONE);
                    binding.tvMsgReceive.setTextColor(context.getColor(R.color.chat_text_color));
                    binding.msgWrapper.setBackground(ContextCompat.getDrawable(context, R.drawable.message_receive));
                    binding.tvMsgReceive.setText(message.getText());
                } else if (message.getType().equalsIgnoreCase(Constraints.VIDEO_TYPE_GENERAL)) {
                    binding.msgWrapper.setVisibility(View.GONE);
                    binding.groupImg.setVisibility(View.GONE);
                    binding.videoReceive.setVisibility(View.VISIBLE);
                    Uri uri = Uri.parse(message.getMedia().get(0).getUrl());

                    ExoPlayer player = new ExoPlayer.Builder(context).build();
                    binding.videoReceive.setPlayer(player);
                    MediaItem mediaItem = MediaItem.fromUri(uri);
                    player.setMediaItem(mediaItem);
                    player.prepare();
                } else if (message.getType().contains(Constraints.FILE_TYPE_GENERAL)) {
                    binding.msgWrapper.setVisibility(View.VISIBLE);
                    binding.groupImg.setVisibility(View.GONE);
                    binding.videoReceive.setVisibility(View.GONE);

                    binding.tvMsgReceive.setCompoundDrawablesWithIntrinsicBounds(
                            ContextCompat.getDrawable(context, R.drawable.ic_file_document_receive), null, null, null);

                    binding.tvMsgReceive.setText(message.getText());
                } else if (message.getType().contains(Constraints.CALL_TYPE_GENERAL)) {
                    binding.msgWrapper.setVisibility(View.VISIBLE);
                    binding.groupImg.setVisibility(View.GONE);
                    binding.videoReceive.setVisibility(View.GONE);
                    binding.tvMsgReceive.setTextColor(context.getColor(R.color.chat_text_color));
                    binding.msgWrapper.setBackground(ContextCompat.getDrawable(context, R.drawable.message_receive));

                    binding.tvMsgReceive.setText(context.getString(R.string.incoming_call));
                    binding.tvMsgReceive.setCompoundDrawablesWithIntrinsicBounds(
                            ContextCompat.getDrawable(context, R.drawable.ic_incoming_call), null, null, null);
                } else if (message.getType().contains(Constraints.GROUP_NOTIFICATION_TYPE_GENERAL)) {
                    binding.msgWrapper.setVisibility(View.GONE);
                    binding.groupImg.setVisibility(View.GONE);
                    binding.videoReceive.setVisibility(View.GONE);
                    binding.tvChatNotification.setVisibility(View.VISIBLE);
                    binding.tvChatNotification.setText(message.getText());
                } else {
                    binding.msgWrapper.setVisibility(View.GONE);
                    binding.groupImg.setVisibility(View.VISIBLE);
                    binding.videoReceive.setVisibility(View.GONE);

                    List<Media> media = message.getMedia();

                    int spanCount = media.size() < 2 ? 1 : 2;

                    binding.groupImg.setLayoutManager(new GridLayoutManager(context, spanCount));
                    binding.groupImg.setAdapter(new ImageGroupAdapter(media, context, ImageGroupAdapter.RECEIVE_MODE));
                }
            }
        }
    }
}
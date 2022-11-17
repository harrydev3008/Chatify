package com.hisu.zola.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.hisu.zola.R;
import com.hisu.zola.database.entity.Media;
import com.hisu.zola.database.entity.Message;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.databinding.LayoutChatReceiveBinding;
import com.hisu.zola.databinding.LayoutChatSendBinding;
import com.hisu.zola.listeners.IOnItemTouchListener;
import com.hisu.zola.util.local.LocalDataManager;

import java.io.File;
import java.util.ArrayList;
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

    public MessageAdapter(Context mContext) {
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

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        Message message = messages.get(position);

        if (holder.getItemViewType() == MSG_SEND_TYPE) {

            final MessageSendViewHolder sendViewHolder = ((MessageSendViewHolder) holder);
            sendViewHolder.displayMessageContent(mContext, message);

            if (!message.getDeleted())
                sendViewHolder.binding.msgSendParent.setOnLongClickListener(view -> {
                    onItemTouchListener.longPress(message, sendViewHolder.binding.msgSendParent);
                    return false;
                });

        } else if (holder.getItemViewType() == MSG_RECEIVE_TYPE) {

            final MessageReceiveViewHolder receiveViewHolder = ((MessageReceiveViewHolder) holder);

            Glide.with(mContext)
                    .asBitmap().load(message.getSender().getAvatarURL()).diskCacheStrategy(DiskCacheStrategy.ALL).into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            receiveViewHolder.binding.ivUserPfp.setImageBitmap(resource);
                        }
                    });

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

        private void displayMessageContent(Context context, Message message) {
            binding.tvMsgSend.setTypeface(null, Typeface.NORMAL);
            if (message.getDeleted()) {
                binding.tvMsgSend.setVisibility(View.VISIBLE);
                binding.videoSend.setVisibility(View.GONE);
                binding.groupImg.setVisibility(View.GONE);
                binding.tvMsgSend.setTextColor(context.getColor(R.color.gray));
                binding.tvMsgSend.setBackground(ContextCompat.getDrawable(context, R.drawable.message_removed));
                binding.tvMsgSend.setText(context.getString(R.string.message_removed));
                binding.tvMsgSend.setTypeface(null, Typeface.ITALIC);
            } else if (message.getType().equalsIgnoreCase("text")) {
                binding.tvMsgSend.setVisibility(View.VISIBLE);
                binding.videoSend.setVisibility(View.GONE);
                binding.groupImg.setVisibility(View.GONE);
                binding.tvMsgSend.setTextColor(context.getColor(R.color.white));
                binding.tvMsgSend.setBackground(ContextCompat.getDrawable(context, R.drawable.message_send));
                binding.tvMsgSend.setText(message.getText());
            } else if (message.getType().equalsIgnoreCase("video")) {
                binding.tvMsgSend.setVisibility(View.GONE);
                binding.groupImg.setVisibility(View.GONE);
                binding.videoSend.setVisibility(View.VISIBLE);

                Uri uri = Uri.parse(message.getMedia().get(0).getUrl());

                ExoPlayer player = new ExoPlayer.Builder(context).build();
                binding.videoSend.setPlayer(player);
                MediaItem mediaItem = MediaItem.fromUri(uri);
                player.setMediaItem(mediaItem);
                player.prepare();
            } else {
                binding.tvMsgSend.setVisibility(View.GONE);
                binding.videoSend.setVisibility(View.GONE);
                binding.groupImg.setVisibility(View.VISIBLE);

                List<Media> media = message.getMedia();

                int spanCount = media.size() < 2 ? 1 : 2;

                binding.groupImg.setLayoutManager(new GridLayoutManager(context, spanCount));
                binding.groupImg.setAdapter(new ImageGroupAdapter(media, context, ImageGroupAdapter.SEND_MODE));
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

                if (message.getType().equalsIgnoreCase("text")) {
                    binding.msgWrapper.setVisibility(View.VISIBLE);
                    binding.groupImg.setVisibility(View.GONE);
                    binding.videoReceive.setVisibility(View.GONE);
                    binding.tvMsgReceive.setTextColor(context.getColor(R.color.chat_text_color));
                    binding.msgWrapper.setBackground(ContextCompat.getDrawable(context, R.drawable.message_receive));
                    binding.tvMsgReceive.setText(message.getText());
                } else if (message.getType().equalsIgnoreCase("video")) { //todo: change later
                    binding.msgWrapper.setVisibility(View.GONE);
                    binding.groupImg.setVisibility(View.GONE);
                    binding.videoReceive.setVisibility(View.VISIBLE);
                    Uri uri = Uri.parse(message.getMedia().get(0).getUrl());

                    File cacheFolder = new File(context.getCacheDir(),"media");
                    LeastRecentlyUsedCacheEvictor cacheEvictor = new LeastRecentlyUsedCacheEvictor(1 * 1024 * 1024);
                    SimpleCache simpleCache = new SimpleCache(cacheFolder, cacheEvictor);
//                    CacheDataSource.Factory factory = new CacheDataSource.Factory();
//                    factory.

                    ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(
                            new CacheDataSource.Factory()
                                    .setCache(simpleCache)
                                    .setUpstreamDataSourceFactory(new DefaultHttpDataSource.Factory()
                                            .setUserAgent("ExoplayerDemo"))
                                    .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
                    ).createMediaSource(MediaItem.fromUri(uri));

//                    playerView.setPlayer(player);


                    ExoPlayer player = new ExoPlayer.Builder(context).build();
                    player.setMediaSource(mediaSource);
                    binding.videoReceive.setPlayer(player);
                    MediaItem mediaItem = MediaItem.fromUri(uri);
                    player.setMediaItem(mediaItem);
                    player.prepare();
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
package com.hisu.zola.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.hisu.zola.R;
import com.hisu.zola.database.entity.Message;
import com.hisu.zola.databinding.LayoutSentFileItemBinding;
import com.hisu.zola.databinding.LayoutSentFileItemHeaderBinding;
import com.hisu.zola.util.converter.TimeConverterUtil;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SentFileAdapter extends RecyclerView.Adapter<SentFileAdapter.SentFileViewHolder> implements
        StickyRecyclerHeadersAdapter<SentFileAdapter.SentFileHeaderViewHolder> {

    private final Context context;
    private List<Message> messages;

    public SentFileAdapter(Context context) {
        this.context = context;
        setHasStableIds(true);
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SentFileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SentFileViewHolder(
                LayoutSentFileItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SentFileViewHolder holder, int position) {
        Message message = messages.get(position);
//        if (!message.getType().equalsIgnoreCase("text"))
//            Glide.with(context).asBitmap().load(message.getMedia().get(0).getUrl()).diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(new SimpleTarget<Bitmap>() {
//                        @Override
//                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                            holder.binding.imvSentFileImg.setImageBitmap(resource);
//                            holder.binding.imvSentFileImg.setVisibility(View.VISIBLE);
//                        }
//                    });


        List<Integer> urls = new ArrayList<>();
        urls.add(R.drawable.bg_profile);
        urls.add(R.drawable.bg_chat);
        urls.add(R.drawable.bg_welcome);

        SentFileItemAdapter adapter = new SentFileItemAdapter(context);
        adapter.setImageURLs(urls);

        holder.binding.rvImages.setAdapter(adapter);
        holder.binding.rvImages.setLayoutManager(new GridLayoutManager(context, 3));

//        SecureRandom random = new SecureRandom();
//        if (random.nextInt(2) == 1)
//            holder.binding.imvSentFileImg.setImageResource(R.drawable.bg_profile);
//        else
//            holder.binding.imvSentFileImg.setImageResource(R.drawable.bg_chat);
    }

    @Override
    public int getItemCount() {
        return messages != null ? messages.size() : 0;
    }

    @Override
    public long getHeaderId(int position) {
        Date date = TimeConverterUtil.getDateFromString(messages.get(position).getCreatedAt());
        return date.getDate() + date.getMonth() + date.getYear();
    }

    @Override
    public SentFileHeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return new SentFileHeaderViewHolder(
                LayoutSentFileItemHeaderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
        );
    }

    @Override
    public void onBindHeaderViewHolder(SentFileHeaderViewHolder sentFileHeaderViewHolder, int i) {
        Date date = TimeConverterUtil.getDateFromString(messages.get(i).getCreatedAt());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM, yyyy", Locale.ENGLISH);
        String header = outputFormat.format(date).replace("/", " tháng ");

        sentFileHeaderViewHolder.binding.tvItemHeader.setText(header);

        sentFileHeaderViewHolder.binding.tvItemHeader.setBackgroundColor(Color.WHITE);
    }

    public static class SentFileHeaderViewHolder extends RecyclerView.ViewHolder {

        private final LayoutSentFileItemHeaderBinding binding;

        public SentFileHeaderViewHolder(@NonNull LayoutSentFileItemHeaderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public static class SentFileViewHolder extends RecyclerView.ViewHolder {

        private final LayoutSentFileItemBinding binding;

        public SentFileViewHolder(@NonNull LayoutSentFileItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
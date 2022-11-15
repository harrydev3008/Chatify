package com.hisu.zola.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hisu.zola.databinding.LayoutSentFileItemChildBinding;

import java.util.List;

public class SentFileItemAdapter extends RecyclerView.Adapter<SentFileItemAdapter.SentFileItemChildViewHolder> {

    private List<Integer> imageURLs;
    private Context context;

    public SentFileItemAdapter(Context context) {
        this.context = context;
    }

    public void setImageURLs(List<Integer> imageURLs) {
        this.imageURLs = imageURLs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SentFileItemChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SentFileItemChildViewHolder(
                LayoutSentFileItemChildBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent, false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SentFileItemChildViewHolder holder, int position) {
        holder.binding.imvSentFileImg.setImageResource(imageURLs.get(position));
    }

    @Override
    public int getItemCount() {
        return imageURLs != null ? imageURLs.size() : 0;
    }

    public static class SentFileItemChildViewHolder extends RecyclerView.ViewHolder {

        private final LayoutSentFileItemChildBinding binding;

        public SentFileItemChildViewHolder(@NonNull LayoutSentFileItemChildBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
package com.devs.blog.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devs.blog.R;

import java.util.List;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder> {
    private Context mContext;
    private List<String> mTags;
    private List<String> mTagCount;


    public TagAdapter(Context mContext, List<String> mTags, List<String> mTagCount) {
        this.mContext = mContext;
        this.mTags = mTags;
        this.mTagCount = mTagCount;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.tag_item,parent,false);

        return new TagAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tag.setText("# " + mTags.get(position));
        holder.noOfPosts.setText(mTagCount.get(position) + " posts");

    }

    @Override
    public int getItemCount() {
        return mTags.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tag, noOfPosts;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tag= itemView.findViewById(R.id.hashTag);
            noOfPosts= itemView.findViewById(R.id.no_of_posts);
        }
    }
    public void filter(List<String> filterTags, List<String> filterTagCount){
        this.mTags = filterTags;
        this.mTagCount = filterTagCount;

        notifyDataSetChanged();
    }
}

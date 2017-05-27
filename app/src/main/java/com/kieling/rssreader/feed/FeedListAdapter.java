package com.kieling.rssreader.feed;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kieling.rssreader.R;
import com.kieling.rssreader.model.FeedItem;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedListAdapter extends RecyclerView.Adapter<FeedListAdapter.FeedModelViewHolder> {
    private static final String TAG = "FeedListAdapter";
    private Context mContext;
    private List<FeedItem> mRssList;

    public FeedListAdapter(Context context, List<FeedItem> rssList) {
        mContext = context;
        mRssList = rssList;
    }

    @Override
    public FeedModelViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed, parent, false);
        return new FeedModelViewHolder(v);
    }

    @Override
    public void onBindViewHolder(FeedModelViewHolder holder, int position) {
        final FeedItem feed = mRssList.get(position);
        holder.feedItemDateText.setText(feed.getPubDate());
        holder.feedItemTitleText.setText(feed.getTitle());
        Log.d(TAG, "img: " + feed.getDescription());
        String src = "<img src=\"";
        int index = feed.getDescription().indexOf(src) + src.length();
        String url = feed.getDescription().substring(index, feed.getDescription().indexOf("\"", index + 1));
        Picasso.with(mContext)
                .load(url)
                //.placeholder(R.drawable.user_placeholder)
                //.error(R.drawable.user_placeholder_error)
                .into(holder.feedItemThumbnail);
    }

    @Override
    public int getItemCount() {
        return mRssList.size();
    }

    static class FeedModelViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.feedItemDateText)
        TextView feedItemDateText;

        @BindView(R.id.feedItemTitleText)
        TextView feedItemTitleText;

        @BindView(R.id.feedItemThumbnail)
        ImageView feedItemThumbnail;

        FeedModelViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}

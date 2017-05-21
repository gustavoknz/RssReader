package com.kieling.rssreader;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kieling.rssreader.model.RssFeed;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RssFeedListAdapter extends RecyclerView.Adapter<RssFeedListAdapter.FeedModelViewHolder> {

    private List<RssFeed> mRssFeeds;

    public RssFeedListAdapter(List<RssFeed> rssFeeds) {
        mRssFeeds = rssFeeds;
    }

    @Override
    public FeedModelViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rss_feed, parent, false);
        return new FeedModelViewHolder(v);
    }

    @Override
    public void onBindViewHolder(FeedModelViewHolder holder, int position) {
        final RssFeed rssFeed = mRssFeeds.get(position);
        holder.titleText.setText(rssFeed.title);
        holder.descriptionText.setText(rssFeed.description);
        holder.linkText.setText(rssFeed.link);
    }

    @Override
    public int getItemCount() {
        return mRssFeeds.size();
    }

    static class FeedModelViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.titleText)
        TextView titleText;

        @BindView(R.id.descriptionText)
        TextView descriptionText;

        @BindView(R.id.linkText)
        TextView linkText;

        FeedModelViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}

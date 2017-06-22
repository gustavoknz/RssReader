package com.kieling.rssreader.feed;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kieling.rssreader.R;
import com.kieling.rssreader.image.ImageActivity;
import com.kieling.rssreader.model.FeedItem;
import com.kieling.rssreader.util.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

class FeedListAdapter extends RecyclerView.Adapter<FeedListAdapter.FeedModelViewHolder> {
    private static final String TAG = "FeedListAdapter";
    private static List<FeedItem> mRssList;
    private Context mContext;

    FeedListAdapter(Context context, List<FeedItem> rssList) {
        mContext = context;
        mRssList = rssList;
    }

    @Override
    public FeedModelViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed, parent, false);
        return new FeedModelViewHolder(v, mContext);
    }

    @Override
    public void onBindViewHolder(FeedModelViewHolder holder, int position) {
        final FeedItem feed = mRssList.get(position);
        holder.feedItemDateText.setText(feed.getPubDate());
        holder.feedItemTitleText.setText(feed.getTitle());
        Log.d(TAG, "img: " + feed.getDescription());
        String url = parseUrl(feed.getDescription());
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

    static class FeedModelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Context mContext;

        @BindView(R.id.feedItemDateText)
        TextView feedItemDateText;

        @BindView(R.id.feedItemTitleText)
        TextView feedItemTitleText;

        @BindView(R.id.feedItemThumbnail)
        ImageView feedItemThumbnail;

        FeedModelViewHolder(View v, Context context) {
            super(v);
            ButterKnife.bind(this, v);
            v.setOnClickListener(this);
            mContext = context;
        }

        @Override
        public void onClick(View v) {
            Utils.writeStringToSharedPreferences(mContext, Utils.IMAGE_PARAMETER_KEY, parseUrl(mRssList.get(getAdapterPosition()).getDescription()));
            mContext.startActivity(new Intent(mContext, ImageActivity.class));
        }
    }

    private static String parseUrl(String str) {
        String src = "<img src=\"";
        int index = str.indexOf(src) + src.length();
        return str.substring(index, str.indexOf("\"", index + 1));
    }
}

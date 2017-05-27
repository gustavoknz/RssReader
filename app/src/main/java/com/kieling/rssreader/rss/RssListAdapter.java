package com.kieling.rssreader.rss;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kieling.rssreader.R;
import com.kieling.rssreader.feed.FeedListActivity;
import com.kieling.rssreader.model.RssMenu;
import com.kieling.rssreader.util.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RssListAdapter extends RecyclerView.Adapter<RssListAdapter.FeedModelViewHolder> {
    private static final String TAG = "RssListAdapter";
    private static List<RssMenu> mRssList;
    private Context mContext;

    public RssListAdapter(Context context, List<RssMenu> rssList) {
        mContext = context;
        mRssList = rssList;
    }

    @Override
    public FeedModelViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rss, parent, false);
        return new FeedModelViewHolder(v, mContext);
    }

    @Override
    public void onBindViewHolder(FeedModelViewHolder holder, int position) {
        final RssMenu rss = mRssList.get(position);
        holder.rssItemTitle.setText(mContext.getString(R.string.main_drawer_rss_item, rss.getTitle(), rss.getList().size()));
    }

    @Override
    public int getItemCount() {
        return mRssList.size();
    }

    static class FeedModelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.rssItemTitle)
        TextView rssItemTitle;
        private Context mContext;

        FeedModelViewHolder(View v, Context context) {
            super(v);
            ButterKnife.bind(this, v);
            mContext = context;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "Item clicked: " + getAdapterPosition());
            RssMenu rss = mRssList.get(getAdapterPosition());
            Log.d(TAG, "rss clicked: " + rss);

            Intent intent = new Intent(mContext, FeedListActivity.class);
            intent.putExtra(Utils.RSS_INTENT_KEY, rss);
            mContext.startActivity(intent);
        }
    }
}

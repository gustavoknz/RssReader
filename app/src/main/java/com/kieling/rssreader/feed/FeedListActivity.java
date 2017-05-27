package com.kieling.rssreader.feed;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.kieling.rssreader.R;
import com.kieling.rssreader.model.RssMenu;
import com.kieling.rssreader.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedListActivity extends AppCompatActivity {
    private static final String TAG = "FeedListActivity";
    @BindView(R.id.feedRecyclerView)
    RecyclerView feedRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_list);
        ButterKnife.bind(this);

        RssMenu rss = (RssMenu) getIntent().getSerializableExtra(Utils.RSS_INTENT_KEY);
        Log.d(TAG, "Showing RSS for " + rss);
        FeedListAdapter feedListAdapter = new FeedListAdapter(this, rss.getList());
        feedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        feedRecyclerView.setAdapter(feedListAdapter);
    }
}

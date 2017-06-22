package com.kieling.rssreader.feed;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.kieling.rssreader.R;
import com.kieling.rssreader.model.FeedItem;
import com.kieling.rssreader.model.RssMenu;
import com.kieling.rssreader.service.DataFetcher;
import com.kieling.rssreader.service.RestService;
import com.kieling.rssreader.util.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedListActivity extends AppCompatActivity implements DataFetcher {
    private static final String TAG = "FeedListActivity";
    @BindView(R.id.feedRecyclerView)
    RecyclerView feedRecyclerView;

    @BindView(R.id.rssSwipeRefresh)
    SwipeRefreshLayout rssSwipeRefresh;

    private List<FeedItem> feedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_list);
        ButterKnife.bind(this);

        RssMenu rss = (RssMenu) Utils.readObjectFromSharedPreferences(getApplicationContext(), Utils.RSS_PARAMETER_KEY, RssMenu.class);

        Log.d(TAG, "Showing RSS for " + rss);
        feedList = rss.getList();
        FeedListAdapter feedListAdapter = new FeedListAdapter(this, feedList);
        feedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        feedRecyclerView.setAdapter(feedListAdapter);

        rssSwipeRefresh.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimaryDark);
        rssSwipeRefresh.setOnRefreshListener(() -> {
            Log.d(TAG, "Refreshing...");
            RestService.fetchData(this, rss.getRootUrl());
        });
    }

    @Override
    public void onComplete(RssMenu data) {
        feedList.clear();
        feedList.addAll(data.getList());
        feedRecyclerView.getAdapter().notifyDataSetChanged();
        rssSwipeRefresh.setRefreshing(false);
    }

    @Override
    public void onError(Throwable throwable) {
        rssSwipeRefresh.setRefreshing(false);
        new AlertDialog.Builder(this)
                .setTitle("Error loading data")
                .setMessage("There was an error loading data. Please try again later.")
                .setPositiveButton(android.R.string.ok, null)
                .create()
                .show();
    }
}

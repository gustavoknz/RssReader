package com.kieling.rssreader;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kieling.rssreader.model.RssMenu;
import com.kieling.rssreader.rss.RssListAdapter;
import com.kieling.rssreader.service.DataFetcher;
import com.kieling.rssreader.service.RestService;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DataFetcher {
    private static final String TAG = "MainActivity";
    private static final List<RssMenu> rssList = new ArrayList<>();

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawerLayout)
    DrawerLayout drawer;

    @BindView(R.id.navView)
    NavigationView navigationView;

    @BindView(R.id.urlText)
    EditText urlText;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.rssRecyclerView)
    RecyclerView rssRecyclerView;

    private TextView mainDrawerRegisteredFeeds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        urlText.setText(R.string.main_url_default);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        mainDrawerRegisteredFeeds = (TextView) navigationView.findViewById(R.id.mainDrawerRegisteredFeeds);
        mainDrawerRegisteredFeeds.setText(getString(R.string.main_drawer_registered_rss, rssList.size()));
        RssListAdapter rssListAdapter = new RssListAdapter(getApplicationContext(), rssList);
        rssRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rssRecyclerView.setAdapter(rssListAdapter);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void fetchDataClicked(View view) {
        String url = urlText.getText().toString();
        String regex = "[a-z0-9+&@#/%?=~_|!:,.;]*[a-z0-9+&@#/%=~_|]";
        if (url.matches(regex)) {
            Log.d(TAG, "Matched: " + url);
            progressBar.setVisibility(View.VISIBLE);
            fetchData(url);
        } else {
            Log.d(TAG, "NOT matched: " + url);
            new AlertDialog.Builder(this)
                    .setTitle("Invalid URL")
                    .setMessage("Enter a valid URL")
                    .setPositiveButton(android.R.string.ok, null)
                    .create()
                    .show();
        }
    }

    public void fetchData(String url) {
        RestService.fetchData(this, url);
    }

    @Override
    public void onComplete(RssMenu data) {
        progressBar.setVisibility(View.INVISIBLE);
        if (!rssList.contains(data)) {
            rssList.add(data);
        }
        mainDrawerRegisteredFeeds.setText(getString(R.string.main_drawer_registered_rss, rssList.size()));
        rssRecyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onError(Throwable throwable) {
        Log.e(TAG, "ERROR /o\\", throwable);
        progressBar.setVisibility(View.INVISIBLE);
    }
}

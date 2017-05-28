package com.kieling.rssreader;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
import android.widget.Toast;

import com.kieling.rssreader.model.RssMenu;
import com.kieling.rssreader.rss.RssListAdapter;
import com.kieling.rssreader.service.DataFetcher;
import com.kieling.rssreader.service.RestService;
import com.kieling.rssreader.util.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        //Navigation drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        sharedPref = getPreferences(Context.MODE_PRIVATE);
        Set<String> urls = sharedPref.getStringSet(Utils.RSS_URL_SET_KEY, new HashSet<>());
        Log.d(TAG, String.format("Initially, I will fetch %d URLs", urls.size()));
        for (String url : urls) {
            Log.d(TAG, "Fetching " + url);
            fetchData(url);
        }

        //Rss items in navigation drawer
        mainDrawerRegisteredFeeds = (TextView) navigationView.findViewById(R.id.mainDrawerRegisteredFeeds);
        mainDrawerRegisteredFeeds.setText(getString(R.string.main_drawer_registered_rss, rssList.size()));
        RssListAdapter rssListAdapter = new RssListAdapter(getApplicationContext(), rssList);
        rssRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rssRecyclerView.setAdapter(rssListAdapter);
        urlText.setText(R.string.main_url_default);
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

            //If I could not add it to the SP, it must already exists.
            //So, it has already been fetched and I do not need to do it again
            if (addUrlToSharedPreferences(url)) {
                progressBar.setVisibility(View.VISIBLE);
                fetchData(url);
            } else {
                Toast.makeText(this, "This URL has already been fetched", Toast.LENGTH_LONG).show();
            }
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

    private boolean addUrlToSharedPreferences(String url) {
        Set<String> urls = new HashSet<>(sharedPref.getStringSet(Utils.RSS_URL_SET_KEY, new HashSet<>()));
        SharedPreferences.Editor editor = sharedPref.edit();
        boolean added = urls.add(url);
        Log.d(TAG, String.format("URL %s added? %b", url, added));
        if (added) {
            editor.putStringSet(Utils.RSS_URL_SET_KEY, urls);
            editor.apply();
        }
        return added;
    }

    public void fetchData(String url) {
        RestService.fetchData(this, url);
    }

    @Override
    public void onComplete(RssMenu data) {
        progressBar.setVisibility(View.INVISIBLE);
        if (!rssList.contains(data)) {
            rssList.add(data);
            mainDrawerRegisteredFeeds.setText(getString(R.string.main_drawer_registered_rss, rssList.size()));
            rssRecyclerView.getAdapter().notifyDataSetChanged();
            Toast.makeText(this, "RSS added successfully", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onError(Throwable throwable) {
        Log.e(TAG, "ERROR /o\\", throwable);
        progressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(this, "There was an error fetching this URL", Toast.LENGTH_LONG).show();
    }
}

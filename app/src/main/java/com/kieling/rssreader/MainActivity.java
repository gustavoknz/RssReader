package com.kieling.rssreader;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kieling.rssreader.model.RssMenu;
import com.kieling.rssreader.rss.RssListAdapter;
import com.kieling.rssreader.service.RssService;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private static final List<RssMenu> rssList = new ArrayList<>();
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
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
        urlText.setText("xkcd.com/rss.xml");
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

        /*rssRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(MainActivity.this, rssRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // do whatever
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );*/
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar items clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view items clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void fabClicked(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
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
        //URL must start with http:// or https://
        String baseUrl = url.startsWith("http://") || url.startsWith("https://") ? url : "http://".concat(url);

        //URL must not end with /
        baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;

        //Get root URL
        String rootUrl = baseUrl.substring(0, baseUrl.lastIndexOf('/') + 1);

        String suffix = baseUrl.substring(baseUrl.lastIndexOf('/') + 1, baseUrl.length());
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(rootUrl)
                .build();

        RssService rssService = retrofit.create(RssService.class);
        Observable<RssMenu> feed = rssService.getRssData(suffix);

        Log.i(TAG, String.format("Fetching data from %s%s", rootUrl, suffix));
        Subscription subscription = feed.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RssMenu>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "Complete! :D");
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "ERROR /o\\", e);
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onNext(RssMenu rssMenu) {
                        Log.i(TAG, "Received title: " + rssMenu.getTitle());

                        if (!rssList.contains(rssMenu)) {
                            rssList.add(rssMenu);
                        }
                        mainDrawerRegisteredFeeds.setText(getString(R.string.main_drawer_registered_rss, rssList.size()));
                        rssRecyclerView.getAdapter().notifyDataSetChanged();
                    }
                });
        Log.d(TAG, "is Unsubscribed? " + subscription.isUnsubscribed());
    }
}

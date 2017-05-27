package com.kieling.rssreader.service;

import android.util.Log;

import com.kieling.rssreader.model.RssMenu;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public final class RestService {
    private static final String TAG = "RestService";

    public static void fetchData(DataFetcher dataFetcher, String url) {
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
        feed.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RssMenu>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "Complete! :D");
                    }

                    @Override
                    public void onError(Throwable e) {
                        dataFetcher.onError(e);
                    }

                    @Override
                    public void onNext(RssMenu rssMenu) {
                        rssMenu.setRootUrl(url);
                        Log.i(TAG, "Received: " + rssMenu);
                        dataFetcher.onComplete(rssMenu);
                    }
                });
    }
}

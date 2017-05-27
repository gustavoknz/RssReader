package com.kieling.rssreader.service;

import com.kieling.rssreader.model.RssMenu;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface RssService {
    @GET("{suffix}")
    Observable<RssMenu> getRssData(@Path("suffix") String suffix);
}

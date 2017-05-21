package com.kieling.rssreader;

import com.kieling.rssreader.model.RssFeed;

import rx.Observable;

public interface RssService {
    Observable<RssFeed> getRssData();
}

package com.kieling.rssreader.service;

import com.kieling.rssreader.model.RssMenu;

public interface DataFetcher {
    void onComplete(RssMenu data);
    void onError(Throwable throwable);
}

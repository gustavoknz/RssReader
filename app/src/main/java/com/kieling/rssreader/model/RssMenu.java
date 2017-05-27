package com.kieling.rssreader.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import java.io.Serializable;
import java.util.List;

@Root(strict = false)
public class RssMenu implements Serializable {

    @Path("channel")
    @Element(name = "title")
    private String title;

    @Path("channel")
    @Element(name = "link")
    private String link;

    @Path("channel")
    @Element(name = "description")
    private String description;

    @Path("channel")
    @Element(name = "language")
    private String language;

    @Path("channel")
    @ElementList(inline = true)
    private List<FeedItem> list;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<FeedItem> getList() {
        return list;
    }

    public void setList(List<FeedItem> list) {
        this.list = list;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RssMenu rssMenu = (RssMenu) o;

        return link.equals(rssMenu.link);
    }

    @Override
    public int hashCode() {
        return link.hashCode();
    }

    @Override
    public String toString() {
        return "RssMenu{" +
                "title='" + title + '\'' +
                ", link='" + link + '\'' +
                ", description='" + description + '\'' +
                ", language='" + language + '\'' +
                ", list=" + list +
                '}';
    }
}

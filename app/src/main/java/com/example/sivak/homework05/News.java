package com.example.sivak.homework05;

public class News {

    String author;
    String title;
    String url;
    String urlToImage;
    String publishedAt;

    public News() {}

    @Override
    public String toString() {
        return "News{" +
                "author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", urlToImage='" + urlToImage + '\'' +
                ", publishedAt='" + publishedAt + '\'' +
                '}';
    }
}

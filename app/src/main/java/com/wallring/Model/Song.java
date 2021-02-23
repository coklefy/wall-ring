package com.wallring.Model;
public class Song {

    private String title;
    private String artworkUrl;
    private long duration;

    public Song(String title, String artworkUrl, long duration) {
        this.title = title;
        this.artworkUrl = artworkUrl;
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }


    public String getArtworkUrl() {
        return artworkUrl;
    }

    public long getDuration() {
        return duration;
    }



}
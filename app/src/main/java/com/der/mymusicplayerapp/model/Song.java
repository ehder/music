package com.der.mymusicplayerapp.model;

public class Song {

    private String title;
    private String subTitle;
    private String path;
    private boolean favSong;

    private String albumName;
    private int trackNumber;
    private int duration;
    private int artistID;
    private int artistName;
    private int year;

    public Song() {
    }

    public Song(String title, String subTitle, String path) {
        this.title = title;
        this.subTitle = subTitle;
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isFavSong() {
        return favSong;
    }

    public void setFavSong(boolean favSong) {
        this.favSong = favSong;
    }
}

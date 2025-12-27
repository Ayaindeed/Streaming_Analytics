package com.streaming.model;

/**
 * Entité représentant une vidéo
 */
public class Video {

    private String videoId;
    private String title;
    private String category;
    private int duration;
    private String uploadDate;
    private int views;
    private int likes;

    // Constructors
    public Video() {}

    public Video(String videoId, String title, String category, int duration,
                String uploadDate, int views, int likes) {
        this.videoId = videoId;
        this.title = title;
        this.category = category;
        this.duration = duration;
        this.uploadDate = uploadDate;
        this.views = views;
        this.likes = likes;
    }

    // Getters and Setters
    public String getVideoId() { return videoId; }
    public void setVideoId(String videoId) { this.videoId = videoId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public String getUploadDate() { return uploadDate; }
    public void setUploadDate(String uploadDate) { this.uploadDate = uploadDate; }

    public int getViews() { return views; }
    public void setViews(int views) { this.views = views; }

    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }

    @Override
    public String toString() {
        return "Video{" +
                "videoId='" + videoId + '\'' +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", duration=" + duration +
                ", uploadDate='" + uploadDate + '\'' +
                ", views=" + views +
                ", likes=" + likes +
                '}';
    }
}

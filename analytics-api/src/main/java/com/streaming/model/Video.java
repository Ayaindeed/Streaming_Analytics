package com.streaming.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * Represents metadata for a video
 */
@Entity
@Table(name = "videos")
public class Video {

    @Id
    private String videoId;
    
    private String title;
    private String category;
    private int duration; // in seconds
    private Instant uploadDate;
    private String creator;
    private String description;
    
    // Default constructor for JPA
    public Video() {
    }

    public Video(String videoId, String title, String category, int duration, 
                String creator, String description) {
        this.videoId = videoId;
        this.title = title;
        this.category = category;
        this.duration = duration;
        this.creator = creator;
        this.description = description;
        this.uploadDate = Instant.now();
    }

    // Getters and Setters
    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Instant getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Instant uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Video{" +
                "videoId='" + videoId + '\'' +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", duration=" + duration +
                ", uploadDate=" + uploadDate +
                ", creator='" + creator + '\'' +
                '}';
    }
}

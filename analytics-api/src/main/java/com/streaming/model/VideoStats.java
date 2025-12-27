package com.streaming.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * Represents aggregated statistics for a video
 */
@Entity
@Table(name = "video_stats")
public class VideoStats {

    @Id
    private String videoId;
    
    private String title;
    private long totalViews;
    private double avgDuration;
    private long totalLikes;
    private long totalShares;
    private long totalComments;
    private Instant lastUpdated;

    // Default constructor for JPA
    public VideoStats() {
    }

    public VideoStats(String videoId, String title) {
        this.videoId = videoId;
        this.title = title;
        this.totalViews = 0;
        this.avgDuration = 0;
        this.totalLikes = 0;
        this.totalShares = 0;
        this.totalComments = 0;
        this.lastUpdated = Instant.now();
    }

    /**
     * Updates statistics with a new event
     * @param event The new event to process
     */
    public void processEvent(Event event) {
        if (event.getVideoId().equals(this.videoId)) {
            switch (event.getAction()) {
                case WATCH:
                    this.totalViews++;
                    // Update average duration using running average formula
                    this.avgDuration = ((this.avgDuration * (this.totalViews - 1)) + event.getDuration()) / this.totalViews;
                    break;
                case LIKE:
                    this.totalLikes++;
                    break;
                case SHARE:
                    this.totalShares++;
                    break;
                case COMMENT:
                    this.totalComments++;
                    break;
            }
            this.lastUpdated = Instant.now();
        }
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

    public long getTotalViews() {
        return totalViews;
    }

    public void setTotalViews(long totalViews) {
        this.totalViews = totalViews;
    }

    public double getAvgDuration() {
        return avgDuration;
    }

    public void setAvgDuration(double avgDuration) {
        this.avgDuration = avgDuration;
    }

    public long getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(long totalLikes) {
        this.totalLikes = totalLikes;
    }

    public long getTotalShares() {
        return totalShares;
    }

    public void setTotalShares(long totalShares) {
        this.totalShares = totalShares;
    }

    public long getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(long totalComments) {
        this.totalComments = totalComments;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public String toString() {
        return "VideoStats{" +
                "videoId='" + videoId + '\'' +
                ", title='" + title + '\'' +
                ", totalViews=" + totalViews +
                ", avgDuration=" + avgDuration +
                ", totalLikes=" + totalLikes +
                ", totalShares=" + totalShares +
                ", totalComments=" + totalComments +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}

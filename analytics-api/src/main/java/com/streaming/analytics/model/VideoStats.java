package com.streaming.analytics.model;

/**
 * Entité représentant les statistiques d'une vidéo
 */
public class VideoStats {

    private String videoId;
    private long totalViews;
    private long totalWatchTime;
    private double averageWatchTime;
    private int uniqueViewers;
    private String lastUpdated;

    // Constructors
    public VideoStats() {}

    public VideoStats(String videoId, long totalViews, long totalWatchTime,
                     double averageWatchTime, int uniqueViewers, String lastUpdated) {
        this.videoId = videoId;
        this.totalViews = totalViews;
        this.totalWatchTime = totalWatchTime;
        this.averageWatchTime = averageWatchTime;
        this.uniqueViewers = uniqueViewers;
        this.lastUpdated = lastUpdated;
    }

    // Getters and Setters
    public String getVideoId() { return videoId; }
    public void setVideoId(String videoId) { this.videoId = videoId; }

    public long getTotalViews() { return totalViews; }
    public void setTotalViews(long totalViews) { this.totalViews = totalViews; }

    public long getTotalWatchTime() { return totalWatchTime; }
    public void setTotalWatchTime(long totalWatchTime) { this.totalWatchTime = totalWatchTime; }

    public double getAverageWatchTime() { return averageWatchTime; }
    public void setAverageWatchTime(double averageWatchTime) { this.averageWatchTime = averageWatchTime; }

    public int getUniqueViewers() { return uniqueViewers; }
    public void setUniqueViewers(int uniqueViewers) { this.uniqueViewers = uniqueViewers; }

    public String getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(String lastUpdated) { this.lastUpdated = lastUpdated; }
}
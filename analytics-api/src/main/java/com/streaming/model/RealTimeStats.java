package com.streaming.model;

import java.util.List;

/**
 * Représente les statistiques temps réel du système
 */
public class RealTimeStats {

    private long currentViewers;
    private long eventsPerSecond;
    private double averageWatchTime;
    private String mostWatchedVideoId;
    private long totalEventsProcessed;
    private String timestamp;
    private List<String> topVideoIds;
    private int activeUsers;

    // Constructors
    public RealTimeStats() {}

    public RealTimeStats(long currentViewers, long eventsPerSecond, double averageWatchTime,
                        String mostWatchedVideoId, long totalEventsProcessed, String timestamp) {
        this.currentViewers = currentViewers;
        this.eventsPerSecond = eventsPerSecond;
        this.averageWatchTime = averageWatchTime;
        this.mostWatchedVideoId = mostWatchedVideoId;
        this.totalEventsProcessed = totalEventsProcessed;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public long getCurrentViewers() { return currentViewers; }
    public void setCurrentViewers(long currentViewers) { this.currentViewers = currentViewers; }

    public long getEventsPerSecond() { return eventsPerSecond; }
    public void setEventsPerSecond(long eventsPerSecond) { this.eventsPerSecond = eventsPerSecond; }

    public double getAverageWatchTime() { return averageWatchTime; }
    public void setAverageWatchTime(double averageWatchTime) { this.averageWatchTime = averageWatchTime; }

    public String getMostWatchedVideoId() { return mostWatchedVideoId; }
    public void setMostWatchedVideoId(String mostWatchedVideoId) { this.mostWatchedVideoId = mostWatchedVideoId; }

    public long getTotalEventsProcessed() { return totalEventsProcessed; }
    public void setTotalEventsProcessed(long totalEventsProcessed) { this.totalEventsProcessed = totalEventsProcessed; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public List<String> getTopVideoIds() { return topVideoIds; }
    public void setTopVideoIds(List<String> topVideoIds) { this.topVideoIds = topVideoIds; }

    public int getActiveUsers() { return activeUsers; }
    public void setActiveUsers(int activeUsers) { this.activeUsers = activeUsers; }
}

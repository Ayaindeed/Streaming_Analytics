package com.streaming.model;

import java.util.List;
import java.util.Map;

/**
 * Représente les statistiques de tendances
 */
public class TrendingStats {

    private String timeframe;
    private List<VideoTrend> trendingVideos;
    private List<String> trendingCategories;
    private Map<String, Long> viewsPerCategory;
    private Map<String, Long> viewsPerDeviceType;
    private double growthRate;
    private String peakHour;
    private long totalViewsInTimeframe;
    private String generatedAt;

    // Inner class for video trends
    public static class VideoTrend {
        private String videoId;
        private String videoTitle;
        private long views;
        private double trendScore;
        private int rank;
        private double growthPercentage;

        // Constructors
        public VideoTrend() {}

        public VideoTrend(String videoId, String videoTitle, long views, double trendScore, int rank) {
            this.videoId = videoId;
            this.videoTitle = videoTitle;
            this.views = views;
            this.trendScore = trendScore;
            this.rank = rank;
        }

        // Getters and Setters
        public String getVideoId() { return videoId; }
        public void setVideoId(String videoId) { this.videoId = videoId; }

        public String getVideoTitle() { return videoTitle; }
        public void setVideoTitle(String videoTitle) { this.videoTitle = videoTitle; }

        public long getViews() { return views; }
        public void setViews(long views) { this.views = views; }

        public double getTrendScore() { return trendScore; }
        public void setTrendScore(double trendScore) { this.trendScore = trendScore; }

        public int getRank() { return rank; }
        public void setRank(int rank) { this.rank = rank; }

        public double getGrowthPercentage() { return growthPercentage; }
        public void setGrowthPercentage(double growthPercentage) { this.growthPercentage = growthPercentage; }
    }

    // Constructors
    public TrendingStats() {}

    public TrendingStats(String timeframe, List<VideoTrend> trendingVideos, 
                        List<String> trendingCategories, double growthRate, String generatedAt) {
        this.timeframe = timeframe;
        this.trendingVideos = trendingVideos;
        this.trendingCategories = trendingCategories;
        this.growthRate = growthRate;
        this.generatedAt = generatedAt;
    }

    // Getters and Setters
    public String getTimeframe() { return timeframe; }
    public void setTimeframe(String timeframe) { this.timeframe = timeframe; }

    public List<VideoTrend> getTrendingVideos() { return trendingVideos; }
    public void setTrendingVideos(List<VideoTrend> trendingVideos) { this.trendingVideos = trendingVideos; }

    public List<String> getTrendingCategories() { return trendingCategories; }
    public void setTrendingCategories(List<String> trendingCategories) { this.trendingCategories = trendingCategories; }

    public Map<String, Long> getViewsPerCategory() { return viewsPerCategory; }
    public void setViewsPerCategory(Map<String, Long> viewsPerCategory) { this.viewsPerCategory = viewsPerCategory; }

    public Map<String, Long> getViewsPerDeviceType() { return viewsPerDeviceType; }
    public void setViewsPerDeviceType(Map<String, Long> viewsPerDeviceType) { this.viewsPerDeviceType = viewsPerDeviceType; }

    public double getGrowthRate() { return growthRate; }
    public void setGrowthRate(double growthRate) { this.growthRate = growthRate; }

    public String getPeakHour() { return peakHour; }
    public void setPeakHour(String peakHour) { this.peakHour = peakHour; }

    public long getTotalViewsInTimeframe() { return totalViewsInTimeframe; }
    public void setTotalViewsInTimeframe(long totalViewsInTimeframe) { this.totalViewsInTimeframe = totalViewsInTimeframe; }

    public String getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(String generatedAt) { this.generatedAt = generatedAt; }
}

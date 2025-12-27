package com.streaming.model;

/**
 * Représente une recommandation de vidéo personnalisée pour un utilisateur
 */
public class VideoRecommendation {

    private String videoId;
    private String videoTitle;
    private String category;
    private int duration;
    private double relevanceScore;
    private long views;
    private int likes;
    private String reason;
    private double confidenceScore;
    private String thumbnail;

    // Constructors
    public VideoRecommendation() {}

    public VideoRecommendation(String videoId, String videoTitle, String category, 
                              double relevanceScore, String reason) {
        this.videoId = videoId;
        this.videoTitle = videoTitle;
        this.category = category;
        this.relevanceScore = relevanceScore;
        this.reason = reason;
    }

    public VideoRecommendation(String videoId, String videoTitle, String category, int duration,
                              double relevanceScore, long views, int likes, String reason,
                              double confidenceScore) {
        this.videoId = videoId;
        this.videoTitle = videoTitle;
        this.category = category;
        this.duration = duration;
        this.relevanceScore = relevanceScore;
        this.views = views;
        this.likes = likes;
        this.reason = reason;
        this.confidenceScore = confidenceScore;
    }

    // Getters and Setters
    public String getVideoId() { return videoId; }
    public void setVideoId(String videoId) { this.videoId = videoId; }

    public String getVideoTitle() { return videoTitle; }
    public void setVideoTitle(String videoTitle) { this.videoTitle = videoTitle; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public double getRelevanceScore() { return relevanceScore; }
    public void setRelevanceScore(double relevanceScore) { this.relevanceScore = relevanceScore; }

    public long getViews() { return views; }
    public void setViews(long views) { this.views = views; }

    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(double confidenceScore) { this.confidenceScore = confidenceScore; }

    public String getThumbnail() { return thumbnail; }
    public void setThumbnail(String thumbnail) { this.thumbnail = thumbnail; }
}

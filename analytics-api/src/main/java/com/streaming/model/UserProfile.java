package com.streaming.model;

/**
 * Entité représentant le profil d'un utilisateur
 */
public class UserProfile {

    private String userId;
    private String preferredCategories;
    private int totalWatchTime;
    private int videosWatched;
    private String lastActivity;
    private String favoriteDevice;

    // Constructors
    public UserProfile() {}

    public UserProfile(String userId, String preferredCategories, int totalWatchTime,
                      int videosWatched, String lastActivity, String favoriteDevice) {
        this.userId = userId;
        this.preferredCategories = preferredCategories;
        this.totalWatchTime = totalWatchTime;
        this.videosWatched = videosWatched;
        this.lastActivity = lastActivity;
        this.favoriteDevice = favoriteDevice;
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getPreferredCategories() { return preferredCategories; }
    public void setPreferredCategories(String preferredCategories) { this.preferredCategories = preferredCategories; }

    public int getTotalWatchTime() { return totalWatchTime; }
    public void setTotalWatchTime(int totalWatchTime) { this.totalWatchTime = totalWatchTime; }

    public int getVideosWatched() { return videosWatched; }
    public void setVideosWatched(int videosWatched) { this.videosWatched = videosWatched; }

    public String getLastActivity() { return lastActivity; }
    public void setLastActivity(String lastActivity) { this.lastActivity = lastActivity; }

    public String getFavoriteDevice() { return favoriteDevice; }
    public void setFavoriteDevice(String favoriteDevice) { this.favoriteDevice = favoriteDevice; }
}

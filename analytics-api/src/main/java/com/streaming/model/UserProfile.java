package com.streaming.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a user profile with viewing history and preferences
 */
@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    private String userId;
    
    @ElementCollection
    private List<String> watchHistory;
    
    @ElementCollection
    private Map<String, Integer> categoryPreferences;
    
    @ElementCollection
    private List<String> recommendedVideos;
    
    private Instant lastUpdated;

    // Default constructor for JPA
    public UserProfile() {
        this.watchHistory = new ArrayList<>();
        this.categoryPreferences = new HashMap<>();
        this.recommendedVideos = new ArrayList<>();
    }

    public UserProfile(String userId) {
        this.userId = userId;
        this.watchHistory = new ArrayList<>();
        this.categoryPreferences = new HashMap<>();
        this.recommendedVideos = new ArrayList<>();
        this.lastUpdated = Instant.now();
    }

    /**
     * Updates user profile with a watch event
     * @param videoId The video ID that was watched
     * @param category The category of the video
     */
    public void addWatchEvent(String videoId, String category) {
        // Add to watch history (keep last 50 videos)
        if (!watchHistory.contains(videoId)) {
            watchHistory.add(0, videoId);
            if (watchHistory.size() > 50) {
                watchHistory.remove(watchHistory.size() - 1);
            }
        }
        
        // Update category preferences
        categoryPreferences.put(category, categoryPreferences.getOrDefault(category, 0) + 1);
        
        this.lastUpdated = Instant.now();
    }

    /**
     * Updates the recommended videos list for this user
     * @param recommendations List of video IDs to recommend
     */
    public void updateRecommendations(List<String> recommendations) {
        this.recommendedVideos = new ArrayList<>(recommendations);
        this.lastUpdated = Instant.now();
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getWatchHistory() {
        return watchHistory;
    }

    public void setWatchHistory(List<String> watchHistory) {
        this.watchHistory = watchHistory;
    }

    public Map<String, Integer> getCategoryPreferences() {
        return categoryPreferences;
    }

    public void setCategoryPreferences(Map<String, Integer> categoryPreferences) {
        this.categoryPreferences = categoryPreferences;
    }

    public List<String> getRecommendedVideos() {
        return recommendedVideos;
    }

    public void setRecommendedVideos(List<String> recommendedVideos) {
        this.recommendedVideos = recommendedVideos;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "userId='" + userId + '\'' +
                ", watchHistorySize=" + watchHistory.size() +
                ", categoryPreferencesSize=" + categoryPreferences.size() +
                ", recommendedVideosSize=" + recommendedVideos.size() +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}

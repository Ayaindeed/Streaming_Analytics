package com.streaming.service;

import com.streaming.model.UserProfile;
import com.streaming.model.Video;
import com.streaming.model.VideoStats;
import com.streaming.repository.UserProfileRepository;
import com.streaming.repository.VideoRepository;
import com.streaming.repository.VideoStatsRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for generating personalized recommendations
 */
@ApplicationScoped
public class RecommendationService {
    
    @Inject
    private UserProfileRepository userProfileRepository;
    
    @Inject
    private VideoRepository videoRepository;
    
    @Inject
    private VideoStatsRepository videoStatsRepository;
    
    /**
     * Generates personalized recommendations for a user
     * Algorithm:
     * 1. Get user's watch history and preferences
     * 2. Get top videos in user's preferred categories
     * 3. Filter out already watched videos
     * 4. Add some generally popular videos
     * 
     * @param userId The user ID
     * @param limit Maximum number of recommendations
     * @return List of recommended videos
     */
    @Transactional
    public List<Video> getRecommendations(String userId, int limit) {
        // Get user profile
        UserProfile profile = userProfileRepository.findOrCreateByUserId(userId);
        
        // Get user's watch history
        List<String> watchedVideos = profile.getWatchHistory();
        
        // Get user's category preferences
        Map<String, Integer> categoryPreferences = profile.getCategoryPreferences();
        
        // Prepare result list
        List<Video> recommendations = new ArrayList<>();
        
        // Find top category based on user preferences
        String topCategory = getTopCategory(categoryPreferences);
        
        // If user has preferences, get videos from preferred categories
        if (topCategory != null) {
            List<Video> categoryVideos = videoRepository.findByCategory(topCategory);
            
            // Filter out watched videos
            List<Video> unwatchedCategoryVideos = categoryVideos.stream()
                    .filter(v -> !watchedVideos.contains(v.getVideoId()))
                    .collect(Collectors.toList());
            
            // Add to recommendations (up to 60% of requested limit)
            int categoryLimit = (int) Math.ceil(limit * 0.6);
            for (int i = 0; i < Math.min(categoryLimit, unwatchedCategoryVideos.size()); i++) {
                recommendations.add(unwatchedCategoryVideos.get(i));
            }
        }
        
        // Add top popular videos (remaining slots)
        int remainingSlots = limit - recommendations.size();
        if (remainingSlots > 0) {
            List<VideoStats> topVideos = videoStatsRepository.getTopVideos(remainingSlots * 2);
            
            for (VideoStats stats : topVideos) {
                // Skip if already in recommendations or already watched
                if (recommendations.size() >= limit) break;
                
                if (watchedVideos.contains(stats.getVideoId())) continue;
                
                if (recommendations.stream().noneMatch(v -> v.getVideoId().equals(stats.getVideoId()))) {
                    Video video = videoRepository.findById(stats.getVideoId());
                    if (video != null) {
                        recommendations.add(video);
                    }
                }
            }
        }
        
        // Update user's recommendations list
        List<String> recommendationIds = recommendations.stream()
                .map(Video::getVideoId)
                .collect(Collectors.toList());
        profile.updateRecommendations(recommendationIds);
        userProfileRepository.save(profile);
        
        return recommendations;
    }
    
    /**
     * Gets the most preferred category for a user
     * 
     * @param categoryPreferences Map of category preferences
     * @return Top category or null if none
     */
    private String getTopCategory(Map<String, Integer> categoryPreferences) {
        if (categoryPreferences == null || categoryPreferences.isEmpty()) {
            return null;
        }
        
        String topCategory = null;
        int maxCount = 0;
        
        for (Map.Entry<String, Integer> entry : categoryPreferences.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                topCategory = entry.getKey();
            }
        }
        
        return topCategory;
    }
    
    /**
     * Refreshes recommendations for active users
     * Used by scheduled jobs to precompute recommendations
     * 
     * @param userLimit Maximum number of users to process
     */
    @Transactional
    public void refreshRecommendationsForActiveUsers(int userLimit) {
        List<UserProfile> activeUsers = userProfileRepository.findRecentlyActiveUsers(
                java.time.Instant.now().minus(java.time.Duration.ofDays(7)), userLimit);
        
        for (UserProfile profile : activeUsers) {
            getRecommendations(profile.getUserId(), 20);
        }
    }
}

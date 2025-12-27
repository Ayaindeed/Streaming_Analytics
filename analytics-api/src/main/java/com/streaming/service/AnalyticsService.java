package com.streaming.service;

import com.streaming.model.Event;
import com.streaming.model.Video;
import com.streaming.model.VideoStats;
import com.streaming.repository.EventRepository;
import com.streaming.repository.VideoRepository;
import com.streaming.repository.VideoStatsRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for advanced analytics operations
 */
@ApplicationScoped
public class AnalyticsService {
    
    @Inject
    private EventRepository eventRepository;
    
    @Inject
    private VideoRepository videoRepository;
    
    @Inject
    private VideoStatsRepository videoStatsRepository;
    
    /**
     * Statistics for a video category
     */
    public static class CategoryStats {
        private final String category;
        private long totalViews;
        private long totalVideos;
        private double avgDuration;
        private long totalLikes;
        
        public CategoryStats(String category) {
            this.category = category;
            this.totalViews = 0;
            this.totalVideos = 0;
            this.avgDuration = 0;
            this.totalLikes = 0;
        }
        
        public void addVideo(VideoStats stats) {
            this.totalVideos++;
            this.totalViews += stats.getTotalViews();
            this.totalLikes += stats.getTotalLikes();
            this.avgDuration = ((this.avgDuration * (this.totalVideos - 1)) + stats.getAvgDuration()) / this.totalVideos;
        }
        
        // Getters
        public String getCategory() { return category; }
        public long getTotalViews() { return totalViews; }
        public long getTotalVideos() { return totalVideos; }
        public double getAvgDuration() { return avgDuration; }
        public long getTotalLikes() { return totalLikes; }
    }
    
    /**
     * Calculates statistics by video category
     * 
     * @return Map of category names to statistics
     */
    @Transactional
    public Map<String, CategoryStats> aggregateByCategory() {
        Map<String, CategoryStats> result = new HashMap<>();
        
        // Get all videos
        List<Video> videos = videoRepository.findAll(0, 10000); // Limit for performance
        
        // Get stats for each video and aggregate by category
        for (Video video : videos) {
            VideoStats stats = videoStatsRepository.getStats(video.getVideoId());
            if (stats == null) continue;
            
            String category = video.getCategory();
            if (!result.containsKey(category)) {
                result.put(category, new CategoryStats(category));
            }
            
            result.get(category).addVideo(stats);
        }
        
        return result;
    }
    
    /**
     * Detects trending videos based on recent view growth
     * 
     * @param limit Maximum number of trending videos to return
     * @return List of trending videos
     */
    @Transactional
    public List<Video> detectTrending(int limit) {
        // Get time thresholds
        Instant now = Instant.now();
        Instant oneDayAgo = now.minus(Duration.ofDays(1));
        Instant oneWeekAgo = now.minus(Duration.ofDays(7));
        
        // Find videos with recent views
        List<VideoStats> recentlyActiveVideos = videoStatsRepository.getTrendingVideos(100, oneDayAgo);
        
        // Calculate growth rate: (views_last_24h / (views_last_week/7))
        Map<String, Double> growthRates = new HashMap<>();
        
        for (VideoStats stats : recentlyActiveVideos) {
            // Get events for last 24h
            List<Event> recentEvents = eventRepository.findByTimeRange(oneDayAgo, now)
                    .stream()
                    .filter(e -> e.getVideoId().equals(stats.getVideoId()) && 
                           e.getAction() == Event.EventAction.WATCH)
                    .collect(Collectors.toList());
            int last24hViews = recentEvents.size();
            
            // Get events for last week
            List<Event> weekEvents = eventRepository.findByTimeRange(oneWeekAgo, oneDayAgo)
                    .stream()
                    .filter(e -> e.getVideoId().equals(stats.getVideoId()) && 
                           e.getAction() == Event.EventAction.WATCH)
                    .collect(Collectors.toList());
            int lastWeekViews = weekEvents.size();
            
            // Calculate daily average for last week (avoid division by zero)
            double dailyAvgLastWeek = lastWeekViews / 6.0; // 6 days (excluding today)
            if (dailyAvgLastWeek < 1) dailyAvgLastWeek = 1;
            
            // Calculate growth rate
            double growthRate = last24hViews / dailyAvgLastWeek;
            
            // Only consider as trending if growth is significant
            if (growthRate > 1.5 && last24hViews > 10) {
                growthRates.put(stats.getVideoId(), growthRate);
            }
        }
        
        // Sort by growth rate and get top videos
        List<String> trendingVideoIds = growthRates.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        
        // Get video objects
        List<Video> trendingVideos = trendingVideoIds.stream()
                .map(videoRepository::findById)
                .filter(video -> video != null)
                .collect(Collectors.toList());
        
        return trendingVideos;
    }
    
    /**
     * Gets global platform statistics
     * 
     * @return Map of statistic names to values
     */
    public Map<String, Object> getGlobalStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Count total videos
        long totalVideos = videoRepository.countVideos();
        stats.put("totalVideos", totalVideos);
        
        // Sum total views across all videos
        long totalViews = 0;
        List<VideoStats> allStats = videoStatsRepository.getTopVideos(10000); // Limit for performance
        for (VideoStats vs : allStats) {
            totalViews += vs.getTotalViews();
        }
        stats.put("totalViews", totalViews);
        
        // Calculate other metrics
        double avgViewsPerVideo = totalVideos > 0 ? (double)totalViews / totalVideos : 0;
        stats.put("avgViewsPerVideo", avgViewsPerVideo);
        
        return stats;
    }
}

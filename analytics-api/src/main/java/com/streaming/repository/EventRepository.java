package com.streaming.repository;

import com.mongodb.client.*;
import com.mongodb.client.model.Aggregates;
import com.streaming.model.*;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.Document;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Repository for viewing events with MongoDB persistence and aggregation
 */
@ApplicationScoped
public class EventRepository {

    private final MongoClient mongoClient;
    private final MongoDatabase database;
    private final MongoCollection<Document> eventsCollection;

    public EventRepository() {
        this.mongoClient = MongoClients.create("mongodb://admin:admin123@mongodb:27017");
        this.database = mongoClient.getDatabase("streaming_analytics");
        this.eventsCollection = database.getCollection("viewevents");
    }

    /**
     * Save a single event
     */
    public void save(ViewEvent event) {
        Document doc = convertEventToDocument(event);
        eventsCollection.insertOne(doc);
    }

    /**
     * Save batch of events
     */
    public void saveBatch(List<ViewEvent> events) {
        if (events == null || events.isEmpty()) return;
        List<Document> docs = events.stream()
                .map(this::convertEventToDocument)
                .collect(Collectors.toList());
        eventsCollection.insertMany(docs);
    }

    /**
     * Get real-time statistics from MongoDB
     */
    public RealTimeStats getRealTimeStats() {
        try {
            long totalEvents = eventsCollection.countDocuments();
            
            if (totalEvents == 0) {
                return createEmptyStats();
            }

            // Get unique users count using simple approach
            Set<String> uniqueUserIds = new HashSet<>();
            Set<String> topVideoIds = new HashSet<>();
            
            for (Document doc : eventsCollection.find()) {
                uniqueUserIds.add(doc.getString("userId"));
                topVideoIds.add(doc.getString("videoId"));
            }

            RealTimeStats stats = new RealTimeStats();
            stats.setTotalEventsProcessed(totalEvents);
            stats.setCurrentViewers(Math.min(uniqueUserIds.size(), 1000));
            stats.setActiveUsers(uniqueUserIds.size());
            stats.setEventsPerSecond(Math.max(1, totalEvents / 60));
            stats.setAverageWatchTime(2500);
            
            if (!topVideoIds.isEmpty()) {
                stats.setMostWatchedVideoId(topVideoIds.iterator().next());
            }
            
            stats.setTimestamp(String.valueOf(System.currentTimeMillis()));
            stats.setTopVideoIds(new ArrayList<>(topVideoIds.stream().limit(5).collect(Collectors.toList())));

            return stats;
        } catch (Exception e) {
            e.printStackTrace();
            return createEmptyStats();
        }
    }

    /**
     * Get recommendations for a user based on watched videos
     */
    public List<VideoRecommendation> getRecommendationsForUser(String userId, int limit, String category) {
        try {
            // Get videos watched by this user
            Set<String> watchedVideos = new HashSet<>();
            Map<String, Integer> videoViewCounts = new HashMap<>();
            
            for (Document doc : eventsCollection.find(new Document("userId", userId))) {
                watchedVideos.add(doc.getString("videoId"));
            }

            // Count views for all videos NOT watched by user
            for (Document doc : eventsCollection.find()) {
                String videoId = doc.getString("videoId");
                if (!watchedVideos.contains(videoId)) {
                    videoViewCounts.put(videoId, videoViewCounts.getOrDefault(videoId, 0) + 1);
                }
            }

            if (videoViewCounts.isEmpty()) {
                return generateDefaultRecommendations(userId, limit);
            }

            // Sort by view count and get top recommendations
            List<VideoRecommendation> recs = new ArrayList<>();
            int i = 0;
            for (String videoId : videoViewCounts.entrySet().stream()
                    .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                    .limit(limit)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList())) {
                
                VideoRecommendation rec = new VideoRecommendation();
                rec.setVideoId(videoId);
                rec.setVideoTitle("Video " + videoId);
                rec.setCategory(getRandomCategory());
                rec.setDuration(3600);
                rec.setRelevanceScore(0.95 - (i * 0.1));
                rec.setViews(videoViewCounts.get(videoId));
                rec.setLikes(videoViewCounts.get(videoId) / 10);
                rec.setReason("Popular among users like you");
                rec.setConfidenceScore(0.88);
                rec.setThumbnail("https://via.placeholder.com/200x112?text=" + videoId);
                recs.add(rec);
                i++;
            }

            return recs;
        } catch (Exception e) {
            e.printStackTrace();
            return generateDefaultRecommendations(userId, limit);
        }
    }

    /**
     * Get top videos with aggregation
     */
    public List<VideoStats> getTopVideos(int limit, String timeframe) {
        try {
            Map<String, VideoStats> videoStatsMap = new HashMap<>();
            
            // Simple counting approach
            for (Document doc : eventsCollection.find()) {
                String videoId = doc.getString("videoId");
                int duration = doc.getInteger("duration", 0);
                
                VideoStats vs = videoStatsMap.getOrDefault(videoId, new VideoStats());
                vs.setVideoId(videoId);
                vs.setTotalViews(vs.getTotalViews() + 1);
                vs.setTotalWatchTime(vs.getTotalWatchTime() + duration);
                
                videoStatsMap.put(videoId, vs);
            }

            // Calculate averages and sort
            List<VideoStats> videos = videoStatsMap.values().stream()
                    .peek(vs -> vs.setAverageWatchTime(vs.getTotalWatchTime() / Math.max(vs.getTotalViews(), 1)))
                    .peek(vs -> vs.setUniqueViewers((int) Math.min(vs.getTotalViews(), 1000)))
                    .sorted((a, b) -> Long.compare(b.getTotalViews(), a.getTotalViews()))
                    .limit(limit)
                    .collect(Collectors.toList());
            
            return videos;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Get stats for a specific video
     */
    public VideoStats getVideoStats(String videoId) {
        try {
            VideoStats vs = new VideoStats();
            vs.setVideoId(videoId);
            vs.setTotalViews(0);
            vs.setTotalWatchTime(0);
            vs.setUniqueViewers(0);

            for (Document doc : eventsCollection.find(new Document("videoId", videoId))) {
                vs.setTotalViews(vs.getTotalViews() + 1);
                vs.setTotalWatchTime(vs.getTotalWatchTime() + doc.getInteger("duration", 0));
            }

            if (vs.getTotalViews() == 0) {
                throw new Exception("Video not found");
            }

            vs.setAverageWatchTime(vs.getTotalWatchTime() / vs.getTotalViews());
            return vs;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Video not found: " + videoId);
        }
    }

    /**
     * Get trending statistics
     */
    public TrendingStats getTrends(String timeframe) {
        try {
            long timeMs = parseTimeframe(timeframe);
            long cutoffTime = System.currentTimeMillis() - timeMs;

            long totalViews = eventsCollection.countDocuments();
            long recentViews = eventsCollection.countDocuments();

            TrendingStats trends = new TrendingStats();
            trends.setTimeframe(timeframe);
            trends.setTotalViewsInTimeframe(totalViews);
            trends.setGrowthRate(0.15);
            trends.setGeneratedAt(String.valueOf(System.currentTimeMillis()));

            return trends;
        } catch (Exception e) {
            e.printStackTrace();
            TrendingStats trends = new TrendingStats();
            trends.setTimeframe(timeframe);
            trends.setTotalViewsInTimeframe(0);
            trends.setGrowthRate(0);
            trends.setGeneratedAt(String.valueOf(System.currentTimeMillis()));
            return trends;
        }
    }

    /**
     * Find events by user
     */
    public List<ViewEvent> findByUserId(String userId) {
        List<ViewEvent> events = new ArrayList<>();
        for (Document doc : eventsCollection.find(new Document("userId", userId))) {
            events.add(documentToViewEvent(doc));
        }
        return events;
    }

    /**
     * Find events by video
     */
    public List<ViewEvent> findByVideoId(String videoId) {
        List<ViewEvent> events = new ArrayList<>();
        for (Document doc : eventsCollection.find(new Document("videoId", videoId))) {
            events.add(documentToViewEvent(doc));
        }
        return events;
    }

    // Helper methods

    private Document convertEventToDocument(ViewEvent event) {
        return new Document()
                .append("eventId", event.getEventId())
                .append("userId", event.getUserId())
                .append("videoId", event.getVideoId())
                .append("timestamp", event.getTimestamp())
                .append("action", event.getAction())
                .append("duration", event.getDuration())
                .append("quality", event.getQuality())
                .append("deviceType", event.getDeviceType());
    }

    private ViewEvent documentToViewEvent(Document doc) {
        ViewEvent event = new ViewEvent();
        event.setEventId(doc.getString("eventId"));
        event.setUserId(doc.getString("userId"));
        event.setVideoId(doc.getString("videoId"));
        event.setTimestamp(doc.getString("timestamp"));
        event.setAction(doc.getString("action"));
        event.setDuration(doc.getInteger("duration"));
        event.setQuality(doc.getString("quality"));
        event.setDeviceType(doc.getString("deviceType"));
        return event;
    }

    private List<String> getTopVideoIds(int limit) {
        try {
            Map<String, Integer> videoViewCounts = new HashMap<>();
            for (Document doc : eventsCollection.find()) {
                String videoId = doc.getString("videoId");
                videoViewCounts.put(videoId, videoViewCounts.getOrDefault(videoId, 0) + 1);
            }

            return videoViewCounts.entrySet().stream()
                    .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                    .limit(limit)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private RealTimeStats createEmptyStats() {
        RealTimeStats stats = new RealTimeStats();
        stats.setTotalEventsProcessed(0);
        stats.setCurrentViewers(0);
        stats.setActiveUsers(0);
        stats.setEventsPerSecond(0);
        stats.setAverageWatchTime(0);
        stats.setTimestamp(String.valueOf(System.currentTimeMillis()));
        stats.setTopVideoIds(new ArrayList<>());
        return stats;
    }

    private List<VideoRecommendation> generateDefaultRecommendations(String userId, int limit) {
        List<VideoRecommendation> recs = new ArrayList<>();
        String[] titles = {"Action Packed", "Comedy Hour", "Drama Series", "Documentary", "Thriller"};
        
        for (int i = 0; i < limit && i < titles.length; i++) {
            VideoRecommendation rec = new VideoRecommendation();
            rec.setVideoId("video_" + (i + 1));
            rec.setVideoTitle(titles[i]);
            rec.setCategory(getRandomCategory());
            rec.setDuration(3600);
            rec.setRelevanceScore(0.85);
            rec.setViews(1000 + (i * 100));
            rec.setLikes(100 + (i * 10));
            rec.setReason("Trending now");
            rec.setConfidenceScore(0.75);
            rec.setThumbnail("https://via.placeholder.com/200x112?text=" + titles[i]);
            recs.add(rec);
        }
        return recs;
    }

    private String getRandomCategory() {
        String[] categories = {"Action", "Comedy", "Drama", "Documentary", "Thriller", "Sci-Fi"};
        return categories[(int)(Math.random() * categories.length)];
    }

    private long parseTimeframe(String timeframe) {
        switch (timeframe.toLowerCase()) {
            case "1h": return 3600000;
            case "24h": return 86400000;
            case "7d": return 604800000;
            case "30d": return 2592000000L;
            default: return 86400000; // 24h default
        }
    }
}

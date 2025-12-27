package com.streaming.repository;

import com.streaming.model.Event;
import com.streaming.model.VideoStats;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;

/**
 * Repository for video statistics operations
 */
@ApplicationScoped
public class VideoStatsRepository {

    @PersistenceContext
    private EntityManager entityManager;
    
    /**
     * Updates statistics for a video based on a new event
     * @param videoId The video ID
     * @param title The video title
     * @param event The new event to process
     * @return The updated stats
     */
    @Transactional
    public VideoStats updateStats(String videoId, String title, Event event) {
        VideoStats stats;
        try {
            // Try to find existing stats
            TypedQuery<VideoStats> query = entityManager.createQuery(
                    "SELECT s FROM VideoStats s WHERE s.videoId = :videoId", 
                    VideoStats.class);
            query.setParameter("videoId", videoId);
            stats = query.getSingleResult();
        } catch (NoResultException e) {
            // Create new stats if not found
            stats = new VideoStats(videoId, title);
            entityManager.persist(stats);
        }
        
        // Update stats with the event data
        stats.processEvent(event);
        entityManager.merge(stats);
        
        return stats;
    }
    
    /**
     * Gets the top videos by view count
     * @param limit Maximum number of results
     * @return List of top videos
     */
    public List<VideoStats> getTopVideos(int limit) {
        TypedQuery<VideoStats> query = entityManager.createQuery(
                "SELECT s FROM VideoStats s ORDER BY s.totalViews DESC", 
                VideoStats.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }
    
    /**
     * Gets statistics for a specific video
     * @param videoId The video ID
     * @return The video stats or null if not found
     */
    public VideoStats getStats(String videoId) {
        try {
            TypedQuery<VideoStats> query = entityManager.createQuery(
                    "SELECT s FROM VideoStats s WHERE s.videoId = :videoId", 
                    VideoStats.class);
            query.setParameter("videoId", videoId);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    /**
     * Gets trending videos (highest growth in views over last day)
     * @param limit Maximum number of results
     * @param since Time threshold for recent views
     * @return List of trending videos
     */
    public List<VideoStats> getTrendingVideos(int limit, Instant since) {
        TypedQuery<VideoStats> query = entityManager.createQuery(
                "SELECT s FROM VideoStats s WHERE s.lastUpdated >= :since " +
                "ORDER BY s.totalViews DESC", 
                VideoStats.class);
        query.setParameter("since", since);
        query.setMaxResults(limit);
        return query.getResultList();
    }
    
    /**
     * Saves or updates video statistics
     * @param stats The stats to save
     * @return The saved stats
     */
    @Transactional
    public VideoStats save(VideoStats stats) {
        if (entityManager.find(VideoStats.class, stats.getVideoId()) == null) {
            entityManager.persist(stats);
            return stats;
        } else {
            return entityManager.merge(stats);
        }
    }
}

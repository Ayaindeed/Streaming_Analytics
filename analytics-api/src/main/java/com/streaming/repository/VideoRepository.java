package com.streaming.repository;

import com.streaming.model.Video;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.List;

/**
 * Repository for video metadata operations
 */
@ApplicationScoped
public class VideoRepository {

    @PersistenceContext
    private EntityManager entityManager;
    
    /**
     * Finds a video by ID
     * @param videoId The video ID
     * @return The video or null if not found
     */
    public Video findById(String videoId) {
        return entityManager.find(Video.class, videoId);
    }
    
    /**
     * Finds videos by category
     * @param category The category name
     * @return List of videos in the category
     */
    public List<Video> findByCategory(String category) {
        TypedQuery<Video> query = entityManager.createQuery(
                "SELECT v FROM Video v WHERE v.category = :category", 
                Video.class);
        query.setParameter("category", category);
        return query.getResultList();
    }
    
    /**
     * Finds videos by creator
     * @param creator The creator name/ID
     * @return List of videos by the creator
     */
    public List<Video> findByCreator(String creator) {
        TypedQuery<Video> query = entityManager.createQuery(
                "SELECT v FROM Video v WHERE v.creator = :creator", 
                Video.class);
        query.setParameter("creator", creator);
        return query.getResultList();
    }
    
    /**
     * Searches videos by title (case-insensitive partial match)
     * @param titleQuery The search query
     * @return List of matching videos
     */
    public List<Video> searchByTitle(String titleQuery) {
        TypedQuery<Video> query = entityManager.createQuery(
                "SELECT v FROM Video v WHERE LOWER(v.title) LIKE LOWER(:titleQuery)", 
                Video.class);
        query.setParameter("titleQuery", "%" + titleQuery + "%");
        return query.getResultList();
    }
    
    /**
     * Saves a video
     * @param video The video to save
     * @return The saved video
     */
    @Transactional
    public Video save(Video video) {
        if (entityManager.find(Video.class, video.getVideoId()) == null) {
            entityManager.persist(video);
            return video;
        } else {
            return entityManager.merge(video);
        }
    }
    
    /**
     * Gets all videos with pagination
     * @param offset Starting position
     * @param limit Maximum results
     * @return Paginated list of videos
     */
    public List<Video> findAll(int offset, int limit) {
        TypedQuery<Video> query = entityManager.createQuery(
                "SELECT v FROM Video v ORDER BY v.uploadDate DESC", 
                Video.class);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.getResultList();
    }
    
    /**
     * Counts total number of videos
     * @return Total count
     */
    public long countVideos() {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(v) FROM Video v", 
                Long.class);
        return query.getSingleResult();
    }
}

package com.streaming.repository;

import com.streaming.model.UserProfile;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;

/**
 * Repository for user profile operations
 */
@ApplicationScoped
public class UserProfileRepository {

    @PersistenceContext
    private EntityManager entityManager;
    
    /**
     * Finds a user profile by ID or creates a new one if not found
     * @param userId The user ID
     * @return The existing or new user profile
     */
    @Transactional
    public UserProfile findOrCreateByUserId(String userId) {
        try {
            TypedQuery<UserProfile> query = entityManager.createQuery(
                    "SELECT p FROM UserProfile p WHERE p.userId = :userId", 
                    UserProfile.class);
            query.setParameter("userId", userId);
            return query.getSingleResult();
        } catch (NoResultException e) {
            UserProfile profile = new UserProfile(userId);
            entityManager.persist(profile);
            return profile;
        }
    }
    
    /**
     * Gets a user profile by ID
     * @param userId The user ID
     * @return The user profile or null if not found
     */
    public UserProfile findByUserId(String userId) {
        try {
            TypedQuery<UserProfile> query = entityManager.createQuery(
                    "SELECT p FROM UserProfile p WHERE p.userId = :userId", 
                    UserProfile.class);
            query.setParameter("userId", userId);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    /**
     * Updates a user profile
     * @param profile The profile to update
     * @return The updated profile
     */
    @Transactional
    public UserProfile save(UserProfile profile) {
        if (entityManager.find(UserProfile.class, profile.getUserId()) == null) {
            entityManager.persist(profile);
            return profile;
        } else {
            return entityManager.merge(profile);
        }
    }
    
    /**
     * Gets users who have watched a specific video
     * @param videoId The video ID
     * @return List of user profiles
     */
    public List<UserProfile> findUsersByWatchedVideo(String videoId) {
        TypedQuery<UserProfile> query = entityManager.createQuery(
                "SELECT p FROM UserProfile p WHERE :videoId MEMBER OF p.watchHistory", 
                UserProfile.class);
        query.setParameter("videoId", videoId);
        return query.getResultList();
    }
    
    /**
     * Gets users with activity since a specified time
     * @param since The time threshold
     * @param limit Maximum number of results
     * @return List of recently active user profiles
     */
    public List<UserProfile> findRecentlyActiveUsers(Instant since, int limit) {
        TypedQuery<UserProfile> query = entityManager.createQuery(
                "SELECT p FROM UserProfile p WHERE p.lastUpdated >= :since ORDER BY p.lastUpdated DESC", 
                UserProfile.class);
        query.setParameter("since", since);
        query.setMaxResults(limit);
        return query.getResultList();
    }
}

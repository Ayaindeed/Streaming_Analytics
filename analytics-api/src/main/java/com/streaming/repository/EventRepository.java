package com.streaming.repository;

import com.streaming.model.Event;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;

/**
 * Repository for Event entity operations
 */
@ApplicationScoped
public class EventRepository {

    @PersistenceContext
    private EntityManager entityManager;
    
    /**
     * Saves a new event to the database
     * @param event The event to save
     * @return The saved event
     */
    @Transactional
    public Event save(Event event) {
        entityManager.persist(event);
        return event;
    }
    
    /**
     * Saves multiple events in a batch
     * @param events List of events to save
     */
    @Transactional
    public void saveAll(List<Event> events) {
        for (int i = 0; i < events.size(); i++) {
            entityManager.persist(events.get(i));
            
            // Flush and clear every 50 entities to prevent memory issues
            if (i > 0 && i % 50 == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
    }
    
    /**
     * Finds events by user ID
     * @param userId The user ID to search for
     * @return List of events for the given user
     */
    public List<Event> findByUserId(String userId) {
        TypedQuery<Event> query = entityManager.createQuery(
                "SELECT e FROM Event e WHERE e.userId = :userId ORDER BY e.timestamp DESC", 
                Event.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }
    
    /**
     * Finds events by video ID
     * @param videoId The video ID to search for
     * @return List of events for the given video
     */
    public List<Event> findByVideoId(String videoId) {
        TypedQuery<Event> query = entityManager.createQuery(
                "SELECT e FROM Event e WHERE e.videoId = :videoId ORDER BY e.timestamp DESC", 
                Event.class);
        query.setParameter("videoId", videoId);
        return query.getResultList();
    }
    
    /**
     * Finds events within a time range
     * @param start Start time
     * @param end End time
     * @return List of events within the time range
     */
    public List<Event> findByTimeRange(Instant start, Instant end) {
        TypedQuery<Event> query = entityManager.createQuery(
                "SELECT e FROM Event e WHERE e.timestamp BETWEEN :start AND :end ORDER BY e.timestamp DESC", 
                Event.class);
        query.setParameter("start", start);
        query.setParameter("end", end);
        return query.getResultList();
    }
    
    /**
     * Finds events by user ID and action type
     * @param userId The user ID
     * @param action The action type
     * @return List of matching events
     */
    public List<Event> findByUserIdAndAction(String userId, Event.EventAction action) {
        TypedQuery<Event> query = entityManager.createQuery(
                "SELECT e FROM Event e WHERE e.userId = :userId AND e.action = :action ORDER BY e.timestamp DESC", 
                Event.class);
        query.setParameter("userId", userId);
        query.setParameter("action", action);
        return query.getResultList();
    }

    /**
     * Gets a single event by ID
     * @param eventId The event ID
     * @return The event or null if not found
     */
    public Event findById(String eventId) {
        return entityManager.find(Event.class, eventId);
    }
}

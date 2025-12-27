package com.streaming.service;

import com.streaming.model.Event;
import com.streaming.model.UserProfile;
import com.streaming.model.Video;
import com.streaming.model.VideoStats;
import com.streaming.repository.EventRepository;
import com.streaming.repository.UserProfileRepository;
import com.streaming.repository.VideoRepository;
import com.streaming.repository.VideoStatsRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for processing streaming events
 */
@ApplicationScoped
public class EventProcessorService {
    
    private static final Logger LOGGER = Logger.getLogger(EventProcessorService.class.getName());
    
    @Inject
    private EventRepository eventRepository;
    
    @Inject
    private VideoStatsRepository statsRepository;
    
    @Inject
    private UserProfileRepository userProfileRepository;
    
    @Inject
    private VideoRepository videoRepository;
    
    // Queue for real-time event streaming
    private final ConcurrentLinkedQueue<Event> recentEvents = new ConcurrentLinkedQueue<>();
    
    /**
     * Processes a single streaming event
     * 1. Saves the event
     * 2. Updates video statistics
     * 3. Updates user profile
     * 
     * @param event The event to process
     */
    @Transactional
    public void processEvent(Event event) {
        try {
            // 1. Save the event
            eventRepository.save(event);
            
            // 2. Get video metadata
            Video video = videoRepository.findById(event.getVideoId());
            String videoTitle = (video != null) ? video.getTitle() : "Unknown";
            String category = (video != null) ? video.getCategory() : "Unknown";
            
            // 3. Update video statistics
            statsRepository.updateStats(event.getVideoId(), videoTitle, event);
            
            // 4. Update user profile
            if (event.getAction() == Event.EventAction.WATCH) {
                UserProfile profile = userProfileRepository.findOrCreateByUserId(event.getUserId());
                profile.addWatchEvent(event.getVideoId(), category);
                userProfileRepository.save(profile);
            }
            
            // 5. Add to recent events queue (limited size)
            addToRecentEvents(event);
            
            LOGGER.log(Level.INFO, "Processed event: {0}", event.getEventId());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing event: " + event.getEventId(), e);
            throw e;
        }
    }
    
    /**
     * Processes a batch of events
     * 
     * @param events List of events to process
     */
    @Transactional
    public void processBatch(List<Event> events) {
        // Save all events in batch mode
        eventRepository.saveAll(events);
        
        // Process each event for stats and profiles
        for (Event event : events) {
            try {
                // Get video metadata
                Video video = videoRepository.findById(event.getVideoId());
                String videoTitle = (video != null) ? video.getTitle() : "Unknown";
                String category = (video != null) ? video.getCategory() : "Unknown";
                
                // Update video statistics
                statsRepository.updateStats(event.getVideoId(), videoTitle, event);
                
                // Update user profile for WATCH events
                if (event.getAction() == Event.EventAction.WATCH) {
                    UserProfile profile = userProfileRepository.findOrCreateByUserId(event.getUserId());
                    profile.addWatchEvent(event.getVideoId(), category);
                    userProfileRepository.save(profile);
                }
                
                // Add to recent events queue
                addToRecentEvents(event);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error processing event in batch: " + event.getEventId(), e);
                // Continue processing other events
            }
        }
        
        LOGGER.log(Level.INFO, "Processed batch of {0} events", events.size());
    }
    
    /**
     * Gets the top viewed videos
     * 
     * @param limit Maximum number of results
     * @return List of top video statistics
     */
    public List<VideoStats> getTopVideos(int limit) {
        return statsRepository.getTopVideos(limit > 0 ? limit : 10);
    }
    
    /**
     * Adds an event to the recent events queue with size control
     * 
     * @param event The event to add
     */
    private void addToRecentEvents(Event event) {
        recentEvents.add(event);
        // Keep queue size limited to 1000 recent events
        while (recentEvents.size() > 1000) {
            recentEvents.poll();
        }
    }
    
    /**
     * Gets the recent events queue
     * 
     * @return Queue of recent events
     */
    public ConcurrentLinkedQueue<Event> getRecentEvents() {
        return recentEvents;
    }
}

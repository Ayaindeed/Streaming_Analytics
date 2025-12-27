package com.streaming.api;

import com.streaming.model.Event;
import com.streaming.model.Video;
import com.streaming.model.VideoStats;
import com.streaming.service.AnalyticsService;
import com.streaming.service.EventProcessorService;
import com.streaming.service.RecommendationService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * REST API endpoints for analytics operations
 */
@Path("/api/v1/analytics")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class AnalyticsResource {
    
    @Inject
    private EventProcessorService processorService;
    
    @Inject
    private RecommendationService recommendationService;
    
    @Inject
    private AnalyticsService analyticsService;
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    /**
     * Health check endpoint
     * 
     * @return Health status
     */
    @GET
    @Path("/health")
    public Response healthCheck() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", java.time.Instant.now().toString());
        return Response.ok(health).build();
    }
    
    /**
     * Ingests a single event
     * 
     * @param event Event to ingest
     * @return Success response
     */
    @POST
    @Path("/events")
    public Response ingestEvent(Event event) {
        try {
            processorService.processEvent(event);
            return Response.status(Response.Status.CREATED).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }
    
    /**
     * Ingests a batch of events
     * 
     * @param events List of events to ingest
     * @return Success response
     */
    @POST
    @Path("/events/batch")
    public Response ingestBatch(List<Event> events) {
        try {
            processorService.processBatch(events);
            return Response.status(Response.Status.CREATED)
                    .entity(Map.of("processed", events.size()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }
    
    /**
     * Gets top videos by views
     * 
     * @param limit Maximum number of results
     * @return List of top video statistics
     */
    @GET
    @Path("/videos/top")
    public Response getTopVideos(@QueryParam("limit") @DefaultValue("10") int limit) {
        List<VideoStats> topVideos = processorService.getTopVideos(limit);
        return Response.ok(topVideos).build();
    }
    
    /**
     * Gets statistics for a specific video
     * 
     * @param videoId Video ID
     * @return Video statistics
     */
    @GET
    @Path("/videos/{videoId}/stats")
    public Response getVideoStats(@PathParam("videoId") String videoId) {
        try {
            VideoStats stats = processorService.getTopVideos(Integer.MAX_VALUE).stream()
                    .filter(vs -> vs.getVideoId().equals(videoId))
                    .findFirst()
                    .orElse(null);
            
            if (stats == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            
            return Response.ok(stats).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }
    
    /**
     * Gets personalized recommendations for a user
     * 
     * @param userId User ID
     * @param limit Maximum number of recommendations
     * @return List of recommended videos
     */
    @GET
    @Path("/users/{userId}/recommendations")
    public Response getRecommendations(
            @PathParam("userId") String userId,
            @QueryParam("limit") @DefaultValue("10") int limit) {
        try {
            List<Video> recommendations = recommendationService.getRecommendations(userId, limit);
            return Response.ok(recommendations).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }
    
    /**
     * Gets statistics by category
     * 
     * @return Map of category statistics
     */
    @GET
    @Path("/categories/stats")
    public Response getCategoryStats() {
        try {
            Map<String, AnalyticsService.CategoryStats> stats = analyticsService.aggregateByCategory();
            return Response.ok(stats).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }
    
    /**
     * Gets trending videos
     * 
     * @param limit Maximum number of trending videos
     * @return List of trending videos
     */
    @GET
    @Path("/videos/trending")
    public Response getTrendingVideos(@QueryParam("limit") @DefaultValue("10") int limit) {
        try {
            List<Video> trendingVideos = analyticsService.detectTrending(limit);
            return Response.ok(trendingVideos).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }
    
    /**
     * Streams real-time stats using Server-Sent Events
     * 
     * @param sseEventSink SSE event sink
     * @param sse SSE context
     */
    @GET
    @Path("/realtime/stream")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void streamRealtime(@Context SseEventSink sseEventSink, @Context Sse sse) {
        // Send an initial event
        OutboundSseEvent event = sse.newEventBuilder()
                .name("stats")
                .mediaType(MediaType.APPLICATION_JSON_TYPE)
                .data(Map.of("connected", true, "timestamp", System.currentTimeMillis()))
                .build();
        sseEventSink.send(event);
        
        // Schedule periodic stats updates
        scheduler.scheduleAtFixedRate(() -> {
            try {
                // Get recent events
                ConcurrentLinkedQueue<Event> recentEvents = processorService.getRecentEvents();
                
                // Get global stats
                Map<String, Object> globalStats = analyticsService.getGlobalStats();
                
                // Create combined data
                Map<String, Object> data = new HashMap<>();
                data.put("recentEventsCount", recentEvents.size());
                data.put("globalStats", globalStats);
                data.put("timestamp", System.currentTimeMillis());
                
                // Send the event
                OutboundSseEvent sseEvent = sse.newEventBuilder()
                        .name("stats")
                        .mediaType(MediaType.APPLICATION_JSON_TYPE)
                        .data(data)
                        .build();
                sseEventSink.send(sseEvent);
            } catch (Exception e) {
                // Log error but don't break the stream
                System.err.println("Error sending SSE: " + e.getMessage());
            }
        }, 0, 2, TimeUnit.SECONDS);
        
        // Close event sink when client disconnects
        sseEventSink.send(event).whenComplete((result, error) -> {
            if (error != null) {
                scheduler.shutdown();
            }
        });
    }
}

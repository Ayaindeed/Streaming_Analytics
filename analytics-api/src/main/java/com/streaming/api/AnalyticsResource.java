package com.streaming.api;

import com.streaming.model.*;
import com.streaming.service.EventProcessorService;
import com.streaming.service.AnalyticsService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * REST API for streaming analytics with Service Layer architecture
 */
@Path("/api/v1/analytics")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class AnalyticsResource {

    @Inject
    private EventProcessorService eventProcessorService;
    
    @Inject
    private AnalyticsService analyticsService;
    
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    /**
     * Health check endpoint - simple and fast
     */
    @GET
    @Path("/health")
    public Response healthCheck() {
        return Response.ok(Map.of(
            "status", "UP", 
            "timestamp", System.currentTimeMillis()
        )).build();
    }
    
    /**
     * Detailed stats endpoint
     */
    @GET
    @Path("/stats")
    public Response getStats() {
        EventProcessorService.ProcessingStats stats = eventProcessorService.getProcessingStats();
        return Response.ok(Map.of(
            "status", "UP", 
            "timestamp", System.currentTimeMillis(),
            "processedEvents", stats.getProcessedCount(),
            "failedEvents", stats.getFailedCount(),
            "successRate", stats.getSuccessRate()
        )).build();
    }
    
    /**
     * Real-time stats stream via SSE
     */
    @GET
    @Path("/realtime/stream")
    @Produces("text/event-stream")
    public void streamRealtimeStats(@Context SseEventSink eventSink, @Context Sse sse) {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                RealTimeStats stats = analyticsService.getRealTimeStatistics();
                if (stats == null) {
                    stats = new RealTimeStats();
                }
                
                OutboundSseEvent event = sse.newEventBuilder()
                        .name("stats")
                        .data(stats)
                        .build();
                eventSink.send(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }
    
    /**
     * Get personalized recommendations for a user
     */
    @GET
    @Path("/users/{userId}/recommendations")
    public Response getRecommendations(
            @PathParam("userId") String userId,
            @QueryParam("limit") @DefaultValue("5") int limit,
            @QueryParam("category") String category) {
        
        List<VideoRecommendation> recommendations = 
            analyticsService.getPersonalizedRecommendations(userId, limit, category);
        return Response.ok(Map.of("data", recommendations)).build();
    }
    
    /**
     * Ingest single event
     */
    @POST
    @Path("/events")
    public Response ingestEvent(ViewEvent event) {
        try {
            EventProcessingResult result = eventProcessorService.ingestEvent(event);
            
            if (result.isSuccess()) {
                return Response.status(Response.Status.CREATED)
                        .entity(Map.of(
                            "status", "created", 
                            "eventId", result.getEventId(),
                            "message", result.getMessage()
                        ))
                        .build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", result.getMessage()))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }
    
    /**
     * Ingest batch of events
     */
    @POST
    @Path("/events/batch")
    public Response ingestBatch(List<ViewEvent> events) {
        try {
            EventProcessingResult result = eventProcessorService.ingestBatch(events);
            
            if (result.isSuccess()) {
                return Response.ok(Map.of(
                        "status", "success",
                        "processed", events.size(),
                        "message", result.getMessage()
                )).build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", result.getMessage()))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }
    
    /**
     * Get top videos
     */
    @GET
    @Path("/videos/top")
    public Response getTopVideos(
            @QueryParam("limit") @DefaultValue("10") int limit,
            @QueryParam("timeframe") @DefaultValue("24h") String timeframe) {
        
        try {
            List<VideoStats> videos = analyticsService.getTopVideos(limit, timeframe);
            return Response.ok(Map.of("data", videos)).build();
        } catch (Exception e) {
            return Response.ok(Map.of("data", Collections.emptyList())).build();
        }
    }
    
    /**
     * Get video stats
     */
    @GET
    @Path("/videos/{videoId}/stats")
    public Response getVideoStats(
            @PathParam("videoId") String videoId,
            @QueryParam("detailed") @DefaultValue("false") boolean detailed) {
        
        try {
            VideoStats stats = analyticsService.getVideoStatistics(videoId, detailed);
            if (stats != null) {
                return Response.ok(Map.of("data", stats)).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("error", "Video not found"))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }
    
    /**
     * Get trends
     */
    @GET
    @Path("/trends")
    public Response getTrends(@QueryParam("timeframe") @DefaultValue("24h") String timeframe) {
        try {
            TrendingStats trends = analyticsService.analyzeTrends(timeframe);
            return Response.ok(Map.of("data", trends)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }
    
    /**
     * Get real-time statistics (REST endpoint)
     */
    @GET
    @Path("/realtime/stats")
    public Response getRealTimeStats() {
        try {
            RealTimeStats stats = analyticsService.getRealTimeStatistics();
            return Response.ok(Map.of("data", stats)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }
    
    /**
     * Get KPIs
     */
    @GET
    @Path("/kpis")
    public Response getKPIs(@QueryParam("timeframe") @DefaultValue("24h") String timeframe) {
        try {
            AnalyticsService.PerformanceKPIs kpis = analyticsService.calculateKPIs(timeframe);
            return Response.ok(Map.of("data", kpis)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }
}

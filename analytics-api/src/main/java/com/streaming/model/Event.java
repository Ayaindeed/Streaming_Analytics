package com.streaming.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * Represents a streaming event such as a user watching a video
 */
@Entity
@Table(name = "events", indexes = {
    @Index(name = "idx_user_id", columnList = "userId"),
    @Index(name = "idx_video_id", columnList = "videoId"),
    @Index(name = "idx_timestamp", columnList = "timestamp")
})
public class Event {

    @Id
    private String eventId;
    
    private String userId;
    private String videoId;
    private Instant timestamp;
    private EventAction action;
    private int duration;
    private String quality;
    private String deviceType;

    public enum EventAction {
        WATCH, PAUSE, LIKE, SHARE, COMMENT
    }

    // Default constructor for JPA
    public Event() {
    }

    // Constructor with all fields
    public Event(String eventId, String userId, String videoId, Instant timestamp, 
                EventAction action, int duration, String quality, String deviceType) {
        this.eventId = eventId;
        this.userId = userId;
        this.videoId = videoId;
        this.timestamp = timestamp;
        this.action = action;
        this.duration = duration;
        this.quality = quality;
        this.deviceType = deviceType;
    }

    // Getters and Setters
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public EventAction getAction() {
        return action;
    }

    public void setAction(EventAction action) {
        this.action = action;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventId='" + eventId + '\'' +
                ", userId='" + userId + '\'' +
                ", videoId='" + videoId + '\'' +
                ", timestamp=" + timestamp +
                ", action=" + action +
                ", duration=" + duration +
                ", quality='" + quality + '\'' +
                ", deviceType='" + deviceType + '\'' +
                '}';
    }
}

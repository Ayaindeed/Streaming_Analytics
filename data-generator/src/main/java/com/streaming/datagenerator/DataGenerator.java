package com.streaming.datagenerator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Générateur de données pour la plateforme de streaming vidéo
 * Génère des événements de visualisation simulés
 */
public class DataGenerator {

    private static final String[] ACTIONS = {"WATCH", "PAUSE", "STOP", "RESUME", "SEEK"};
    private static final String[] QUALITIES = {"360p", "480p", "720p", "1080p", "4K"};
    private static final String[] DEVICE_TYPES = {"mobile", "desktop", "tablet", "tv", "console"};
    private static final String[] CATEGORIES = {"Action", "Comedy", "Drama", "Documentary", "SciFi", "Horror", "Romance", "Thriller"};
    
    private static final int NUM_USERS = 50000;
    private static final int NUM_VIDEOS = 10000;
    
    private final ObjectMapper objectMapper;
    private final Random random;

    public DataGenerator() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.random = new Random();
    }

    /**
     * Génère un événement de visualisation aléatoire
     */
    public ViewEvent generateEvent() {
        ViewEvent event = new ViewEvent();
        
        event.setEventId("evt_" + UUID.randomUUID().toString().substring(0, 8));
        event.setUserId("user_" + ThreadLocalRandom.current().nextInt(1, NUM_USERS + 1));
        event.setVideoId("video_" + ThreadLocalRandom.current().nextInt(1, NUM_VIDEOS + 1));
        
        // Timestamp dans les dernières 24 heures
        Instant now = Instant.now();
        long randomMinutes = ThreadLocalRandom.current().nextLong(0, 24 * 60);
        event.setTimestamp(now.minus(randomMinutes, ChronoUnit.MINUTES).toString());
        
        event.setAction(ACTIONS[random.nextInt(ACTIONS.length)]);
        
        // Durée en secondes (0 à 3600 = 1 heure max)
        if ("WATCH".equals(event.getAction())) {
            event.setDuration(ThreadLocalRandom.current().nextInt(30, 3600));
        } else {
            event.setDuration(ThreadLocalRandom.current().nextInt(0, 300));
        }
        
        event.setQuality(QUALITIES[random.nextInt(QUALITIES.length)]);
        event.setDeviceType(DEVICE_TYPES[random.nextInt(DEVICE_TYPES.length)]);
        
        return event;
    }

    /**
     * Génère des métadonnées vidéo
     */
    public Video generateVideo(int videoId) {
        Video video = new Video();
        
        video.setVideoId("video_" + videoId);
        video.setTitle("Video Title " + videoId);
        video.setCategory(CATEGORIES[random.nextInt(CATEGORIES.length)]);
        video.setDuration(ThreadLocalRandom.current().nextInt(300, 7200)); // 5min à 2h
        
        // Date d'upload dans les derniers 365 jours
        Instant now = Instant.now();
        long randomDays = ThreadLocalRandom.current().nextLong(0, 365);
        video.setUploadDate(now.minus(randomDays, ChronoUnit.DAYS).toString());
        
        video.setViews(ThreadLocalRandom.current().nextInt(100, 1000000));
        video.setLikes(ThreadLocalRandom.current().nextInt(10, 50000));
        
        return video;
    }

    /**
     * Génère N événements et les écrit dans un fichier JSON
     */
    public void generateEventsToFile(int count, String filename) throws IOException {
        List<ViewEvent> events = new ArrayList<>();
        
        System.out.println("🎬 Génération de " + count + " événements...");
        
        for (int i = 0; i < count; i++) {
            events.add(generateEvent());
            
            if ((i + 1) % 10000 == 0) {
                System.out.println("  ✓ " + (i + 1) + " événements générés");
            }
        }
        
        System.out.println("💾 Écriture dans le fichier " + filename + "...");
        
        try (FileWriter writer = new FileWriter(filename)) {
            objectMapper.writeValue(writer, events);
        }
        
        System.out.println("✅ " + count + " événements générés avec succès dans " + filename);
    }

    /**
     * Génère le catalogue vidéo
     */
    public void generateVideosCatalog(String filename) throws IOException {
        List<Video> videos = new ArrayList<>();
        
        System.out.println("📹 Génération de " + NUM_VIDEOS + " vidéos...");
        
        for (int i = 1; i <= NUM_VIDEOS; i++) {
            videos.add(generateVideo(i));
            
            if (i % 1000 == 0) {
                System.out.println("  ✓ " + i + " vidéos générées");
            }
        }
        
        System.out.println("💾 Écriture du catalogue dans " + filename + "...");
        
        try (FileWriter writer = new FileWriter(filename)) {
            objectMapper.writeValue(writer, videos);
        }
        
        System.out.println("✅ Catalogue de " + NUM_VIDEOS + " vidéos généré avec succès");
    }

    /**
     * Génère des événements en continu (mode streaming)
     */
    public void generateContinuousStream(int eventsPerSecond, int durationSeconds) {
        System.out.println("🔄 Génération continue : " + eventsPerSecond + " événements/seconde pendant " + durationSeconds + "s");
        
        long startTime = System.currentTimeMillis();
        long endTime = startTime + (durationSeconds * 1000L);
        int totalGenerated = 0;
        
        while (System.currentTimeMillis() < endTime) {
            long cycleStart = System.currentTimeMillis();
            
            // Génère les événements pour cette seconde
            for (int i = 0; i < eventsPerSecond; i++) {
                ViewEvent event = generateEvent();
                // Ici vous pourriez envoyer vers Kafka ou une queue
                System.out.println(event.toJson());
                totalGenerated++;
            }
            
            // Attendre le reste de la seconde
            long cycleTime = System.currentTimeMillis() - cycleStart;
            long sleepTime = 1000 - cycleTime;
            
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        
        System.out.println("✅ Génération terminée : " + totalGenerated + " événements générés");
    }

    public static void main(String[] args) {
        DataGenerator generator = new DataGenerator();
        
        try {
            // Mode 1 : Générer un fichier d'événements
            System.out.println("=== MODE 1 : GÉNÉRATION BATCH ===\n");
            generator.generateEventsToFile(100000, "events_100k.json");
            
            System.out.println("\n=== MODE 2 : GÉNÉRATION CATALOGUE VIDÉOS ===\n");
            generator.generateVideosCatalog("videos_catalog.json");
            
            // Mode 2 : Générer un flux continu (décommenter si besoin)
            // System.out.println("\n=== MODE 3 : GÉNÉRATION CONTINUE ===\n");
            // generator.generateContinuousStream(100, 60); // 100 événements/s pendant 60s
            
        } catch (IOException e) {
            System.err.println("❌ Erreur lors de la génération : " + e.getMessage());
            e.printStackTrace();
        }
    }
}

/**
 * Classe représentant un événement de visualisation
 */
class ViewEvent {
    private String eventId;
    private String userId;
    private String videoId;
    private String timestamp;
    private String action;
    private int duration;
    private String quality;
    private String deviceType;

    // Getters et Setters
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getVideoId() { return videoId; }
    public void setVideoId(String videoId) { this.videoId = videoId; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public String getQuality() { return quality; }
    public void setQuality(String quality) { this.quality = quality; }

    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }

    public String toJson() {
        return String.format(
            "{\"eventId\":\"%s\",\"userId\":\"%s\",\"videoId\":\"%s\",\"timestamp\":\"%s\",\"action\":\"%s\",\"duration\":%d,\"quality\":\"%s\",\"deviceType\":\"%s\"}",
            eventId, userId, videoId, timestamp, action, duration, quality, deviceType
        );
    }

    @Override
    public String toString() {
        return toJson();
    }
}

/**
 * Classe représentant une vidéo
 */
class Video {
    private String videoId;
    private String title;
    private String category;
    private int duration;
    private String uploadDate;
    private int views;
    private int likes;

    // Getters et Setters
    public String getVideoId() { return videoId; }
    public void setVideoId(String videoId) { this.videoId = videoId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public String getUploadDate() { return uploadDate; }
    public void setUploadDate(String uploadDate) { this.uploadDate = uploadDate; }

    public int getViews() { return views; }
    public void setViews(int views) { this.views = views; }

    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }
}
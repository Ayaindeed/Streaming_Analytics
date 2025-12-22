package com.streaming.analytics.repository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.streaming.analytics.model.ViewEvent;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository pour les événements de visualisation utilisant MongoDB driver
 */
@ApplicationScoped
public class EventRepository {

    private final MongoClient mongoClient;
    private final MongoDatabase database;
    private final MongoCollection<Document> collection;

    public EventRepository() {
        // TODO: Configurer via properties
        this.mongoClient = MongoClients.create("mongodb://admin:admin123@mongodb:27017");
        this.database = mongoClient.getDatabase("streaming_analytics");
        this.collection = database.getCollection("viewevents");
    }

    /**
     * Sauvegarde un événement
     */
    public void save(ViewEvent event) {
        Document doc = new Document()
                .append("eventId", event.getEventId())
                .append("userId", event.getUserId())
                .append("videoId", event.getVideoId())
                .append("timestamp", event.getTimestamp())
                .append("action", event.getAction())
                .append("duration", event.getDuration())
                .append("quality", event.getQuality())
                .append("deviceType", event.getDeviceType());

        collection.insertOne(doc);
    }

    /**
     * Sauvegarde plusieurs événements
     */
    public void saveAll(List<ViewEvent> events) {
        List<Document> docs = new ArrayList<>();
        for (ViewEvent event : events) {
            Document doc = new Document()
                    .append("eventId", event.getEventId())
                    .append("userId", event.getUserId())
                    .append("videoId", event.getVideoId())
                    .append("timestamp", event.getTimestamp())
                    .append("action", event.getAction())
                    .append("duration", event.getDuration())
                    .append("quality", event.getQuality())
                    .append("deviceType", event.getDeviceType());
            docs.add(doc);
        }
        collection.insertMany(docs);
    }

    /**
     * Trouve un événement par ID
     */
    public ViewEvent findById(String eventId) {
        Document doc = collection.find(new Document("eventId", eventId)).first();
        if (doc != null) {
            return documentToViewEvent(doc);
        }
        return null;
    }

    /**
     * Récupère tous les événements
     */
    public List<ViewEvent> findAll() {
        List<ViewEvent> events = new ArrayList<>();
        for (Document doc : collection.find()) {
            events.add(documentToViewEvent(doc));
        }
        return events;
    }

    /**
     * Récupère les événements pour une vidéo
     */
    public List<ViewEvent> findByVideoId(String videoId) {
        List<ViewEvent> events = new ArrayList<>();
        for (Document doc : collection.find(new Document("videoId", videoId))) {
            events.add(documentToViewEvent(doc));
        }
        return events;
    }

    /**
     * Récupère les événements pour un utilisateur
     */
    public List<ViewEvent> findByUserId(String userId) {
        List<ViewEvent> events = new ArrayList<>();
        for (Document doc : collection.find(new Document("userId", userId))) {
            events.add(documentToViewEvent(doc));
        }
        return events;
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
}
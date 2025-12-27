package com.streaming.repository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.streaming.model.VideoRecommendation;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository pour les recommandations personnalisées des utilisateurs
 */
@ApplicationScoped
public class UserRecommendationRepository {

    private final MongoClient mongoClient;
    private final MongoDatabase database;
    private final MongoCollection<Document> collection;

    public UserRecommendationRepository() {
        // TODO: Configurer via properties
        this.mongoClient = MongoClients.create("mongodb://admin:admin123@mongodb:27017");
        this.database = mongoClient.getDatabase("streaming_analytics");
        this.collection = database.getCollection("recommendations");
    }

    /**
     * Sauvegarde une recommandation
     */
    public void save(String userId, VideoRecommendation recommendation) {
        Document doc = new Document()
                .append("userId", userId)
                .append("videoId", recommendation.getVideoId())
                .append("videoTitle", recommendation.getVideoTitle())
                .append("category", recommendation.getCategory())
                .append("duration", recommendation.getDuration())
                .append("relevanceScore", recommendation.getRelevanceScore())
                .append("views", recommendation.getViews())
                .append("likes", recommendation.getLikes())
                .append("reason", recommendation.getReason())
                .append("confidenceScore", recommendation.getConfidenceScore())
                .append("timestamp", System.currentTimeMillis());

        collection.insertOne(doc);
    }

    /**
     * Récupère les recommandations pour un utilisateur
     */
    public List<VideoRecommendation> findByUserId(String userId, int limit) {
        List<VideoRecommendation> recommendations = new ArrayList<>();
        for (Document doc : collection.find(new Document("userId", userId))
                .sort(new Document("relevanceScore", -1))
                .limit(limit)) {
            recommendations.add(documentToRecommendation(doc));
        }
        return recommendations;
    }

    /**
     * Récupère les recommandations pour un utilisateur dans une catégorie spécifique
     */
    public List<VideoRecommendation> findByUserIdAndCategory(String userId, String category, int limit) {
        List<VideoRecommendation> recommendations = new ArrayList<>();
        Document query = new Document()
                .append("userId", userId)
                .append("category", category);
        
        for (Document doc : collection.find(query)
                .sort(new Document("relevanceScore", -1))
                .limit(limit)) {
            recommendations.add(documentToRecommendation(doc));
        }
        return recommendations;
    }

    /**
     * Supprime les anciennes recommandations
     */
    public void deleteOldRecommendations(String userId, long olderThanMs) {
        collection.deleteMany(new Document()
                .append("userId", userId)
                .append("timestamp", new Document("$lt", System.currentTimeMillis() - olderThanMs))
        );
    }

    private VideoRecommendation documentToRecommendation(Document doc) {
        VideoRecommendation rec = new VideoRecommendation();
        rec.setVideoId(doc.getString("videoId"));
        rec.setVideoTitle(doc.getString("videoTitle"));
        rec.setCategory(doc.getString("category"));
        rec.setDuration(doc.getInteger("duration"));
        rec.setRelevanceScore(doc.getDouble("relevanceScore"));
        rec.setViews(doc.getLong("views"));
        rec.setLikes(doc.getInteger("likes"));
        rec.setReason(doc.getString("reason"));
        rec.setConfidenceScore(doc.getDouble("confidenceScore"));
        return rec;
    }
}

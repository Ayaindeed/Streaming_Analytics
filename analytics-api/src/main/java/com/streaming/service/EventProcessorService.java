package com.streaming.service;

import com.streaming.model.ViewEvent;
import com.streaming.model.EventProcessingResult;
import com.streaming.repository.EventRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service de traitement des événements de streaming
 * Gère l'ingestion, le traitement batch et la validation des événements
 */
@ApplicationScoped
public class EventProcessorService {

    private static final Logger logger = LoggerFactory.getLogger(EventProcessorService.class);
    
    @Inject
    private EventRepository eventRepository;
    
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    private final AtomicInteger processedEvents = new AtomicInteger(0);
    private final AtomicInteger failedEvents = new AtomicInteger(0);

    /**
     * Ingestion d'un événement unique
     */
    public EventProcessingResult ingestEvent(ViewEvent event) {
        try {
            // Validation
            validateEvent(event);
            
            // Enrichissement des données
            enrichEvent(event);
            
            // Persistance
            eventRepository.save(event);
            
            processedEvents.incrementAndGet();
            
            logger.info("Event processed successfully: {}", event.getEventId());
            
            return new EventProcessingResult(event.getEventId(), true, "Event processed");
            
        } catch (Exception e) {
            failedEvents.incrementAndGet();
            logger.error("Failed to process event: {}", event.getEventId(), e);
            return new EventProcessingResult(event.getEventId(), false, "Processing failed: " + e.getMessage());
        }
    }

    /**
     * Ingestion batch d'événements avec traitement parallèle
     */
    public EventProcessingResult ingestBatch(List<ViewEvent> events) {
        if (events == null || events.isEmpty()) {
            return new EventProcessingResult(null, false, "Empty event list");
        }

        try {
            logger.info("Starting batch processing of {} events", events.size());
            
            int batchSize = 1000; // Traiter par lots de 1000
            int totalProcessed = 0;
            int totalFailed = 0;

            // Diviser en sous-lots pour optimiser la performance
            for (int i = 0; i < events.size(); i += batchSize) {
                int end = Math.min(i + batchSize, events.size());
                List<ViewEvent> subBatch = events.subList(i, end);
                
                // Valider et enrichir
                for (ViewEvent event : subBatch) {
                    try {
                        validateEvent(event);
                        enrichEvent(event);
                    } catch (Exception e) {
                        logger.warn("Event validation/enrichment failed: {}", event.getEventId(), e);
                        totalFailed++;
                    }
                }
                
                // Sauvegarder le batch
                eventRepository.saveBatch(subBatch);
                totalProcessed += subBatch.size();
                
                logger.info("Processed sub-batch: {}/{}", end, events.size());
            }

            processedEvents.addAndGet(totalProcessed);
            failedEvents.addAndGet(totalFailed);

            logger.info("Batch processing completed: {} processed, {} failed", totalProcessed, totalFailed);
            
            return new EventProcessingResult(
                null,
                true, 
                String.format("Batch processed: %d success, %d failed", totalProcessed, totalFailed)
            );

        } catch (Exception e) {
            logger.error("Batch processing error", e);
            return new EventProcessingResult(null, false, "Batch processing failed: " + e.getMessage());
        }
    }

    /**
     * Ingestion asynchrone pour haute performance
     */
    public CompletableFuture<EventProcessingResult> ingestEventAsync(ViewEvent event) {
        return CompletableFuture.supplyAsync(() -> ingestEvent(event), executorService);
    }

    /**
     * Traitement en streaming (pour données en temps réel)
     */
    public void processStreamingEvent(ViewEvent event) {
        CompletableFuture.runAsync(() -> {
            try {
                validateEvent(event);
                enrichEvent(event);
                eventRepository.save(event);
                processedEvents.incrementAndGet();
            } catch (Exception e) {
                failedEvents.incrementAndGet();
                logger.error("Streaming event processing failed: {}", event.getEventId(), e);
            }
        }, executorService);
    }

    /**
     * Validation des événements
     */
    private void validateEvent(ViewEvent event) {
        if (event.getEventId() == null || event.getEventId().isEmpty()) {
            throw new IllegalArgumentException("Event ID is required");
        }
        
        if (event.getUserId() == null || event.getUserId().isEmpty()) {
            throw new IllegalArgumentException("User ID is required");
        }
        
        if (event.getVideoId() == null || event.getVideoId().isEmpty()) {
            throw new IllegalArgumentException("Video ID is required");
        }
        
        if (event.getTimestamp() == null || event.getTimestamp().isEmpty()) {
            throw new IllegalArgumentException("Timestamp is required");
        }
        
        if (event.getDuration() < 0) {
            throw new IllegalArgumentException("Duration cannot be negative");
        }
    }

    /**
     * Enrichissement des données de l'événement
     */
    private void enrichEvent(ViewEvent event) {
        // Ajouter timestamp si manquant
        if (event.getTimestamp() == null || event.getTimestamp().isEmpty()) {
            event.setTimestamp(String.valueOf(System.currentTimeMillis()));
        }
        
        // Définir device par défaut si manquant
        if (event.getDeviceType() == null || event.getDeviceType().isEmpty()) {
            event.setDeviceType("unknown");
        }
        
        // Définir qualité par défaut si manquant
        if (event.getQuality() == null || event.getQuality().isEmpty()) {
            event.setQuality("auto");
        }
        
        // Définir action par défaut si manquant
        if (event.getAction() == null || event.getAction().isEmpty()) {
            event.setAction("view");
        }
    }

    /**
     * Nettoyer les anciens événements (maintenance)
     */
    public int cleanupOldEvents(long olderThanTimestamp) {
        try {
            // Implementation dépend du repository
            logger.info("Cleanup old events older than {}", olderThanTimestamp);
            // Cette méthode pourrait être ajoutée au repository si nécessaire
            return 0;
        } catch (Exception e) {
            logger.error("Cleanup failed", e);
            return 0;
        }
    }

    /**
     * Obtenir les statistiques de traitement
     */
    public ProcessingStats getProcessingStats() {
        return new ProcessingStats(
            processedEvents.get(),
            failedEvents.get(),
            calculateSuccessRate()
        );
    }

    private double calculateSuccessRate() {
        int total = processedEvents.get() + failedEvents.get();
        if (total == 0) return 100.0;
        return (processedEvents.get() * 100.0) / total;
    }

    /**
     * Classe interne pour les statistiques
     */
    public static class ProcessingStats {
        private final int processedCount;
        private final int failedCount;
        private final double successRate;

        public ProcessingStats(int processedCount, int failedCount, double successRate) {
            this.processedCount = processedCount;
            this.failedCount = failedCount;
            this.successRate = successRate;
        }

        public int getProcessedCount() { return processedCount; }
        public int getFailedCount() { return failedCount; }
        public double getSuccessRate() { return successRate; }
    }

    /**
     * Arrêt gracieux du service
     */
    public void shutdown() {
        executorService.shutdown();
        logger.info("EventProcessorService shutdown initiated");
    }
}

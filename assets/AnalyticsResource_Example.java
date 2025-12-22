package com.streaming.analytics.api;

import com.streaming.analytics.model.ViewEvent;
import com.streaming.analytics.service.EventProcessorService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

/**
 * API REST pour l'ingestion et l'analyse des événements de visualisation
 * 
 * EXEMPLE DE CODE FONCTIONNEL - À ADAPTER ET COMPLÉTER
 * 
 * Ce fichier montre la structure de base de l'API REST.
 * Les étudiants doivent :
 * 1. Implémenter les méthodes manquantes
 * 2. Ajouter la gestion d'erreurs
 * 3. Optimiser les performances
 * 4. Ajouter la validation des données
 */
@Path("/api/v1/analytics")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AnalyticsResource {
    
    @Inject
    private EventProcessorService eventProcessor;
    
    /**
     * ENDPOINT 1 : Ingestion d'un événement unique
     * POST /api/v1/analytics/events
     * 
     * Exemple de requête :
     * {
     *   "eventId": "evt_123",
     *   "userId": "user_456",
     *   "videoId": "video_789",
     *   "timestamp": "2024-01-15T10:30:00Z",
     *   "action": "WATCH",
     *   "duration": 245,
     *   "quality": "1080p",
     *   "deviceType": "mobile"
     * }
     */
    @POST
    @Path("/events")
    public Response ingestEvent(ViewEvent event) {
        try {
            // Validation basique
            if (event == null || event.getEventId() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Invalid event data\"}")
                    .build();
            }
            
            // Traiter l'événement
            eventProcessor.processEvent(event);
            
            // Réponse succès
            return Response.status(Response.Status.CREATED)
                .entity("{\"message\":\"Event processed successfully\",\"eventId\":\"" + event.getEventId() + "\"}")
                .build();
                
        } catch (Exception e) {
            // TODO: Améliorer la gestion d'erreurs
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        }
    }
    
    /**
     * ENDPOINT 2 : Ingestion batch (lot d'événements)
     * POST /api/v1/analytics/events/batch
     * 
     * À COMPLÉTER PAR LES ÉTUDIANTS
     * 
     * Objectifs :
     * - Accepter une liste d'événements
     * - Insérer en batch pour optimiser les performances
     * - Retourner le nombre d'événements traités
     */
    @POST
    @Path("/events/batch")
    public Response ingestBatch(List<ViewEvent> events) {
        // TODO: Implémenter l'ingestion batch
        // Utiliser eventProcessor.processBatch(events)
        
        return Response.status(Response.Status.NOT_IMPLEMENTED)
            .entity("{\"error\":\"À implémenter\"}")
            .build();
    }
    
    /**
     * ENDPOINT 3 : Top vidéos les plus vues
     * GET /api/v1/analytics/videos/top?limit=10
     * 
     * À COMPLÉTER PAR LES ÉTUDIANTS
     */
    @GET
    @Path("/videos/top")
    public Response getTopVideos(@QueryParam("limit") @DefaultValue("10") int limit) {
        // TODO: Récupérer les top vidéos
        // Utiliser eventProcessor.getTopVideos(limit)
        
        return Response.status(Response.Status.NOT_IMPLEMENTED)
            .entity("{\"error\":\"À implémenter\"}")
            .build();
    }
    
    /**
     * ENDPOINT 4 : Statistiques pour une vidéo spécifique
     * GET /api/v1/analytics/videos/{videoId}/stats
     * 
     * À COMPLÉTER PAR LES ÉTUDIANTS
     */
    @GET
    @Path("/videos/{videoId}/stats")
    public Response getVideoStats(@PathParam("videoId") String videoId) {
        // TODO: Récupérer les stats pour cette vidéo
        
        return Response.status(Response.Status.NOT_IMPLEMENTED)
            .entity("{\"error\":\"À implémenter\"}")
            .build();
    }
    
    /**
     * ENDPOINT 5 : Recommandations personnalisées
     * GET /api/v1/analytics/users/{userId}/recommendations?limit=5
     * 
     * À COMPLÉTER PAR LES ÉTUDIANTS
     */
    @GET
    @Path("/users/{userId}/recommendations")
    public Response getRecommendations(
            @PathParam("userId") String userId,
            @QueryParam("limit") @DefaultValue("5") int limit) {
        // TODO: Générer des recommandations
        
        return Response.status(Response.Status.NOT_IMPLEMENTED)
            .entity("{\"error\":\"À implémenter\"}")
            .build();
    }
    
    /**
     * ENDPOINT 6 : Health check
     * GET /api/v1/analytics/health
     */
    @GET
    @Path("/health")
    public Response healthCheck() {
        return Response.ok()
            .entity("{\"status\":\"UP\",\"service\":\"Analytics API\"}")
            .build();
    }
    
    /**
     * BONUS : Streaming temps réel avec Server-Sent Events
     * GET /api/v1/analytics/realtime/stream
     * 
     * AVANCÉ - Optionnel
     */
    // @GET
    // @Path("/realtime/stream")
    // @Produces(MediaType.SERVER_SENT_EVENTS)
    // public void streamRealtime(@Context SseEventSink sseEventSink, @Context Sse sse) {
    //     // TODO: Implémenter le streaming SSE
    //     // Envoyer des stats toutes les 2 secondes
    // }
}


/**
 * NOTES POUR LES ÉTUDIANTS :
 * 
 * 1. VALIDATION
 *    - Valider tous les paramètres d'entrée
 *    - Vérifier les IDs ne sont pas vides
 *    - Valider les limites (ex: limit entre 1 et 100)
 * 
 * 2. GESTION D'ERREURS
 *    - Utiliser try-catch appropriés
 *    - Retourner des codes HTTP corrects (200, 201, 400, 404, 500)
 *    - Messages d'erreur clairs et utiles
 * 
 * 3. PERFORMANCE
 *    - Éviter les requêtes N+1
 *    - Utiliser la pagination pour les résultats volumineux
 *    - Considérer le cache pour les données fréquemment accédées
 * 
 * 4. SÉCURITÉ
 *    - Valider et nettoyer les entrées
 *    - Limiter les tailles de batch
 *    - Rate limiting (bonus)
 * 
 * 5. TESTS
 *    - Tester chaque endpoint avec Postman ou curl
 *    - Vérifier les cas limites
 *    - Mesurer les performances
 */

// Script d'initialisation MongoDB pour Streaming Analytics
// Ce fichier est exécuté automatiquement au démarrage du container

db = db.getSiblingDB('streaming_analytics');

// Créer les collections avec validateurs
print('Creating collections...');

db.createCollection('viewevents', {
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      properties: {
        eventId: { bsonType: 'string' },
        userId: { bsonType: 'string' },
        videoId: { bsonType: 'string' },
        timestamp: { bsonType: 'date' },
        action: { bsonType: 'string' },
        duration: { bsonType: 'int' },
        quality: { bsonType: 'string' },
        deviceType: { bsonType: 'string' }
      }
    }
  }
});

db.createCollection('video_stats');
db.createCollection('user_profiles');
db.createCollection('videos');
db.createCollection('recommendations');

// Create indexes for performance
print('Creating indexes...');

// Events indexes
db.viewevents.createIndex({ 'userId': 1 });
db.viewevents.createIndex({ 'videoId': 1 });
db.viewevents.createIndex({ 'timestamp': -1 });
db.viewevents.createIndex({ 'userId': 1, 'timestamp': -1 });
db.viewevents.createIndex({ 'videoId': 1, 'timestamp': -1 });

// Video stats indexes
db.video_stats.createIndex({ 'videoId': 1 }, { unique: true });
db.video_stats.createIndex({ 'totalViews': -1 });
db.video_stats.createIndex({ 'lastUpdated': -1 });

// User profiles indexes
db.user_profiles.createIndex({ 'userId': 1 }, { unique: true });

// Videos indexes
db.videos.createIndex({ 'videoId': 1 }, { unique: true });
db.videos.createIndex({ 'category': 1 });
db.videos.createIndex({ 'views': -1 });

// Recommendations indexes
db.recommendations.createIndex({ 'userId': 1 });
db.recommendations.createIndex({ 'generatedAt': -1 });

print('✅ MongoDB initialization complete!');

#!/bin/bash
# Script to initialize MongoDB with data from data-generator

echo "Waiting for MongoDB to be ready..."
sleep 5

# Check if MongoDB is ready
mongosh --host mongodb:27017 -u admin -p admin123 --authenticationDatabase admin --eval "db.adminCommand('ping')"

if [ $? -eq 0 ]; then
    echo "MongoDB is ready!"
    
    # Check if data already exists
    COUNT=$(mongosh --host mongodb:27017 -u admin -p admin123 --authenticationDatabase admin streaming_analytics --quiet --eval "db.viewevents.countDocuments()")
    
    if [ "$COUNT" -gt 0 ]; then
        echo "Data already exists in database. Skipping import."
        exit 0
    fi
    
    echo "Generating data..."
    cd /data-generator
    
    # Run data generator if JAR exists
    if [ -f "target/data-generator-1.0-SNAPSHOT.jar" ]; then
        java -jar target/data-generator-1.0-SNAPSHOT.jar
        echo "Data generation complete!"
        
        # Import data into MongoDB
        if [ -f "events_100k.json" ]; then
            echo "Importing events into MongoDB..."
            mongoimport --host mongodb:27017 -u admin -p admin123 --authenticationDatabase admin \
                --db streaming_analytics --collection viewevents --file events_100k.json --jsonArray
        fi
        
        if [ -f "videos_catalog.json" ]; then
            echo "Importing videos into MongoDB..."
            mongoimport --host mongodb:27017 -u admin -p admin123 --authenticationDatabase admin \
                --db streaming_analytics --collection videos --file videos_catalog.json --jsonArray
        fi
        
        echo "Data import complete!"
    else
        echo "Data generator JAR not found. Please build the project first."
    fi
else
    echo "MongoDB is not ready. Exiting..."
    exit 1
fi

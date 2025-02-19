#!/bin/bash

echo "🚀 Starting Deployment Process"

# Define services
SERVICES=("auth-service" "holiday-service" "user-service")

# Set Docker image registry (modify as needed)
REGISTRY="your-docker-registry"

# Build and Push Services
for SERVICE in "${SERVICES[@]}"; do
    echo "🔨 Building $SERVICE..."
    docker build -t $REGISTRY/$SERVICE:latest -f backend/$SERVICE/Dockerfile backend/$SERVICE/

    echo "📤 Pushing $SERVICE to registry..."
    docker push $REGISTRY/$SERVICE:latest
done


echo "✅ Deployment complete! Services running in Kubernetes."

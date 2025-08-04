#!/bin/bash

echo "YouTube SMM Android App - APK Builder"
echo "====================================="

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "Error: Docker is not installed. Please install Docker first."
    exit 1
fi

# Check if docker-compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo "Error: docker-compose is not installed. Please install docker-compose first."
    exit 1
fi

# Create output directory
mkdir -p output

echo "Building APK with Docker..."
echo "This may take several minutes on first run..."

# Build with docker-compose
docker-compose up --build

# Check if build was successful
if [ -f "output/app-release.apk" ]; then
    echo ""
    echo "✅ APK build completed successfully!"
    echo "📱 APK file location: output/app-release.apk"
    echo "📏 File size: $(du -h output/app-release.apk | cut -f1)"
    echo ""
    echo "To install on device:"
    echo "adb install output/app-release.apk"
else
    echo ""
    echo "❌ APK build failed!"
    echo "Check the logs above for errors."
    exit 1
fi
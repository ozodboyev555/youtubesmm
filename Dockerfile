FROM openjdk:17-jdk

# Install required packages
RUN microdnf update && microdnf install -y \
    wget \
    unzip \
    git \
    && microdnf clean all

# Set environment variables
ENV ANDROID_HOME=/opt/android-sdk
ENV ANDROID_SDK_ROOT=/opt/android-sdk
ENV PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools

# Download and install Android SDK
RUN mkdir -p $ANDROID_HOME/cmdline-tools && cd $ANDROID_HOME/cmdline-tools && \
    wget -q https://dl.google.com/android/repository/commandlinetools-linux-8512546_latest.zip && \
    unzip commandlinetools-linux-8512546_latest.zip && \
    mv cmdline-tools latest && \
    rm commandlinetools-linux-8512546_latest.zip

# Set up Android SDK
RUN yes | $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager --licenses
RUN $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"

# Set working directory
WORKDIR /app

# Copy project files
COPY . .

# Grant execute permission to gradlew
RUN chmod +x gradlew

# Build APK
RUN ./gradlew assembleRelease

# Create output directory
RUN mkdir -p /output

# Copy APK to output directory
RUN cp app/build/outputs/apk/release/app-release.apk /output/

# Expose output directory
VOLUME /output

CMD ["cp", "/app/app/build/outputs/apk/release/app-release.apk", "/output/"]
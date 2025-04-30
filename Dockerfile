# Use official JDK image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy Gradle wrapper and project files
COPY . .

# Build the project
RUN ./gradlew build

# Run the application
CMD ["java", "-jar", "build/libs/your-app-name.jar"]

# Multi-stage Docker build for phoss SMP with MongoDB
FROM maven:3.9-eclipse-temurin-11 AS builder

# Set working directory
WORKDIR /app

# Copy the entire project (since dependencies are complex)
COPY . .

# Build only the MongoDB webapp and its dependencies, skipping tests
RUN mvn clean package -DskipTests -pl phoss-smp-webapp-mongodb -am

# Production stage
FROM eclipse-temurin:11-jre-alpine

# Install curl for health checks
RUN apk add --no-cache curl

# Create app directory and user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
WORKDIR /app

# Copy the executable JAR from builder stage
COPY --from=builder /app/phoss-smp-webapp-mongodb/target/phoss-smp-webapp-mongodb.jar ./phoss-smp-webapp-mongodb.jar

# Create config directory
RUN mkdir -p /app/config && chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose port (Heroku will override with $PORT)
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:${PORT:-8080}/ || exit 1

# Set environment variables
ENV JAVA_OPTS="-Xmx512m -Dfile.encoding=UTF-8"
ENV SKIP_POM_CHECK=true

# Run the application
CMD ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=heroku -jar phoss-smp-webapp-mongodb.jar"]

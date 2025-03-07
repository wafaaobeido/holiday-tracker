# ---- Stage 1: Build & Run Tests ----
FROM my-java19-maven AS build

WORKDIR /app

COPY . .

RUN mvn clean verify -P testcontainers

# ---- Stage 2: Build JAR without tests ----
FROM my-java19-maven AS build

# Set the working directory
WORKDIR /app

# Copy only necessary files (No need for `../backend/` since the context is already `backend`)
COPY pom.xml /app/pom.xml
COPY shared-libraries /app/shared-libraries
COPY holiday-service /app/holiday-service

RUN mvn -f /app/pom.xml clean install -N -DskipTests

# Build the shared-libraries first
RUN mvn -f /app/shared-libraries/pom.xml clean install -DskipTests

# Build the holiday-service
RUN mvn -f /app/holiday-service/pom.xml clean package -DskipTests

# ---- Stage 3: Create Final Image ----
FROM openjdk:19-jdk-slim AS runtime

# Set working directory inside the container
WORKDIR /app

# Copy built JAR from the build stage
COPY --from=build /app/holiday-service/target/holiday-service-**.jar holiday-service.jar

# Expose the application port (adjust if necessary)
EXPOSE 8082

# Run the application
CMD ["java", "-jar", "holiday-service.jar"]
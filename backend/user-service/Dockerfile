# ---- Stage 1: Build & Run Tests ----
FROM my-java19-maven AS build

# Set the working directory
WORKDIR /app

COPY . .

RUN mvn clean verify -P testcontainers

# ---- Stage 2: Build JAR tests ----
FROM my-java19-maven AS build

WORKDIR /app

# Copy only necessary files (No need for `../backend/` since the context is already `backend`)
COPY pom.xml /app/pom.xml
COPY shared-libraries /app/shared-libraries
COPY user-service /app/user-service

# Install Parent POM
RUN mvn -f /app/pom.xml clean install -N -DskipTests

# Build the shared-libraries first
RUN mvn -f /app/shared-libraries/pom.xml clean install -DskipTests

# Build the user-service
RUN mvn -f /app/user-service/pom.xml clean package -DskipTests

# ---- Stage 3: Create Final Image ----
FROM openjdk:19-jdk-slim AS runtime

# Set working directory inside the container
WORKDIR /app

# Copy built JAR from the build stage
COPY --from=build /app/user-service/target/user-service-**.jar user-service.jar

# Expose the service port
EXPOSE 8083

# Run the application
CMD ["java", "-jar", "user-service.jar"]
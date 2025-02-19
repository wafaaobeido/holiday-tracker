FROM openjdk:19-jdk-slim

# Install Maven manually
RUN apt update && apt install -y maven

# Set working directory
WORKDIR /app

# Print versions for debugging
RUN java -version && mvn -version

CMD ["bash"]

FROM openjdk:17

# Set the working directory in the image to /app
WORKDIR /app

# Copy the jar file from the build stage
COPY ttrpg-convert-cli-299-SNAPSHOT-runner.jar app.jar
COPY docs /app/docs
COPY examples /app/examples

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

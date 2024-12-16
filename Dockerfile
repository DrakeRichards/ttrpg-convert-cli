FROM openjdk:17

# Set the working directory in the image to /app
WORKDIR /app

# Copy the jar file from the build stage
COPY ttrpg-convert-cli-299-SNAPSHOT-runner.jar app.jar
COPY docs /app/docs
COPY data /app/data
COPY examples /app/examples

# Set the working directory in the image to /app/data
WORKDIR /app

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# Step 1: Use OpenJDK as base image
#FROM  openjdk:21-jdk









FROM eclipse-temurin:21-jdk

# Step 2: Set working directory inside container
WORKDIR /app

# Step 3: Copy jar from target folder into container
COPY  target/ecom-proj.jar  app.jar

# Step 4: Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
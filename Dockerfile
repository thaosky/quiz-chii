FROM openjdk:17-jdk
WORKDIR /app/quizchii-0.0.1-SNAPSHOT.jar /app/quizchii.jar
EXPOSE 8080
CMD ["java", "-jar", "springdemo.jar"]
FROM maven AS build

WORKDIR /usr/app

COPY . .

RUN mvn clean package -DskipTests

FROM openjdk:17-slim

WORKDIR /app

COPY --from=build /usr/app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
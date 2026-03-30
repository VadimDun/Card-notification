FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /build

# Копируем весь проект
COPY . .

# Сборка нужного модуля
RUN mvn clean package -pl demo-module -am -DskipTests


FROM eclipse-temurin:17-jdk

WORKDIR /app

# Копируем jar нужного модуля
COPY --from=build /build/demo-module/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]
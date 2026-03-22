# Используем Java 17
FROM eclipse-temurin:17-jdk

# Рабочая директория
WORKDIR /app

# Копируем jar
COPY demo-module/target/demo-module-*.jar app.jar

# Порт приложения
EXPOSE 8080

# Запуск приложения
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]
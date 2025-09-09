# ---- Build stage ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /src

# Cache intelligent: d'abord pom, puis deps, puis sources
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -q -DskipTests package

# ---- Run stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copie le jar buildé
COPY --from=build /src/target/*.jar app.jar

# Koyeb fournit PORT à l'exécution. On garde 8080 en fallback côté Spring:
# server.port=${PORT:8080} dans application-prod.properties
ENV PORT=8080

# Petites limites mémoire pour l’instance gratuite
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Facultatif: timezone stable (sinon reste en UTC)
# ENV TZ=Africa/Lome

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar","--server.port=${PORT}"]

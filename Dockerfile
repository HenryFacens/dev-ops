### Stage 1: build application
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /workspace

# Copia arquivos de configuração primeiro para aproveitar cache
COPY pom.xml ./
COPY mvnw ./
COPY .mvn .mvn

# Faz download das dependências antecipadamente
RUN mvn -q dependency:go-offline

# Copia resto do projeto e gera o jar
COPY src ./src
COPY docs ./docs
COPY mosquitto ./mosquitto

RUN mvn -q clean package -DskipTests

### Stage 2: runtime image
FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=builder /workspace/target/teste-na-pratica_spring-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

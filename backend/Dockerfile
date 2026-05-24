# Build stage
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

# Copiar arquivos do wrapper e o POM
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x ./mvnw
# Baixar dependências o-offline para usar cache
RUN ./mvnw dependency:go-offline

# Copiar código fonte e empacotar
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copiar o JAR gerado do estágio de build
COPY --from=build /app/target/*.jar app.jar

# Expor a porta da aplicação
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

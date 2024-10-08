FROM maven:3.9-ibm-semeru-21-jammy AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests
# 如果使用国内网络环境打包可以使用下边的命令，正在使用的是Github Action打包时使用的，国外环境无影响
# RUN mvn clean package -s settings.xml -DskipTests

FROM eclipse-temurin:21-jre AS builder
WORKDIR application
ARG JAR_FILE=/app/target/*.jar
COPY --from=build ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM eclipse-temurin:21-jre
WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
EXPOSE 8080
FROM alpine/git:latest AS guildserver_sources

WORKDIR /home
RUN git clone "https://github.com/sebcourtois/cda-guilds-server.git" --depth=1

FROM eclipse-temurin:21-alpine AS guildserver_build

COPY --from=guildserver_sources /home/cda-guilds-server /home/cda-guilds-server
WORKDIR /home/cda-guilds-server

RUN chmod +x gradlew
RUN ./gradlew bootJar

FROM eclipse-temurin:21-alpine
LABEL authors="sebastyx"

COPY --from=guildserver_build /home/cda-guilds-server/build/libs /opt

EXPOSE 5000 50505

CMD ["java", "-jar", "/opt/guildsserver-0.0.1-SNAPSHOT.jar"]














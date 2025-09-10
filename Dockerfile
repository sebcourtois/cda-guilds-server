FROM eclipse-temurin:21-alpine
LABEL authors="sebastyx"

COPY ./build/libs /opt

EXPOSE 5000-5005/udp
EXPOSE 50505/tcp
EXPOSE 50500/tcp

CMD ["java", "-jar", "/opt/guildsserver-0.0.1-SNAPSHOT.jar"]

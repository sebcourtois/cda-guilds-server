FROM eclipse-temurin:21-alpine
LABEL authors="sebastyx"

COPY ./build/libs /opt

EXPOSE 5000/udp
EXPOSE 49394/tcp
EXPOSE 49395/tcp

CMD ["java", "-jar", "/opt/guildsserver-0.0.1-SNAPSHOT.jar"]

FROM openjdk:12
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} Proyectos.jar
ENTRYPOINT ["java","-jar","/Proyectos.jar"]
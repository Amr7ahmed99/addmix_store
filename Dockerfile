FROM maven:3.8.6-openjdk-18-slim AS build
WORKDIR /home/addmix_store

COPY ./pom.xml /home/addmix_store/pom.xml
COPY ./src/main/java/com/web/service/addmix_store/AddmixStoreApplication.java /home/addmix_store/src/main/java/com/web/service/addmix_store/AddmixStoreApplication.java

RUN mvn -f /home/addmix_store/pom.xml clean package

COPY . /home/addmix_store/
RUN mvn -f /home/addmix_store/pom.xml clean package


FROM openjdk:18-slim-buster
COPY --from=build /home/addmix_store/target/*.jar addmix_store-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT [ "sh", "-c", "java -jar /addmix_store-0.0.1-SNAPSHOT.jar" ]
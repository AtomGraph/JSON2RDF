# Built from the sources at the tag being released; the image is the CLI, called as `docker run -i atomgraph/json2rdf
# <base>` with the JSON on standard input and the RDF on standard output.

FROM maven:3.9.11-eclipse-temurin-21 AS build

WORKDIR /usr/src/JSON2RDF

COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src src
RUN mvn -B clean package

FROM eclipse-temurin:21-jre

LABEL maintainer="martynas@atomgraph.com"

COPY --from=build /usr/src/JSON2RDF/target/json2rdf-*-jar-with-dependencies.jar /usr/local/lib/json2rdf.jar

ENTRYPOINT ["java", "-jar", "/usr/local/lib/json2rdf.jar"]

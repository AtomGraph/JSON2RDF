FROM maven:3.8.4-openjdk-17 as maven

LABEL maintainer="martynas@atomgraph.com"

COPY . /usr/src/JSON2RDF

WORKDIR /usr/src/JSON2RDF

RUN mvn clean install

### entrypoint

ENTRYPOINT ["java", "-jar", "target/json2rdf-jar-with-dependencies.jar"]
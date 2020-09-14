FROM maven:3.6.0-jdk-8

LABEL maintainer="martynas@atomgraph.com"

COPY . /usr/src/JSON2RDF

WORKDIR /usr/src/JSON2RDF

RUN mvn clean install

### entrypoint

ENTRYPOINT ["java", "-jar", "target/json2rdf-jar-with-dependencies.jar"]

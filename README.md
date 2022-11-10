# JSON2RDF
Streaming generic JSON to RDF converter

Reads JSON data and streams N-Triples output. The conversion algorithm is similar to that of [JSON-LD](https://www.w3.org/TR/json-ld11-api/) but accepts arbitrary JSON and does not require a `@context`.

The resulting RDF representation is lossless with the exception of array ordering and some [datatype round-tripping](https://www.w3.org/TR/json-ld11-api/#data-round-tripping).
The lost ordering should not be a problem in the majority of cases, as RDF applications tend to impose their own value-based ordering using SPARQL `ORDER BY`.

A common use case is feeding the JSON2RDF output into a triplestore or SPARQL processor and using a SPARQL `CONSTRUCT` query to map the generic RDF to more specific RDF that uses terms from some vocabulary.
SPARQL is an inherently more flexible RDF mapping mechanism than JSON-LD `@context`.

## Build

    mvn clean install

That should produce an executable JAR file `target/json2rdf-jar-with-dependencies.jar` in which dependency libraries will be included.

## Usage

The JSON data is read from `stdin`, the resulting RDF data is written to `stdout`.

JSON2RDF is available as a `.jar` as well as a Docker image [atomgraph/json2rdf](https://hub.docker.com/r/atomgraph/json2rdf) (recommended).

Parameters:
* `base` - the base URI for the data. Property namespace is constructed by adding `#` to the base URI.

Options:
* `--input-charset` - JSON input encoding, by default UTF-8
* `--output-charset` - RDF output encoding, by default UTF-8

## Examples

JSON2RDF output is streaming and produces N-Triples, therefore we pipe it through [`riot`](https://jena.apache.org/documentation/io/) to get a more readable Turtle output.

***

Bob DuCharme's blog post on using JSON2RDF: [Converting JSON to RDF](http://www.bobdc.com/blog/json2rdf/).

***

JSON data in [`ordinary-json-document.json`](https://www.w3.org/TR/json-ld11/#interpreting-json-as-json-ld)
```json
{
  "name": "Markus Lanthaler",
  "homepage": "http://www.markus-lanthaler.com/",
  "image": "http://twitter.com/account/profile_image/markuslanthaler"
}
```

Java execution from shell:

```bash
cat ordinary-json-document.json | java -jar json2rdf-jar-with-dependencies.jar https://localhost/ | riot --formatted=TURTLE
```

Alternatively, Docker execution from shell:
```bash
cat ordinary-json-document.json | docker run -i -a stdin -a stdout -a stderr atomgraph/json2rdf https://localhost/ | riot --formatted=TURTLE
```

Note that using Docker you need to [bind](https://docs.docker.com/engine/reference/commandline/run/#attach-to-stdinstdoutstderr--a) `stdin`/`stdout`/`stderr` streams.

Turtle output

```turtle
[ <https://localhost/#homepage>  "http://www.markus-lanthaler.com/" ;
  <https://localhost/#image>     "http://twitter.com/account/profile_image/markuslanthaler" ;
  <https://localhost/#name>      "Markus Lanthaler"
] .
```

The following SPARQL query can be used to map this generic RDF to the desired target RDF, e.g. a structure that uses [schema.org](https://schema.org) vocabulary.

```sparql
BASE <https://localhost/>
PREFIX : <#>
PREFIX schema: <http://schema.org/>

CONSTRUCT
{
  ?person schema:homepage ?homepage ;
    schema:image ?image ;
    schema:name ?name .
}
{
  ?person :homepage ?homepageStr ;
    :image ?imageStr ;
    :name ?name .
  BIND (URI(?homepageStr) AS ?homepage)
  BIND (URI(?imageStr) AS ?image)
}
```

Turtle output after the mapping

```turtle
[ <http://schema.org/homepage>  <http://www.markus-lanthaler.com/> ;
  <http://schema.org/image>     <http://twitter.com/account/profile_image/markuslanthaler> ;
  <http://schema.org/name>      "Markus Lanthaler"
] .
```

***

JSON data in [`city-distances.json`](https://www.w3.org/TR/xslt-30/#json-to-xml-mapping)

```json
{
  "desc"    : "Distances between several cities, in kilometers.",
  "updated" : "2014-02-04T18:50:45",
  "uptodate": true,
  "author"  : null,
  "cities"  : {
    "Brussels": [
      {"to": "London",    "distance": 322},
      {"to": "Paris",     "distance": 265},
      {"to": "Amsterdam", "distance": 173}
    ],
    "London": [
      {"to": "Brussels",  "distance": 322},
      {"to": "Paris",     "distance": 344},
      {"to": "Amsterdam", "distance": 358}
    ],
    "Paris": [
      {"to": "Brussels",  "distance": 265},
      {"to": "London",    "distance": 344},
      {"to": "Amsterdam", "distance": 431}
    ],
    "Amsterdam": [
      {"to": "Brussels",  "distance": 173},
      {"to": "London",    "distance": 358},
      {"to": "Paris",     "distance": 431}
    ]
  }
}
```

Java execution from shell:
```bash
cat city-distances.json | java -jar json2rdf-jar-with-dependencies.jar https://localhost/ | riot --formatted=TURTLE
```

Alternatively, Docker execution from shell:
```bash
cat city-distances.json | docker run -i -a stdin -a stdout -a stderr atomgraph/json2rdf https://localhost/ | riot --formatted=TURTLE
```

Turtle output

```turtle
[ <https://localhost/#cities>    [ <https://localhost/#Amsterdam>  [ <https://localhost/#distance>  "431"^^<http://www.w3.org/2001/XMLSchema#int> ;
                                                                     <https://localhost/#to>        "Paris"
                                                                   ] ;
                                   <https://localhost/#Amsterdam>  [ <https://localhost/#distance>  "358"^^<http://www.w3.org/2001/XMLSchema#int> ;
                                                                     <https://localhost/#to>        "London"
                                                                   ] ;
                                   <https://localhost/#Amsterdam>  [ <https://localhost/#distance>  "173"^^<http://www.w3.org/2001/XMLSchema#int> ;
                                                                     <https://localhost/#to>        "Brussels"
                                                                   ] ;
                                   <https://localhost/#Brussels>   [ <https://localhost/#distance>  "322"^^<http://www.w3.org/2001/XMLSchema#int> ;
                                                                     <https://localhost/#to>        "London"
                                                                   ] ;
                                   <https://localhost/#Brussels>   [ <https://localhost/#distance>  "265"^^<http://www.w3.org/2001/XMLSchema#int> ;
                                                                     <https://localhost/#to>        "Paris"
                                                                   ] ;
                                   <https://localhost/#Brussels>   [ <https://localhost/#distance>  "173"^^<http://www.w3.org/2001/XMLSchema#int> ;
                                                                     <https://localhost/#to>        "Amsterdam"
                                                                   ] ;
                                   <https://localhost/#London>     [ <https://localhost/#distance>  "358"^^<http://www.w3.org/2001/XMLSchema#int> ;
                                                                     <https://localhost/#to>        "Amsterdam"
                                                                   ] ;
                                   <https://localhost/#London>     [ <https://localhost/#distance>  "322"^^<http://www.w3.org/2001/XMLSchema#int> ;
                                                                     <https://localhost/#to>        "Brussels"
                                                                   ] ;
                                   <https://localhost/#London>     [ <https://localhost/#distance>  "344"^^<http://www.w3.org/2001/XMLSchema#int> ;
                                                                     <https://localhost/#to>        "Paris"
                                                                   ] ;
                                   <https://localhost/#Paris>      [ <https://localhost/#distance>  "431"^^<http://www.w3.org/2001/XMLSchema#int> ;
                                                                     <https://localhost/#to>        "Amsterdam"
                                                                   ] ;
                                   <https://localhost/#Paris>      [ <https://localhost/#distance>  "344"^^<http://www.w3.org/2001/XMLSchema#int> ;
                                                                     <https://localhost/#to>        "London"
                                                                   ] ;
                                   <https://localhost/#Paris>      [ <https://localhost/#distance>  "265"^^<http://www.w3.org/2001/XMLSchema#int> ;
                                                                     <https://localhost/#to>        "Brussels"
                                                                   ]
                                 ] ;
  <https://localhost/#desc>      "Distances between several cities, in kilometers." ;
  <https://localhost/#updated>   "2014-02-04T18:50:45" ;
  <https://localhost/#uptodate>  true
] .
```
### Mapping Twitter export to RDF

You can [download your Twitter data](https://twitter.com/settings/download_your_data) which includes tweets in `tweets.js`. Remove the `window.YTD.tweets.part0 = ` string and save the rest as `tweets.json`.

To get the RDF output, save the following query as `tweets.rq`

```sparql
BASE            <https://twitter.com/>
PREFIX :        <#>
PREFIX xsd:     <http://www.w3.org/2001/XMLSchema#>
PREFIX sioc:    <http://rdfs.org/sioc/ns#>
PREFIX dct:     <http://purl.org/dc/terms/>

CONSTRUCT
{
    ?tweet a sioc:Post ;
        sioc:id ?id ;
        dct:created ?created ;
        sioc:content ?full_text ;
        sioc:reply_of ?reply_of .
}
{
    ?tweet_obj :id ?id ;
        :created_at ?created_at_string ;
        :full_text ?full_text ;
        :lang ?lang .
    OPTIONAL
    {
        ?tweet_obj :in_reply_to_status_id ?in_reply_to_status_id ;
            :in_reply_to_screen_name ?in_reply_to_screen_name .
        BIND(URI(CONCAT(?in_reply_to_screen_name, "/status/", ?in_reply_to_status_id)) AS ?reply_of)
    }

    BIND("atomgraphhq" AS ?username)
    BIND(URI(CONCAT(?username, "/status/", ?id)) AS ?tweet)
    BIND(SUBSTR(?created_at_string, 27, 4) AS ?year_string)
    BIND(SUBSTR(?created_at_string, 5, 3) AS ?month_string)
    BIND(SUBSTR(?created_at_string, 9, 2) AS ?day_string)
    VALUES (?month_string ?month_number_string)
    {
         ("Jan"    "01")
         ("Feb"    "02")
         ("Mar"    "03")
         ("Apr"    "04")
         ("May"    "05")
         ("Jun"    "06")
         ("Jul"    "07")
         ("Aug"    "08")
         ("Sep"    "09")
         ("Oct"    "10")
         ("Nov"    "11")
         ("Dec"    "12")
    }
    BIND(SUBSTR(?created_at_string, 12, 8) AS ?time)
    BIND(SUBSTR(?created_at_string, 21, 3) AS ?tz_hours)
    BIND(SUBSTR(?created_at_string, 24, 2) AS ?tz_minutes)
    BIND(STRDT(CONCAT(?year_string, "-", ?month_number_string, "-", ?day_string, "T", ?time, ?tz_hours, ":", ?tz_minutes), xsd:dateTime) AS ?created)
}
```
adjust your Twitter handle in the query string as `?username`, and then run this command:
```bash
cat twitter.json | docker run -i -a stdin -a stdout -a stderr atomgraph/json2rdf https://twitter.com/ > tweets.nt && \
    sparql --data tweets.nt --query tweets.rq
```
Output sample:
```turtle
<https://twitter.com/atomgraphhq/status/1535239790693699587>
        a              sioc:Post ;
        dct:created    "2022-06-10T12:37:44+00:00"^^xsd:dateTime ;
        sioc:content   "Follow it on GitHub!\nhttps://t.co/pu5KkOoIOX" ;
        sioc:id        "1535239790693699587" ;
        sioc:reply_of  <https://twitter.com/atomgraphhq/status/1535211486582382593> .
```
Improvements to the mapping query are welcome.

## Performance

Largest dataset tested so far: 2.95 GB / 30459482 lines of JSON to 4.5 GB / 21964039 triples in 2m10s.
Hardware: x64 Windows 10 PC with Intel Core i5-7200U 2.5 GHz CPU and 16 GB RAM.

## Dependencies

* [javax.json](https://mvnrepository.com/artifact/org.glassfish/javax.json)
* [Apache Jena](https://jena.apache.org/)
* [picocli](https://picocli.info)
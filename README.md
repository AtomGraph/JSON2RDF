# JSON2RDF
Streaming generic JSON to RDF converter

## Build

    mvn clean install

That should produce an executable JAR file `target/json2rdf-2.0.0-SNAPSHOT-jar-with-dependencies.jar` in which dependency libraries will be included.

## Usage

The JSON data is read from `stdin`, the resulting RDF data is written to `stdout`.

Parameters:
* `base` - the base URI for the data (also becomes the `BASE` URI of the SPARQL query)

Options:
* `--query-file` - a text file with SPARQL 1.1 [`CONSTRUCT`](https://www.w3.org/TR/sparql11-query/#construct) query string
* `--input-charset` - JSON input encoding, by default UTF-8
* `--output-charset` - RDF output encoding, by default UTF-8

## Examples

Input [`ordinary-json-document.json`](https://www.w3.org/TR/json-ld11/#interpreting-json-as-json-ld)
```json
{
  "name": "Markus Lanthaler",
  "homepage": "http://www.markus-lanthaler.com/",
  "image": "http://twitter.com/account/profile_image/markuslanthaler"
}
```

Command

    ordinary-json-document.json | java -jar JSON2RDF-1.0.0-SNAPSHOT-jar-with-dependencies.jar https://localhost/ | riot --formatted=TURTLE

Output

```turtle
[ <https://localhost/#homepage>  "http://www.markus-lanthaler.com/" ;
  <https://localhost/#image>     "http://twitter.com/account/profile_image/markuslanthaler" ;
  <https://localhost/#name>      "Markus Lanthaler"
] .
```

Input [`city-distances.json`](https://www.w3.org/TR/xslt-30/#json-to-xml-mapping)

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

Command

    cat city-distances.json | java -jar JSON2RDF-1.0.0-SNAPSHOT-jar-with-dependencies.jar https://localhost/ | riot --formatted=TURTLE


Output

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

## Performance

### Without transformation

Largest dataset tested so far: 2.95 GB / 30459482 lines of JSON to 4.5 GB / 21964039 triples in 2m10s. Hardware: x64 Windows 10 PC with Intel Core i5-7200U 2.5 GHz CPU and 16 GB RAM.

## Dependencies

* [Apache Jena](https://jena.apache.org/)
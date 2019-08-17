/*
 * Copyright 2019 Martynas Jusevičius <martynas@atomgraph.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.atomgraph.etl.json;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParsingException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.system.StreamRDFLib;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author Martynas Jusevičius <martynas@atomgraph.com>
 */
public class JsonStreamRDFWriterTest
{
    
    private static final String BASE = "http://localhost/";
    private static final String NS = BASE + "#";

    @Test(expected = JsonParsingException.class)
    public void testInvalid()
    {
        String json = "{ ";
        
        Model expected = ModelFactory.createDefaultModel();
        
        assertIsomorphic(json, expected);
    }

    @Test
    public void testEmpty()
    {
        String json = "[ { } ]";
        
        Model expected = ModelFactory.createDefaultModel();
        
        assertIsomorphic(json, expected);
    }

    @Test
    public void testObject()
    {
        String json = "{ \"key\": \"val\" }";
        
        Model expected = ModelFactory.createDefaultModel();
        expected.createResource().
                addLiteral(expected.createProperty(getNS(), "key"), "val");
        
        assertIsomorphic(json, expected);
    }
    
    @Test
    public void testDuplicateKeys()
    {
        String json = "{ \"key\": \"val1\", \"key\": [ \"val2\" ], \"key\": [ \"val3\" ], \"key\": \"val4\" }";
        
        Model expected = ModelFactory.createDefaultModel();
        expected.createResource().
                addLiteral(expected.createProperty(getNS(), "key"), "val1").
                addLiteral(expected.createProperty(getNS(), "key"), "val2").
                addLiteral(expected.createProperty(getNS(), "key"), "val3").
                addLiteral(expected.createProperty(getNS(), "key"), "val4");
        
        assertIsomorphic(json, expected);
    }

    @Test
    public void testNestedObjects()
    {
        String json = "{ \"before\": \"val\", \"obj\": { \"key\": \"val\" }, \"after\": \"val\" }";
        
        Model expected = ModelFactory.createDefaultModel();
        expected.createResource().
                addLiteral(expected.createProperty(getNS(), "before"), "val").
                addProperty(expected.createProperty(getNS(), "obj"), expected.createResource().
                        addLiteral(expected.createProperty(getNS(), "key"), "val")).
                addLiteral(expected.createProperty(getNS(), "after"), "val");
        
        assertIsomorphic(json, expected);
    }

    @Test
    public void testMixedArrays()
    {
        String json = "{ \"array\": [ \"before\", { \"key\": \"val\" }, \"middle\", { \"key1\": \"val1\" }, \"after\" ] }";
        
        Model expected = ModelFactory.createDefaultModel();
        expected.createResource().
                addLiteral(expected.createProperty(getNS(), "array"), "before").
                addProperty(expected.createProperty(getNS(), "array"), expected.createResource().
                        addLiteral(expected.createProperty(getNS(), "key"), "val")).
                addLiteral(expected.createProperty(getNS(), "array"), "middle").
                addProperty(expected.createProperty(getNS(), "array"), expected.createResource().
                        addLiteral(expected.createProperty(getNS(), "key1"), "val1")).
                addLiteral(expected.createProperty(getNS(), "array"), "after");
        
        assertIsomorphic(json, expected);
    }

    @Test
    public void testNestedArrays()
    {
        String json = "{ \"array\": [ \"before\", [ \"val\", { \"key\": \"val\" } ], \"after\" ] }";
        
        Model expected = ModelFactory.createDefaultModel();
        expected.createResource().
                addLiteral(expected.createProperty(getNS(), "array"), "before").
                addLiteral(expected.createProperty(getNS(), "array"), "val").
                addProperty(expected.createProperty(getNS(), "array"), expected.createResource().
                        addLiteral(expected.createProperty(getNS(), "key"), "val")).
                addLiteral(expected.createProperty(getNS(), "array"), "after");
        
        assertIsomorphic(json, expected);
    }
    
    @Test
    public void testLiteralValues()
    {
        String json = "{ \"bool_true\": true, \"bool_false\": false, \"int\": 42, \"float\": 66.6 }";
        
        Model expected = ModelFactory.createDefaultModel();
        expected.createResource().
                addLiteral(expected.createProperty(getNS(), "bool_true"), Boolean.TRUE).
                addLiteral(expected.createProperty(getNS(), "bool_false"), Boolean.FALSE).
                addLiteral(expected.createProperty(getNS(), "int"), Integer.valueOf("42")).
                addLiteral(expected.createProperty(getNS(), "float"), Float.valueOf("66.6"));
        
        assertIsomorphic(json, expected);
    }

    @Test
    public void testNullValue()
    {
        String json = "{ \"key\": null }";
        
        Model expected = ModelFactory.createDefaultModel();
        
        assertIsomorphic(json, expected);
    }

    public void assertIsomorphic(String json, Model expected)
    {
        assertIsomorphic(getJsonParser(json), getNS(), expected);
    }

    public static void assertIsomorphic(JsonParser jsonParser, String base, Model expected)
    {
        Model parsed = ModelFactory.createDefaultModel();
        new JsonStreamRDFWriter(jsonParser, StreamRDFLib.graph(parsed.getGraph()), base).convert();
        
        assertIsomorphic(expected, parsed);
    }
    
    public static void assertIsomorphic(Model wanted, Model got)
    {
        if (!wanted.isIsomorphicWith(got))
            fail("Models not isomorphic (not structurally equal))");
    }
    
    public static JsonParser getJsonParser(String json)
    {
        return Json.createParser(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));
    }
    
    public String getNS()
    {
        return NS;
    }
    
}

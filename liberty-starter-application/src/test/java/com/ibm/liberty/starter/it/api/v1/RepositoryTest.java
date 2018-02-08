/*******************************************************************************
 * Copyright (c) 2016,2017 IBM Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.ibm.liberty.starter.it.api.v1;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.ibm.liberty.starter.it.api.v1.utils.RepositoryClient;

public class RepositoryTest {
    
    @Test
    public void testTestProvidedPom() throws Exception {
        String file = RepositoryClient.invoke("net/wasdev/wlp/starters/test/provided-pom/0.0.1/provided-pom-0.0.1.pom", 200);
        assertTrue("Expecting pom file with groupId net.wasdev.wlp.starters.test, contents of file was " + file, file.contains("<groupId>net.wasdev.wlp.starters.test</groupId>"));
        assertTrue("Expecting pom file with artifactId provided-pom, contents of file was " + file, file.contains("<artifactId>provided-pom</artifactId>"));
        assertTrue("Expecting pom file with version 0.0.1, contents of file was " + file, file.contains("<version>0.0.1</version>"));
    }
    
    @Test
    public void testTestMicroserviceRuntimePom() throws Exception {
        String file = RepositoryClient.invoke("net/wasdev/wlp/starters/test/runtime-pom/0.0.1/runtime-pom-0.0.1.pom", 200);
        assertTrue("Expecting pom file with groupId net.wasdev.wlp.starters.test, contents of file was " + file, file.contains("<groupId>net.wasdev.wlp.starters.test</groupId>"));
        assertTrue("Expecting pom file with artifactId provided-pom, contents of file was " + file, file.contains("<artifactId>runtime-pom</artifactId>"));
        assertTrue("Expecting pom file with version 0.0.1, contents of file was " + file, file.contains("<version>0.0.1</version>"));
    }
    
    @Test
    public void testTestMicroserviceCompilePom() throws Exception {
        String file = RepositoryClient.invoke("net/wasdev/wlp/starters/test/compile-pom/0.0.1/compile-pom-0.0.1.pom", 200);
        assertTrue("Expecting pom file with groupId net.wasdev.wlp.starters.test, contents of file was " + file, file.contains("<groupId>net.wasdev.wlp.starters.test</groupId>"));
        assertTrue("Expecting pom file with artifactId provided-pom, contents of file was " + file, file.contains("<artifactId>compile-pom</artifactId>"));
        assertTrue("Expecting pom file with version 0.0.1, contents of file was " + file, file.contains("<version>0.0.1</version>"));
    }
    
    @Test
    public void testTestMicroserviceServerSnippet() throws Exception {
        String file = RepositoryClient.invoke("net/wasdev/wlp/starters/test/server-snippet/0.0.1/server-snippet-0.0.1.xml", 200);
        assertTrue("Server snippet containing <!-- Sample test server snippet -->, contents of file was " + file, file.contains("<!-- Sample test server snippet -->"));
    }
    
    @Test
    public void testInvalidFile() throws Exception {
        String url = "net/wasdev/wlp/starters/test/wibble/0.0.1/wibble-0.0.1.pom";
        String output = RepositoryClient.invoke(url, 404);
        assertTrue("Invalid file name, expected 404. Output was " + output, output.contains("File not found: " + url));
    }

    @Test
    public void testInvalidTechType() throws Exception {
        String url = "net/wasdev/wlp/starters/wibble/provided-pom/0.0.1/provided-pom-0.0.1.pom";
        String output = RepositoryClient.invoke(url, 404);
        assertTrue("Status 404 returned correctly but error message was incorrect. Expected 'Tech type wibble not found', was " + output, output.contains("Tech type wibble not found"));
    }



}

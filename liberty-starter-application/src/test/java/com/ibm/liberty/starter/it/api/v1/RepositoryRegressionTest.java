/*******************************************************************************
 * Copyright (c) 2017 IBM Corp.
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

public class RepositoryRegressionTest {
    
    private final String PROVIDED = "provided-pom";
    private final String RUNTIME = "runtime-pom";
    private final String COMPILE = "compile-pom";
    
    @Test
    public void testMicroprofile() throws Exception {
        String id = "microprofile";
        String[] versions = {"0.0.1-SNAPSHOT", "0.0.2", "0.0.3"};
        for (String version : versions) {
          assertPom(id, version, PROVIDED);
          assertPom(id, version, RUNTIME);
          assertServerXml(id, version);
        }
        assertMetadata(id, PROVIDED);
    }
    
    @Test
    public void testMsBuilder() throws Exception {
        String id = "ms-builder";
        String version = "0.1";
        assertPom(id, version, PROVIDED);
        assertPom(id, version, RUNTIME);
        assertMetadata(id, PROVIDED);
    }
    
    @Test
    public void testPersistence() throws Exception {
        String id = "persistence";
        String[] versions = {"0.0.1", "0.0.2", "0.0.3"};
        for (String version : versions) {
            assertPom(id, version, PROVIDED);
            assertPom(id, version, RUNTIME);
            assertServerXml(id, version);
        }
        assertMetadata(id, PROVIDED);
    }
    
    @Test
    public void testRest() throws Exception {
        String id = "rest";
        String[] versions = {"0.0.1", "0.0.2", "0.0.3"};
        for (String version : versions) {
            assertPom(id, version, PROVIDED);
            assertPom(id, version, RUNTIME);
            assertServerXml(id, version);
        }
        assertMetadata(id, PROVIDED);
    }
    
    @Test
    public void testSpringbootWeb() throws Exception {
        String id = "springbootweb";
        String[] versions = {"0.0.2", "0.0.3"};
        for (String version : versions) {
            assertPom(id, version, PROVIDED);
            assertPom(id, version, RUNTIME);
            assertPom(id, version, COMPILE);
        }
        assertMetadata(id, PROVIDED);
    }
    
    @Test
    public void testSwagger() throws Exception {
        String id = "swagger";
        String[] versions = {"0.0.1", "0.0.2", "0.0.3", "0.0.4"};
        for (String version : versions) {
            assertPom(id, version, PROVIDED);
            assertPom(id, version, RUNTIME);
            assertServerXml(id, version);
        }
        assertMetadata(id, PROVIDED);
    }
    
    @Test
    public void testWatsonSDK() throws Exception {
        String id = "watsonsdk";
        String[] versions = {"0.0.1", "0.0.2", "0.0.3", "0.0.4", "0.0.5", "0.0.6"};
        for (String version : versions) {
            assertPom(id, version, COMPILE);
        }
        assertMetadata(id, COMPILE);
    }
    
    @Test
    public void testServlet() throws Exception {
        String id = "web";
        String[] versions = {"0.0.1", "0.0.2", "0.0.3"};
        for (String version : versions) {
            assertPom(id, version, PROVIDED);
            assertPom(id, version, RUNTIME);
            assertServerXml(id, version);
        }
        assertMetadata(id, PROVIDED);
    }
    
    @Test
    public void testWebsocket() throws Exception {
        String id = "websocket";
        String[] versions = {"0.0.2", "0.0.3", "0.0.4"};
        for (String version : versions) {
            assertPom(id, version, PROVIDED);
            assertPom(id, version, RUNTIME);
            assertServerXml(id, version);
        }
        assertMetadata(id, PROVIDED);
    }
    
    private void assertPom(String id, String version, String type) throws Exception {
        String pom = RepositoryClient.invoke("net/wasdev/wlp/starters/" + id + "/" + type + "/" + version + "/" + type + "-" + version + ".pom", 200);
        assertTrue("Expecting pom file with groupId net.wasdev.wlp.starters." + id + ", contents of file was " + pom, pom.contains("<groupId>net.wasdev.wlp.starters." + id + "</groupId>"));
        assertTrue("Expecting pom file with artifactId " + type + ", contents of file was " + pom, pom.contains("<artifactId>" + type + "</artifactId>"));
        assertTrue("Expecting pom file with version " + version + ", contents of file was " + pom, pom.contains("<version>" + version + "</version>"));
    }
    

    private void assertServerXml(String id, String version) throws Exception {
        String file = RepositoryClient.invoke("net/wasdev/wlp/starters/" + id + "/server-snippet/" + version + "/server-snippet-" + version + ".xml", 200);
        assertTrue("Server snippet containing <featureManager>, contents of file was " + file, file.contains("<featureManager>"));
    }
    
    private void assertMetadata(String id, String type) throws Exception {
        String file = RepositoryClient.invoke("net/wasdev/wlp/starters/" + id +"/" + type + "/maven-metadata-local.xml", 200);
        assertTrue("Expecting xml file with tag <metadata>, contents of file was " + file, file.contains("<metadata>"));
    }

}

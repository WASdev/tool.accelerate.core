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

package com.ibm.liberty.starter.it.api.v1.utils;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.junit.Assert;

public class RepositoryAPIJarClient {

    public static final String CONFIG = "config";
    public static final String FAULT_TOLERANCE = "faulttolerance";

    public static void assertJar(String id, String version, String type) throws Exception {
        String url = "http://localhost:" + System.getProperty("liberty.test.port") + "/start/api/v1/repo/net/wasdev/wlp/starters/" + id + "/" + type + "/" + version + "/" + type + "-" + version + ".jar";
        Client client = ClientBuilder.newClient();
        Response response = client.target(url).request("application/zip").get();
        try {
            int responseStatus = response.getStatus();
            if (responseStatus != 200) {
                Assert.fail("Expected response status 200, instead found " + responseStatus + ". Response was " + response.readEntity(String.class));
            }
            switch (type) {
                case CONFIG:
                    assertTrue("Config class not found", findConfig(response));
                    break;
                case FAULT_TOLERANCE:
                    assertTrue("Fault tolerance class not found", findFaultTolerance(response));
            }
        } finally {
            response.close();
        }
    }

    private static boolean findConfig(Response resp) throws Exception {
       return findFile(resp, "Config.class", "config.jar");
    }
    
    private static boolean findFaultTolerance(Response resp) throws Exception {
        return findFile(resp, "CircuitBreaker.class", "config.jar");
     }
    
    private static boolean findFile(Response response, String klass, String jar) throws Exception {
        // Read the response into an InputStream
        InputStream entityInputStream = response.readEntity(InputStream.class);
        // Create a new ZipInputStream from the response InputStream
        ZipInputStream zipIn = new ZipInputStream(entityInputStream);
        // This system property is being set in the liberty-starter-application/build.gradle file
        String tempDir = System.getProperty("liberty.temp.dir");
        File file = new File(tempDir + "/" + jar);
        System.out.println("Creating zip file: " + file.toString());
        file.getParentFile().mkdirs();
        ZipEntry inputEntry = null;
        boolean found = false;
        while ((inputEntry = zipIn.getNextEntry()) != null) {
            String entryName = inputEntry.getName();
            if (entryName.contains(klass)) {
                System.out.println("Found " + klass + " file.");
                found = true;
            }
            zipIn.closeEntry();
        }
        zipIn.close();
        return found;
    }
    
}

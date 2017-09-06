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

import static com.ibm.liberty.starter.it.api.v1.utils.UploadEndpointUtils.getBasicSwagger;
import static com.ibm.liberty.starter.it.api.v1.utils.UploadEndpointUtils.invokeUploadEndpoint;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.io.InputStream;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.junit.Test;

public class WorkspaceEndpointTest {
    
    @Test
    public void testStarterWorkspaceEndpoint() throws Exception {
        String endpoint = "/start/api/v1/workspace";
        Client client = ClientBuilder.newClient();
        String port = System.getProperty("liberty.test.port");
        String url = "http://localhost:" + port + endpoint;
        System.out.println("Testing " + url);
        Response response = client.target(url).request().get();
        try {
            int status = response.getStatus();
            assertEquals("Response incorrect, response status was " + status, 200, status);
            String workspaceId = response.readEntity(String.class);
            assertNotNull("Returned workspace ID was not a valid UUID : " + workspaceId, UUID.fromString(workspaceId));
        } finally {
            response.close();
        }
    }
    
    @Test
    public void testWorkspaceFileDownloadEndpoint() throws Exception {
        String uuid = UUID.randomUUID().toString();
        int responseCode = invokeUploadEndpoint("tech=swagger&process=true&cleanup=true&workspace=" + uuid, "sampleUpload.json", getBasicSwagger("CollectiveInfo"));
        assertEquals("Response from upload endpoint was incorrect, response was " + responseCode, Response.Status.OK.getStatusCode(), responseCode);
        
        String endpoint = "/start/api/v1/workspace/files?workspace=" + uuid + "&serviceId=swagger&dir=server";
        Client client = ClientBuilder.newClient();
        String port = System.getProperty("liberty.test.port");
        String url = "http://localhost:" + port + endpoint;
        System.out.println("Testing " + url);
        Response response = client.target(url).request().get();
        int responseStatus = response.getStatus();
        String output = "";
        if (responseStatus != 200) {
            output = response.readEntity(String.class);
            assumeTrue(!output.contains("Missing project generation configuration: generation URL, starter URL"));
        }
        assertTrue("Expected response status 200, instead found " + responseStatus + " and output " + output, responseStatus == 200);
        try {
            InputStream entityInputStream = response.readEntity(InputStream.class);
            ZipInputStream zipIn = new ZipInputStream(entityInputStream);
            ZipEntry inputEntry = null;
            boolean modelFileExists = false;
            boolean apiFileExists = false;
            boolean ignorefileExists = false;
            boolean licenseFileExists = false;
            boolean pomFileExists = false;
            boolean swaggerFileExists = false;
            String zipEntries = "";
            while ((inputEntry = zipIn.getNextEntry()) != null) {
                String entryName = inputEntry.getName();
                zipEntries += entryName + ",";
                switch (entryName) {
                    case "swagger/server/src/main/java/io/swagger/model/CollectiveInfo.java":
                        modelFileExists = true;
                        break;
                    case "swagger/server/src/main/java/io/swagger/api/IbmApi.java":
                        apiFileExists = true;
                        break;
                    case "swagger/server/.swagger-codegen-ignore":
                        ignorefileExists = true;
                        break;
                    case "swagger/server/LICENSE":
                        licenseFileExists = true;
                        break;
                    case "swagger/server/pom.xml":
                        pomFileExists = true;
                        break;
                    case "swagger/server/swagger.json":
                        swaggerFileExists = true;
                        break;
                    default:
                        break;
                }
            }
            zipIn.close();
            assertExists("swagger/server/src/main/java/io/swagger/model/CollectiveInfo.java", modelFileExists, zipEntries);
            assertExists("swagger/server/src/main/java/io/swagger/api/IbmApi.java", apiFileExists, zipEntries);
            assertExists("swagger/server/.swagger-codegen-ignore", ignorefileExists, zipEntries);
            assertExists("swagger/server/LICENSE", licenseFileExists, zipEntries);
            assertExists("swagger/server/pom.xml", pomFileExists, zipEntries);
            assertExists("swagger/server/swagger.json", swaggerFileExists, zipEntries);
            String[] entries = zipEntries.split(",");
            assertEquals("Expected 6 zip entries. Entries found:" + zipEntries, 6, entries.length);
        } finally {
            response.close();
        }
    }
    
    private void assertExists(String fileName, boolean exists, String zipEntries) {
        assertTrue("Expected " + fileName + " in the zip file. Entries found:" + zipEntries, exists);
    }

}

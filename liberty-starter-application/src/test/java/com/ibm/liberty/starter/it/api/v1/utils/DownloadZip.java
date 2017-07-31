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

public class DownloadZip {
    public static Response get(String queryString) throws Exception {
        Client client = ClientBuilder.newClient();
        String port = System.getProperty("liberty.test.port");
        String url = "http://localhost:" + port + "/start/api/v1/data?" + queryString;
        System.out.println("Testing " + url);
        Response response = client.target(url).request("application/zip").get();
        return response;
    }
    
    public static void assertBasicContent(Response resp, String buildFile, boolean bluemix) throws Exception{
        boolean foundBuildFile = false;
        boolean foundReadme = false;
        boolean foundSrc = false;
        boolean foundTests = false;
        boolean foundBluemixFiles = false;
        // Read the response into an InputStream
        InputStream entityInputStream = resp.readEntity(InputStream.class);
        // Create a new ZipInputStream from the response InputStream
        ZipInputStream zipIn = new ZipInputStream(entityInputStream);
        // This system property is being set in the liberty-starter-application/build.gradle file
        String tempDir = System.getProperty("liberty.temp.dir");
        File file = new File(tempDir + "/TestApp.zip");
        System.out.println("Creating zip file: " + file.toString());
        file.getParentFile().mkdirs();
        ZipEntry inputEntry = null;
        String zipEntries = "";
        while ((inputEntry = zipIn.getNextEntry()) != null) {
            String entryName = inputEntry.getName();
            zipEntries += entryName + ",";
            if (buildFile.equals(entryName)) {
                foundBuildFile = true;
            }
            if ("README.md".equals(entryName)) {
                foundReadme = true;
            }
            if (entryName.startsWith("src/main/java")) {
                foundSrc = true;
            }
            if (entryName.startsWith("src/test/java")) {
                foundTests = true;
            }
            if (entryName.equals("manifest.yml")) {
                foundBluemixFiles = true;
            }
            zipIn.closeEntry();
        }
        zipIn.close();
        assertTrue("Didn't find build file " + buildFile + ". Zip entries were: " + zipEntries, foundBuildFile);
        assertTrue("Didn't find README.md file. Zip entries were: " + zipEntries, foundReadme);
        assertTrue("Didn't find any Java source files i.e. files starting src/main/java. Zip entries were: " + zipEntries, foundSrc);
        assertTrue("Didn't find any Java test files i.e. files starting src/test/java. Zip entries were: " + zipEntries, foundTests);
        assertTrue("Expected foundBluemixFiles to be " + bluemix + ". Zip entries were: " + zipEntries, bluemix == foundBluemixFiles);
    }
}

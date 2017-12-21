/*******************************************************************************
 * Copyright (c) 2017 IBM Corp.
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
 *******************************************************************************/
package com.ibm.liberty.starter.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.ibm.liberty.starter.ProjectConstructionInputData;
import com.ibm.liberty.starter.exception.ProjectGenerationException;

public class BxCodegenClient {
    
    private static final Logger log = Logger.getLogger(BxCodegenClient.class.getName());
    
    public final String URL = System.getenv("bxCodegenEndpoint");
    public final String STARTERKIT_URL = System.getenv("appAccelStarterkit");
    private final int retriesAllowed = 18;
    
    public Map<String, byte[]> getFileMap(ProjectConstructionInputData inputData) throws ProjectGenerationException {
        checkConfig();
        if (inputData.generationId != null) {
            return getExistingFileMap(inputData.generationId);
        } else {
            return generateAndGetFileMap(inputData);
        }
    }
    
    private Map<String, byte[]> generateAndGetFileMap(ProjectConstructionInputData inputData) {
        String payload = getPayload(inputData);
        String id = callBxCodegen(payload);
        waitForFinishedStatus(id);
        Map<String, byte[]> projectMap = getProjectMap(id);
        return projectMap;
    }
    
    private Map<String, byte[]> getExistingFileMap(String id) {
        Map<String, byte[]> projectMap = getProjectMap(id);
        return projectMap;
    }
    
    public String generateProject(ProjectConstructionInputData inputData) throws ProjectGenerationException {
        checkConfig();
        String payload = getPayload(inputData);
        String id = callBxCodegen(payload);
        waitForFinishedStatus(id);
        return id;
    }
    
    private void checkConfig() throws ProjectGenerationException {
        String missingConfig = "";
        if(URL == null) {
            missingConfig += "generation URL, ";
        }
        if (STARTERKIT_URL == null) {
            missingConfig += "starter URL, ";
        }
        if (!missingConfig.isEmpty()) {
            String missing = missingConfig.substring(0, missingConfig.length() - 1);
            throw new ProjectGenerationException("Missing project generation configuration: " + missing);
        }
    }
    
    private String getPayload(ProjectConstructionInputData inputData) {
        String payload = "{\"project\":{"
                + "\"backendPlatform\":\"JAVA\","
                + "\"name\":\"" + inputData.appName + "\"},"
                + "\"generatorOptions\":{"
                + "\"generator-ibm-java\":{"
                + "\"options\":" + inputData.toBxJSON()
                + "}},"
                + "\"templateSources\":[\"" + STARTERKIT_URL + "\"]}";
        return payload;
    }
    
    protected String callBxCodegen(String payload) {
        System.out.println("Sending codegen request with payload: " + payload);
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(URL + "api/generator");
        Invocation.Builder invoBuild = target.request();
        Response response = invoBuild.accept(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(payload));
        String responseString = response.readEntity(String.class);
        responseString.replaceAll(" ", "");
        InputStream is = new ByteArrayInputStream(responseString.getBytes());
        JsonObject object = Json.createReader(is).readObject();
        JsonObject job = (JsonObject) object.get("job");
        String id = job.getString("id");
        System.out.println("Received job id: " + id);
        return id;
    }
    
    private void waitForFinishedStatus(String id) throws ProjectGenerationException {
        int retries = 0;
        while (("RUNNING").equals(checkStatus(id)) && retries <= retriesAllowed) {
            System.out.println("Retry number " + retries);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                log.severe(e.getClass().getName() + " caught " + e.getMessage());
                throw new ProjectGenerationException("Code generation failed for job with id: " + id + ". Try again later.");
            }
            retries++;
        }
        String status = checkStatus(id);
        if(!("FINISHED").equals(status)) {
            if ("FAILED".equals(status)) {
                throw new ProjectGenerationException("Code generation failed for job with id: " + id + ". Try again later.");
            }
            if ("RUNNING".equals(status)) {
                throw new ProjectGenerationException("Code generation for job with id " + id + " timed out. Try again later");
            }
            throw new ProjectGenerationException("Did not receive FINISHED from Bx codegen for job with id: " + id + ". Status received:" + status);
        }
    }
    
    protected String checkStatus(String id) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(URL + "api/generator/" + id + "/status");
        Invocation.Builder invoBuild = target.request();
        Response response = invoBuild.accept(MediaType.APPLICATION_JSON_TYPE).get();
        JsonObject responseObject = response.readEntity(JsonObject.class);
        String responseStatus = responseObject.getString("status");
        System.out.println("Received response status : " + responseStatus);
        return responseStatus;
    }
    
    protected Map<String, byte[]> getProjectMap(String id) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(URL + "api/generator/" + id);
        Invocation.Builder invoBuild = target.request();
        Response response = invoBuild.accept("application/zip").get();
        InputStream is= response.readEntity(InputStream.class);
        ZipInputStream zis = new ZipInputStream(is);
        Map<String, byte[]> map = new HashMap<String, byte[]>();
        ZipEntry ze;
        try {
            while ((ze = zis.getNextEntry()) != null) {
                String path = ze.getName();
                int length = 0;
                byte[] bytes = new byte[1024];
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                while ((length = zis.read(bytes)) != -1 ) {
                    baos.write(bytes, 0, length);
                }
                map.put(path, baos.toByteArray());
            }
            zis.close();
        } catch (IOException e) {
            log.severe("Caught IOException while reading project zip to Map<String, byte[]> : " + e.getMessage());
            throw new ProjectGenerationException("Code generation failed for job with id: " + id + ". Try again later.");
        }
        return map;
    }

}

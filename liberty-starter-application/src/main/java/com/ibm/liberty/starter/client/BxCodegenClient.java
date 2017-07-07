package com.ibm.liberty.starter.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
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

public class BxCodegenClient {
    
    public static final String URL = "";//TODO: add bxcodegen url
    
    public Map<String, byte[]> getFileMap(ProjectConstructionInputData inputData) {
        String payload = getPayload(inputData);
        String id = callBxCodegen(payload);
        waitForFinishedStatus(id);
        Map<String, byte[]> projectMap = getProjectMap(id);
        return projectMap;
    }
    
    private String getPayload(ProjectConstructionInputData inputData) {
        String payload = "{\"project\":{"
                + "\"backendPlatform\":\"JAVA\","
                + "\"name\":\"" + inputData.appName + "\"},"
                + "\"generatorOptions\":{"
                + "\"generator-java\":{"
                + "\"options\":" + inputData.toBxJSON()
                + "}},"
                + "\"templateSources\":[\"\"]}";//TODO: add template url
        return payload;
    }
    
    private String callBxCodegen(String payload) {
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
    
    private void waitForFinishedStatus(String id) {
        int retries = 0;
        while (("RUNNING").equals(checkStatus(id)) && retries < 7) {
            System.out.println("Retry number " + retries);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            retries++;
        }
    }
    
    private String checkStatus(String id) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(URL + "api/generator/" + id + "/status");
        Invocation.Builder invoBuild = target.request();
        Response response = invoBuild.accept(MediaType.APPLICATION_JSON_TYPE).get();
        JsonObject responseObject = response.readEntity(JsonObject.class);
        String responseStatus = responseObject.getString("status");
        System.out.println("Received response status : " + responseStatus);
        return responseStatus;
    }
    
    private Map<String, byte[]> getProjectMap(String id) {
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return map;
    }

}

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

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadEndpointUtils {
    
    public static int invokeUploadEndpoint(String params, String fileName, String fileContent) throws Exception {
        String port = System.getProperty("liberty.test.port");
        String path = "http://localhost:" + port + "/start/api/v1/upload" + ((params != null && !params.trim().isEmpty()) ? ("?" + params) : "");
        System.out.println("Testing " + path);
        
        String boundary = "----WebKitFormBoundarybcoFJqLu81T8NPk8";
        URL url = new URL(path);
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
        httpConnection.setUseCaches(false);
        httpConnection.setDoInput(true);
        httpConnection.setDoOutput(true);        
        
        httpConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        OutputStream outputStream = httpConnection.getOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream), true);
        
        final String NEW_LINE = "\r\n";
        writer.append("--" + boundary).append(NEW_LINE);
        writer.append("Content-Disposition: form-data; name=\"fileFormData\"; filename=\"" + fileName + "\"").append(NEW_LINE);
        writer.append("Content-Type: application/octet-stream").append(NEW_LINE).append(NEW_LINE);
        writer.append(fileContent).append(NEW_LINE);
        writer.append(NEW_LINE).flush();
        writer.append("--" + boundary + "--").append(NEW_LINE);
        writer.close();
        
        httpConnection.disconnect();
        return httpConnection.getResponseCode();
    }
    
    public static String getBasicSwagger(String modelName) {
        String swaggerContent = "{\"swagger\": \"2.0\",\"info\": {\"description\": \"Info APIs for Collective\",\"version\": \"1.0.0\"},\"basePath\": \"/\","
                + "\"paths\": {\"/ibm/api/root1/v1/info\": {\"get\": {\"summary\": \"Retrieve collective's core information\","
                + "\"description\": \"Returns a JSON with core information about collective\",\"operationId\": \"getInfo\",\"produces\": "
                + "[\"application/json\"],\"responses\": {\"200\": {\"description\": \"successful operation\","
                + "\"schema\": {\"$ref\": \"#/definitions/" + modelName + "\"}},\"404\": {\"description\": \"Invalid path\"}}}}},\"definitions\": {"
                + "\"" + modelName + "\": {\"properties\": {\"name\": {\"type\": \"string\",\"description\": \"Name of the collective\"}}}}}";
        return swaggerContent;
    }

}

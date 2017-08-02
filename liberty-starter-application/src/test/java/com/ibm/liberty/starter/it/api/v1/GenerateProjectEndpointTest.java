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
import static org.junit.Assume.assumeTrue;

import java.io.InputStream;
import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;

import com.ibm.liberty.starter.it.api.v1.utils.DownloadZip;

public class GenerateProjectEndpointTest {
    
    @Test
    public void generateMavenProjectTest() throws Exception {
        Response generateResponse = callGenerateEndpoint();
        int generateResponseStatus = generateResponse.getStatus();
        String generateOutput = "";
        if (generateResponseStatus != 200) {
            generateOutput = generateResponse.readEntity(String.class);
            assumeTrue(!generateOutput.contains("Missing project generation configuration: generation URL, starter URL"));
        }
        assertTrue("Expected response status 200, instead found " + generateResponseStatus + " and output " + generateOutput, generateResponseStatus == 200);
        String responseString = generateResponse.readEntity(String.class);
        JsonReader jsonReader = Json.createReader(new StringReader(responseString));
        String queryString = jsonReader.readObject().getString("requestQueryString");
        Response response = DownloadZip.get(queryString);
        try {
            int responseStatus = response.getStatus();
            String output = "";
            if (responseStatus != 200) {
                output = response.readEntity(String.class);
                assumeTrue(!output.contains("Missing project generation configuration: generation URL, starter URL"));
            }
            assertTrue("Expected response status 200, instead found " + responseStatus + " and output " + output, responseStatus == 200);
            DownloadZip.assertBasicContent(response, "pom.xml", false);
        } finally {
            response.close();
        }
    }
    
    private Response callGenerateEndpoint() {
        Client client = ClientBuilder.newClient();
        String port = System.getProperty("liberty.test.port");
        String url = "http://localhost:" + port + "/start/api/v1/generate?tech=rest&name=Test&deploy=local";
        System.out.println("Testing " + url);
        Response response = client.target(url).request(MediaType.APPLICATION_JSON).get();
        return response;
    }

}

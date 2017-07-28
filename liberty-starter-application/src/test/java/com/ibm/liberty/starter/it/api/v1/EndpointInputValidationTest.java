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

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.junit.Test;

public class EndpointInputValidationTest {
    
    @Test
    public void testServiceFinderEndpoint() throws Exception {
        String endpoint = "/start/api/v1/services";
        Response response = callEndpoint(endpoint);
        int status = response.getStatus();
        response.close();
        assertTrue("Response incorrect, response status was " + status, status == 200);
    }

    @Test
    public void testTechFinderInvalidTechType() throws Exception {
        String endpoint = "/start/api/v1/tech/ABC123";
        Response response = callEndpoint(endpoint);
        int status = response.getStatus();
        response.close();
        assertTrue("Response incorrect, response status was " + status, status == Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void testTechSelectorInvalidTechType() throws Exception {
        String endpoint = "/start/api/v1/data?tech=ABC123&deploy=local";
        Response response = callEndpoint(endpoint);
        int status = response.getStatus();
        response.close();
        assertTrue("Response incorrect, response status was " + status, status == Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void testTechSelectorValidName() throws Exception {
        String endpoint = "/start/api/v1/data?tech=test&name=testName&deploy=local";
        Response response = callEndpoint(endpoint);
        int status = response.getStatus();
        String output = "";
        if (status == 500) {
            output = response.readEntity(String.class);
            assertTrue(output.contains("Missing project generation configuration: generation URL, starter URL"));
        } else {
            assertTrue("Response incorrect, response status was " + status + " output was " + output, status == 200);
        }
        response.close();
    }

    @Test
    public void testTechSelectorInvalidName() throws Exception {
        String endpoint = "/start/api/v1/data?tech=test&name=in/valid&deploy=local";
        Response response = callEndpoint(endpoint);
        int status = response.getStatus();
        response.close();
        assertTrue("Response incorrect, response status was " + status, status == Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void testRepoInvalidTech() throws Exception {
        String url = "/start/api/v1/repo/net/wasdev/wlp/starters/abc123/provided-pom/0.0.1/provided-pom-0.0.1.pom";
        Response response = callEndpoint(url);
        int status = response.getStatus();
        response.close();
        assertTrue("Response incorrect, response status was " + status, status == Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void testRepoInvalidPath() throws Exception {
        String url = "/start/api/v1/repo/net/wasdev/wlp/starters/test/&=";
        Response response = callEndpoint(url);
        int status = response.getStatus();
        response.close();
        assertTrue("Response incorrect, response status was " + status, status == Response.Status.BAD_REQUEST.getStatusCode());
    }

    private Response callEndpoint(String endpoint) throws Exception {
        Client client = ClientBuilder.newClient();
        String port = System.getProperty("liberty.test.port");
        String url = "http://localhost:" + port + endpoint;
        System.out.println("Testing " + url);
        Response response = client.target(url).request().get();
        return response;
    }

}

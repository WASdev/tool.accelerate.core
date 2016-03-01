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
        assertTrue("Response incorrect, response status was " + status, status == 200);
    }

    @Test
    public void testTechFinderInvalidTechType() throws Exception {
        String endpoint = "/start/api/v1/tech/ABC123";
        Response response = callEndpoint(endpoint);
        int status = response.getStatus();
        assertTrue("Response incorrect, response status was " + status, status == Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void testTechSelectorInvalidTechType() throws Exception {
        String endpoint = "/start/api/v1/data?tech=ABC123&deploy=local";
        Response response = callEndpoint(endpoint);
        int status = response.getStatus();
        assertTrue("Response incorrect, response status was " + status, status == Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void testTechSelectorValidName() throws Exception {
        String endpoint = "/start/api/v1/data?tech=test&name=testName&deploy=local";
        Response response = callEndpoint(endpoint);
        int status = response.getStatus();
        assertTrue("Response incorrect, response status was " + status, status == 200);
    }

    @Test
    public void testTechSelectorInvalidName() throws Exception {
        String endpoint = "/start/api/v1/data?tech=test&name=in/valid&deploy=local";
        Response response = callEndpoint(endpoint);
        int status = response.getStatus();
        assertTrue("Response incorrect, response status was " + status, status == Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void testRepoInvalidTech() throws Exception {
        String url = "/start/api/v1/repo/net/wasdev/wlp/starters/abc123/provided-pom/0.0.1/provided-pom-0.0.1.pom";
        Response response = callEndpoint(url);
        int status = response.getStatus();
        assertTrue("Response incorrect, response status was " + status, status == Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void testRepoInvalidPath() throws Exception {
        String url = "/start/api/v1/repo/net/wasdev/wlp/starters/test/ABCDEF";
        Response response = callEndpoint(url);
        int status = response.getStatus();
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

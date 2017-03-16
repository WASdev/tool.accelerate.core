package com.ibm.liberty.starter.availability;

import static org.junit.Assert.assertTrue;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

import org.junit.Test;

public class EndpointAvailabilityTest {
	
    private String port = System.getProperty("liberty.test.port");

    @Test
    public void testLocalHostEndpoint() throws Exception {
        String url = "http://localhost:" + port + "/";
        System.out.println("Testing endpoint " + url);
        int maxCount = 30;
        int responseCode = makeRequest(url);
        for(int i = 0; (responseCode != 200) && (i < maxCount); i++) {
          System.out.println("Response code : " + responseCode + ", retrying ... (" + i + " of " + maxCount + ")");
          Thread.sleep(5000);
          responseCode = makeRequest(url);
        }
        assertTrue("Incorrect response code: " + responseCode, responseCode == 200);
    }
    
    @Test
    public void testEndpoint() throws Exception {
    	String url = "http://172.0.0.1:" + port + "/";
        System.out.println("Testing endpoint " + url);
        int maxCount = 30;
        int responseCode = makeRequest(url);
        for(int i = 0; (responseCode != 200) && (i < maxCount); i++) {
          System.out.println("Response code : " + responseCode + ", retrying ... (" + i + " of " + maxCount + ")");
          Thread.sleep(5000);
          responseCode = makeRequest(url);
        }
        assertTrue("Incorrect response code: " + responseCode, responseCode == 200);
    }
    
    @Test
    public void testStartEndpoint() throws Exception {
        String url = "http://localhost:" + port + "/start/";
        System.out.println("Testing endpoint " + url);
        int maxCount = 30;
        int responseCode = makeRequest(url);
        for(int i = 0; (responseCode != 200) && (i < maxCount); i++) {
          System.out.println("Response code : " + responseCode + ", retrying ... (" + i + " of " + maxCount + ")");
          Thread.sleep(5000);
          responseCode = makeRequest(url);
        }
        assertTrue("Incorrect response code: " + responseCode, responseCode == 200);
    }

    private int makeRequest(String url) {
      Client client = ClientBuilder.newClient();
      Invocation.Builder invoBuild = client.target(url).request();
      Response response = invoBuild.get();
      int responseCode = response.getStatus();
      response.close();
      return responseCode;
    }
}

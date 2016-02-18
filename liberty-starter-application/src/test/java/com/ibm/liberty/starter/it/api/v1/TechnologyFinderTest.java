package com.ibm.liberty.starter.it.api.v1;

import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.junit.Ignore;
import org.junit.Test;

import com.ibm.liberty.starter.api.v1.model.internal.Services;
import com.ibm.liberty.starter.api.v1.model.registration.Service;

@Ignore
//Currently broken tests, getting a message body reader not found error
public class TechnologyFinderTest {
	
	private String endpoint = "/start/api/v1/tech";

	@Test
	public void testTechList() throws Exception {
		Services services = getObjectFromEndpoint(Services.class, "", MediaType.APPLICATION_JSON_TYPE);
		List<Service> servicesList= services.getServices();
		assertTrue("Expected service list to only contain one service.", servicesList.size() == 1);
		assertTrue("Expected service to be test service.", "test".equals(servicesList.get(0).getId()));
	}
	
	@Test
	public void testTechTest() throws Exception {
		Service testService = getObjectFromEndpoint(Service.class, "/test", MediaType.APPLICATION_JSON_TYPE);
		assertTrue("Tech list incorrect", "test".equals(testService.getId()));		
	}
    
    private < E > E getObjectFromEndpoint(Class<E> klass, String extension, MediaType mediaType) {
    	String port = System.getProperty("liberty.test.port");
    	String url = "http://localhost:" + port + endpoint + extension;
    	System.out.println("Getting object from url " + url);
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(url);
        Invocation.Builder invoBuild = target.request();
        E object = invoBuild.accept(mediaType).get(klass);
        client.close();
        return object;
    }
}

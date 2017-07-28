/*******************************************************************************
 * Copyright (c) 2016 IBM Corp.
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

import com.ibm.liberty.starter.api.v1.model.internal.Services;
import com.ibm.liberty.starter.api.v1.model.registration.Service;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static org.junit.Assert.assertTrue;

@Ignore
//Currently broken tests, getting a message body reader not found error
public class TechnologyEndpointTest {
    
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

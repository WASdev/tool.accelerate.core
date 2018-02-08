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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

public class RepositoryClient {
    
    public static String invoke(String file, int expectedStatus) throws Exception {
        Client client = ClientBuilder.newClient();
        String port = System.getProperty("liberty.test.port");
        String url = "http://localhost:" + port + "/start/api/v1/repo/" + file;
        System.out.println("Testing " + url);
        Response response = client.target(url).request().get();
        try {
            int responseStatus = response.getStatus();
            InputStream responseStream = response.readEntity(InputStream.class);
            String output = inputStreamToString(responseStream);
            assertTrue("Response status is: " + responseStatus + " Response message: " + output, responseStatus == expectedStatus);
            return output;
        } finally {
            response.close();
        }
    }
    
    private static String inputStreamToString(InputStream inputStream) throws IOException {
        InputStreamReader isr = new InputStreamReader(inputStream);
        char[] chars = new char[1024];
        StringBuilder responseBuilder = new StringBuilder();

        int read;
        while ((read = isr.read(chars)) != -1) {
            responseBuilder.append(chars, 0, read);
        }
        return responseBuilder.toString();
    }

}

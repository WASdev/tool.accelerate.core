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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class PluginAPITest {

	@Test
    public void provideNoInput() throws Exception {
        Response response = makeTheCall();
        assertEquals("The server return an error if there are no parameters", 400, response.getStatus());

    }

    @Test
    public void provideOnlyTechName() throws Exception {
        Response response = makeTheCall(new TestHeader("techName", "techName"));
        assertEquals("The server return an error if there are no parameters", 400, response.getStatus());

    }
  
    @Test
    public void provideOnlyNamespace() throws Exception {
        Response response = makeTheCall(new TestHeader("namespace", "namespace"));
        assertEquals("The server return an error if there are no parameters", 400, response.getStatus());

    }
    
    @Test
    public void checkReturnedPayload() throws Exception {
        Response response = makeTheCall(new TestHeader("namespace", "namespace"), new TestHeader("techName", "techName"));
        assertEquals("The server return an error if there are no parameters", 200, response.getStatus());

        // Build up an idea of the structure we expect back and then test for it
        ZipStructure expectedFormat = new ZipStructure("libertyPluginProject.zip");
        expectedFormat.addFile("entry1.txt", "Hello world!\nHello line 2");
        
        ZipStructure actualZip = parseResponse(response);
        assertTrue("The expectedZip should match what we had returned to us", expectedFormat.looksLike(actualZip));
        
        String apiKey = response.getHeaderString("returnedApiKey");
        String fileNameHeader = response.getHeaderString("returnedFileName");
        assertEquals("An API key should be returned", "myNewShinyKey", apiKey);
        assertEquals("We should have a header that gives the file name", "libertyPluginProject.zip", fileNameHeader);
    }
    
	private Response makeTheCall(TestHeader... headers) {
		Client client = ClientBuilder.newClient();
        String port = System.getProperty("liberty.test.port");
        String url = "http://localhost:" + port + "/start/api/v1/plugin";
        WebTarget target = client.target(url);

        for (TestHeader header : headers) {
        	target = target.queryParam(header.getKey(), header.getValue());
		}

        Response response = target.request("application/zip").post(null);
		return response;
	}
    
    private ZipStructure parseResponse(Response response) throws Exception {
    	String zipName = "";
    	List<Object> contentDisposition = response.getHeaders().get("Content-Disposition");
    	for (Object object : contentDisposition) {
			String header = (String) object;
			if (header.contains("filename")) {
				zipName = header.substring(header.indexOf("filename=\"") + "filename=\"".length(), header.length() -1);
				break;
			}
		}
    	ZipStructure returnedZip = new ZipStructure(zipName);
        // Read the response into an InputStream
        InputStream entityInputStream = response.readEntity(InputStream.class);
        // Create a new ZipInputStream from the response InputStream
        ZipInputStream zipIn = new ZipInputStream(entityInputStream);
        ZipEntry inputEntry = null;
        while ((inputEntry = zipIn.getNextEntry()) != null) {
            String entryName = inputEntry.getName();
            InputStreamReader isr = new InputStreamReader(zipIn);
            BufferedReader br = new BufferedReader(isr);
            String line = br.readLine();
            StringBuilder sb = new StringBuilder();
            if (line != null) {
            	sb.append(line);
            }
            line = br.readLine();
            while (line != null) {
            	sb.append("\n" + line);
            	line = br.readLine();
            }
            returnedZip.addFile(entryName, sb.toString());
            zipIn.closeEntry();
        }
        zipIn.close();
        System.out.println("Zip is " + returnedZip.toString());
		return returnedZip;
    }
    
    public static class TestHeader {

    	String key;
    	String value;
		public TestHeader(String key, String value) {
			this.key = key;
			this.value = value;
		}

		public String getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}

	}
}

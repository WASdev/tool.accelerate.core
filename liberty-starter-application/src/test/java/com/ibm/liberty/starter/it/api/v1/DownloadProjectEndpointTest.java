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

import javax.ws.rs.core.Response;

import org.junit.Test;

import com.ibm.liberty.starter.it.api.v1.utils.DownloadZip;

public class DownloadProjectEndpointTest {
    
    @Test
    public void generateMavenProjectTest() throws Exception {
        String queryString = "tech=rest&name=Test&deploy=local";
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
    
    @Test
    public void generateGradleProjectTest() throws Exception {
        String queryString = "tech=rest&name=Test&deploy=local&build=GRADLE";
        Response response = DownloadZip.get(queryString);
        try {
            int responseStatus = response.getStatus();
            String output = "";
            if (responseStatus != 200) {
                output = response.readEntity(String.class);
                assumeTrue(!output.contains("Missing project generation configuration: generation URL, starter URL"));
            }
            assertTrue("Expected response status 200, instead found " + responseStatus + " and output " + output, responseStatus == 200);
            DownloadZip.assertBasicContent(response, "build.gradle", false);
        } finally {
            response.close();
        }
    }
    
    @Test
    public void generateBluemixProjectTest() throws Exception {
        String queryString = "tech=rest&name=Test&deploy=bluemix";
        Response response = DownloadZip.get(queryString);
        try {
            int responseStatus = response.getStatus();
            String output = "";
            if (responseStatus != 200) {
                output = response.readEntity(String.class);
                assumeTrue(!output.contains("Missing project generation configuration: generation URL, starter URL"));
            }
            assertTrue("Expected response status 200, instead found " + responseStatus + " and output " + output, responseStatus == 200);
            DownloadZip.assertBasicContent(response, "pom.xml", true);
        } finally {
            response.close();
        }
    }
    
    
    

}

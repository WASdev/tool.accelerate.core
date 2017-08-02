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
package com.ibm.liberty.starter.service.swagger.api.v1.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import com.ibm.liberty.starter.api.v1.model.provider.EndpointResponse;

/**
 * Test the deployed service responds as expected
 * 
 */
public class TestApplication extends EndpointTest {

    @Before
    public void checkSetup() {
        checkAvailability("/api/v1/provider/");
    }

    @Test
    public void testProvider() throws Exception {
        EndpointResponse response = testEndpoint("/api/v1/provider/", EndpointResponse.class);
        String status = response.getStatus();
        assertTrue("Expected response to be UP, instead found" + status, status.equals("UP"));
    }
    
    @Test
    public void testPrepareDynamicPackages() throws Exception {
        String serverOutputDir = new File("./build/test/wlp/servers/StarterServer").getCanonicalPath().replace('\\', '/');
        System.out.println("serverOutputDir=" + serverOutputDir);
        String uuid = UUID.randomUUID().toString();
        String swaggerTechDirPath = serverOutputDir + "/workarea/appAccelerator/" + uuid + "/swagger";
        File swaggerFile = new File(swaggerTechDirPath + "/server/src/sampleSwagger.json");
        createSampleSwagger(swaggerFile);
        assertTrue("Swagger file doesn't exist : " + swaggerFile.getCanonicalPath(), swaggerFile.exists());
        String actual = testEndpoint("/api/v1/provider/packages/prepare?path=" + swaggerTechDirPath + "&options=server");
        assertNotNull("No response from API for packages/prepare", actual);
        assertEquals("Response doesn't match : " + actual, "success", actual);
        String packagedFilePath = swaggerTechDirPath + "/package/src/sampleSwagger.json";
        assertTrue("Swagger file was not packaged successfully : " + packagedFilePath, new File(packagedFilePath).exists());
    }
    
    private void createSampleSwagger(File file) throws Exception{
        String swaggerContent = "{\"swagger\": \"2.0\",\"info\": {\"description\": \"Info APIs for Collective\",\"version\": \"1.0.0\"},\"basePath\": \"/\","
                + "\"paths\": {\"/ibm/api/root1/v1/info\": {\"get\": {\"summary\": \"Retrieve collective's core information\","
                + "\"description\": \"Returns a JSON with core information about collective\",\"operationId\": \"getInfo\",\"produces\": "
                + "[\"application/json\"],\"responses\": {\"200\": {\"description\": \"successful operation\","
                + "\"schema\": {\"$ref\": \"#/definitions/CollectiveInfo\"}},\"404\": {\"description\": \"Invalid path\"}}}}},\"definitions\": {"
                + "\"CollectiveInfo\": {\"properties\": {\"name\": {\"type\": \"string\",\"description\": \"Name of the collective\"}}}}}";
        FileUtils.writeStringToFile(file, swaggerContent);
    }

}

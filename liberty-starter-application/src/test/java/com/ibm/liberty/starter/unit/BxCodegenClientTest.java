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
package com.ibm.liberty.starter.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.ibm.liberty.starter.ProjectConstructionInputData;
import com.ibm.liberty.starter.ProjectConstructor;
import com.ibm.liberty.starter.api.v1.model.internal.Services;
import com.ibm.liberty.starter.api.v1.model.registration.Service;
import com.ibm.liberty.starter.exception.ProjectGenerationException;
import com.ibm.liberty.starter.unit.utils.MockBxCodegenClient;

public class BxCodegenClientTest {
    
    private static ProjectConstructionInputData inputData;
    private static ProjectConstructionInputData inputDataWithId;
    
    @Rule
    public ExpectedException exceptions = ExpectedException.none();
    
    @BeforeClass
    public static void setup() {
        List<Service> servicesList = new ArrayList<Service>();
        servicesList.add(new Service());
        Services services = new Services();
        services.setServices(servicesList);
        inputData = new ProjectConstructionInputData(services, null, "TestName", ProjectConstructor.DeployType.LOCAL, ProjectConstructor.BuildType.MAVEN, "workspaceDir", null, "testArtifactId", "test.group.id", null);
        inputDataWithId = new ProjectConstructionInputData(services, null, "TestName", ProjectConstructor.DeployType.LOCAL, ProjectConstructor.BuildType.MAVEN, "workspaceDir", null, "testArtifactId", "test.group.id", "5678");
    }

    @Test
    public void getFileMapCallsScaffolderWhenFinished() {
        MockBxCodegenClient bxClient = new MockBxCodegenClient();
        assumeTrue(bxClient.URL != null);
        assumeTrue(bxClient.STARTERKIT_URL != null);
        bxClient.setStatus("FINISHED");
        Map<String, byte[]> files = bxClient.getFileMap(inputData);
        assertTrue("Expected file map to be returned successfully", files.keySet().contains("1234"));
    }
    
    @Test
    public void getFileMapThrowsExceptionWhenFailed() {
        MockBxCodegenClient bxClient = new MockBxCodegenClient();
        assumeTrue(bxClient.URL != null);
        assumeTrue(bxClient.STARTERKIT_URL != null);
        exceptions.expect(ProjectGenerationException.class);
        exceptions.expectMessage("Code generation failed for job with id: 1234. Try again later.");
        bxClient.setStatus("FAILED");
        bxClient.getFileMap(inputData);
    }
    
    @Test
    public void getFileMapThrowsExceptionWhenRUNNING() {
        MockBxCodegenClient bxClient = new MockBxCodegenClient();
        assumeTrue(bxClient.URL != null);
        assumeTrue(bxClient.STARTERKIT_URL != null);
        exceptions.expect(ProjectGenerationException.class);
        exceptions.expectMessage("Code generation for job with id 1234 timed out. Try again later");
        bxClient.setStatus("RUNNING");
        bxClient.getFileMap(inputData);
        int retryCount = bxClient.statusCount;
        assertEquals(9, retryCount);
    }
    
    @Test
    public void getFileMapThrowsExceptionWhenUnexpectedStatus() {
        MockBxCodegenClient bxClient = new MockBxCodegenClient();
        assumeTrue(bxClient.URL != null);
        assumeTrue(bxClient.STARTERKIT_URL != null);
        exceptions.expect(ProjectGenerationException.class);
        exceptions.expectMessage("Did not receive FINISHED from Bx codegen for job with id: 1234. Status received:UNKNOWN");
        bxClient.setStatus("UNKNOWN");
        bxClient.getFileMap(inputData);
    }
    
    @Test
    public void getFileMapThrowsExceptionWhenNullURL() {
        MockBxCodegenClient bxClient = new MockBxCodegenClient();
        assumeTrue(bxClient.URL == null);
        assumeTrue(bxClient.STARTERKIT_URL == null);
        exceptions.expect(ProjectGenerationException.class);
        exceptions.expectMessage("Missing project generation configuration: generation URL, starter URL");
        bxClient.getFileMap(inputData);
    }
    
    @Test
    public void getFileMapWithIdCallsScaffolder() {
        MockBxCodegenClient bxClient = new MockBxCodegenClient();
        assumeTrue(bxClient.URL != null);
        assumeTrue(bxClient.STARTERKIT_URL != null);
        Map<String, byte[]> files = bxClient.getFileMap(inputDataWithId);
        assertTrue("Expected file map to be returned successfully", files.keySet().contains("5678"));
        int retryCount = bxClient.statusCount;
        assertEquals(0, retryCount);
    }
    
    @Test
    public void generateProjectCallsScaffolderWhenFinished() {
        MockBxCodegenClient bxClient = new MockBxCodegenClient();
        assumeTrue(bxClient.URL != null);
        assumeTrue(bxClient.STARTERKIT_URL != null);
        bxClient.setStatus("FINISHED");
        String id = bxClient.generateProject(inputData);
        assertTrue("Expected id to be returned successfully", "1234".equals(id));
    }
    
    @Test
    public void generateProjectThrowsExceptionWhenFailed() {
        MockBxCodegenClient bxClient = new MockBxCodegenClient();
        assumeTrue(bxClient.URL != null);
        assumeTrue(bxClient.STARTERKIT_URL != null);
        exceptions.expect(ProjectGenerationException.class);
        exceptions.expectMessage("Code generation failed for job with id: 1234. Try again later.");
        bxClient.setStatus("FAILED");
        bxClient.generateProject(inputData);
    }
    
    @Test
    public void generateProjectThrowsExceptionWhenRUNNING() {
        MockBxCodegenClient bxClient = new MockBxCodegenClient();
        assumeTrue(bxClient.URL != null);
        assumeTrue(bxClient.STARTERKIT_URL != null);
        exceptions.expect(ProjectGenerationException.class);
        exceptions.expectMessage("Code generation for job with id 1234 timed out. Try again later");
        bxClient.setStatus("RUNNING");
        bxClient.generateProject(inputData);
        int retryCount = bxClient.statusCount;
        assertEquals(9, retryCount);
    }
    
    @Test
    public void generateProjectThrowsExceptionWhenUnexpectedStatus() {
        MockBxCodegenClient bxClient = new MockBxCodegenClient();
        assumeTrue(bxClient.URL != null);
        assumeTrue(bxClient.STARTERKIT_URL != null);
        exceptions.expect(ProjectGenerationException.class);
        exceptions.expectMessage("Did not receive FINISHED from Bx codegen for job with id: 1234. Status received:UNKNOWN");
        bxClient.setStatus("UNKNOWN");
        bxClient.generateProject(inputData);
    }
    
    @Test
    public void generateProjectThrowsExceptionWhenNullURL() {
        MockBxCodegenClient bxClient = new MockBxCodegenClient();
        assumeTrue(bxClient.URL == null);
        assumeTrue(bxClient.STARTERKIT_URL == null);
        exceptions.expect(ProjectGenerationException.class);
        exceptions.expectMessage("Missing project generation configuration: generation URL, starter URL");
        bxClient.generateProject(inputData);
    }
    
}

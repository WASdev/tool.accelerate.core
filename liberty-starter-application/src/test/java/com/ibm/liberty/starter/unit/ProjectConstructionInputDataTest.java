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

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ibm.liberty.starter.ProjectConstructionInputData;
import com.ibm.liberty.starter.ProjectConstructor;
import com.ibm.liberty.starter.api.v1.model.internal.Services;
import com.ibm.liberty.starter.api.v1.model.registration.Service;

public class ProjectConstructionInputDataTest {
    
    static ProjectConstructionInputData inputData;
    static ProjectConstructionInputData inputDataWithBx;
    static ProjectConstructionInputData inputDataWithGradle;
    static ProjectConstructionInputData inputDataWithNullIds;
    static ProjectConstructionInputData inputDataWithBeta;
    
    @BeforeClass
    public static void setup() {
        Service foo = new Service();
        foo.setId("foo");
        Service bar = new Service();
        bar.setId("bar");
        List<Service> serviceList = new ArrayList<Service>();
        serviceList.add(foo);
        serviceList.add(bar);
        Services services = new Services();
        services.setServices(serviceList);
        inputData = new ProjectConstructionInputData(services, null, "testName", ProjectConstructor.DeployType.LOCAL, ProjectConstructor.BuildType.MAVEN, "c:/users/1234", new String[]{"foo:bar", "test:value"}, "testArtifact", "testGroup", "5678", false);
        inputDataWithBx = new ProjectConstructionInputData(services, null, "testName", ProjectConstructor.DeployType.BLUEMIX, ProjectConstructor.BuildType.MAVEN, "c:/users/1234", new String[]{"foo:bar", "test:value"}, "testArtifact", "testGroup", "5678", false);
        inputDataWithGradle = new ProjectConstructionInputData(services, null, "testName", ProjectConstructor.DeployType.LOCAL, ProjectConstructor.BuildType.GRADLE, "c:/users/1234", new String[]{"foo:bar", "test:value"}, "testArtifact", "testGroup", "5678", false);
        inputDataWithNullIds = new ProjectConstructionInputData(services, null, "testName", ProjectConstructor.DeployType.LOCAL, ProjectConstructor.BuildType.MAVEN, "c:/users/1234", new String[]{"foo:bar", "test:value"}, null, null, "5678", false);
        inputDataWithBeta = new ProjectConstructionInputData(services, null, "testName", ProjectConstructor.DeployType.LOCAL, ProjectConstructor.BuildType.MAVEN, "c:/users/1234", new String[]{"foo:bar", "test:value"}, "testArtifact", "testGroup", "5678", true);
    }
    
    @Test
    public void toBxJsonTest() throws Exception {
        String expected = "{\"technologies\":\"foo,bar\","
                + "\"appName\":\"testName\","
                + "\"buildType\":\"maven\","
                + "\"createType\":\"picnmix\","
                + "\"platforms\":\"\","
                + "\"artifactId\":\"testArtifact\","
                + "\"groupId\":\"testGroup\"}";
        assertEquals(expected, inputData.toBxJSON());
    }
    
    @Test
    public void toBxJsonWithGradleTest() throws Exception {
        String expected = "{\"technologies\":\"foo,bar\","
                + "\"appName\":\"testName\","
                + "\"buildType\":\"gradle\","
                + "\"createType\":\"picnmix\","
                + "\"platforms\":\"\","
                + "\"artifactId\":\"testArtifact\","
                + "\"groupId\":\"testGroup\"}";
        assertEquals(expected, inputDataWithGradle.toBxJSON());
    }
    
    @Test
    public void toBxJsonWithBluemixTest() throws Exception {
        String expected = "{\"technologies\":\"foo,bar\","
                + "\"appName\":\"testName\","
                + "\"buildType\":\"maven\","
                + "\"createType\":\"picnmix\","
                + "\"platforms\":\"bluemix\","
                + "\"artifactId\":\"testArtifact\","
                + "\"groupId\":\"testGroup\"}";
        assertEquals(expected, inputDataWithBx.toBxJSON());
    }
    
    @Test
    public void toBxJsonWithNullIdsTest() throws Exception {
        String json = inputDataWithNullIds.toBxJSON();
        assertTrue("Expected json not to include artifactId, found " + json, !json.contains("artifactId"));
        assertTrue("Expected json not to include groupId, found " + json, !json.contains("groupId"));
    }
    
    @Test
    public void toBxJsonTestWithBeta() throws Exception {
        String expected = "{\"technologies\":\"foo,bar\","
                + "\"appName\":\"testName\","
                + "\"buildType\":\"maven\","
                + "\"createType\":\"picnmix\","
                + "\"platforms\":\"\","
                + "\"artifactId\":\"testArtifact\","
                + "\"groupId\":\"testGroup\","
                + "\"libertybeta\":\"true\"}";
        assertEquals(expected, inputDataWithBeta.toBxJSON());
    }
    
    @Test
    public void toRequestQueryStringTest() throws Exception {
        String[] expected = {"tech=foo", "tech=bar", "name=testName", "deploy=local", "build=MAVEN", "workspace=1234", "techoptions=foo:bar", "techoptions=test:value", "artifactId=testArtifact", "groupId=testGroup", "generationId=5678", "beta=false"};
        String actual = "&" + inputData.toRequestQueryString(null) + "&";
        for(String expectedString : expected) {
            assertTrue("Didn't find expected string " + expectedString, actual.contains("&" + expectedString + "&"));
        }
    }
    
    @Test
    public void toRequestQueryStringTestWithId() throws Exception {
        String[] expected = {"tech=foo", "tech=bar", "name=testName", "deploy=local", "build=MAVEN", "workspace=1234", "techoptions=foo:bar", "techoptions=test:value", "artifactId=testArtifact", "groupId=testGroup", "generationId=abcd", "beta=false"};
        String actual = "&" + inputData.toRequestQueryString("abcd") + "&";
        for(String expectedString : expected) {
            assertTrue("Didn't find expected string " + expectedString, actual.contains("&" + expectedString + "&"));
        }
    }

}

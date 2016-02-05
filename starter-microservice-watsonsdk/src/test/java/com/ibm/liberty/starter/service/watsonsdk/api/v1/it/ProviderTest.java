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
package com.ibm.liberty.starter.service.watsonsdk.api.v1.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ibm.liberty.starter.api.v1.model.provider.Dependency;
import com.ibm.liberty.starter.api.v1.model.provider.Location;
import com.ibm.liberty.starter.api.v1.model.provider.Provider;

/**
 * Test the deployed service responds as expected
 *
 */
public class ProviderTest extends EndpointTest {
	
	private static Provider provider = null;

	@BeforeClass
	public static void checkSetup() throws Exception {
		checkAvailability("/api/v1/provider/");
		provider =  testEndpoint("/api/v1/provider/", Provider.class);
        assertNotNull("The response from the provider for the API should not be null", provider);
	}

    @Test
    public void testDescription() throws Exception {
        assertTrue("The description should contain a second level header of \"Watson SDK\".", provider.getDescription().contains("<h2>Watson SDK</h2>"));
    }
    
    @Test
    public void testDependencyCount() throws Exception {
    	assertEquals("There should be only one dependency", 1, provider.getDependencies().length);
    }
    
    @Test
    public void testCompileDependencySpecified() throws Exception {
    	Dependency compileDependency = provider.getDependencies()[0];
    	assertEquals("The first and hopefully only dependency is a compile dependency", Dependency.Scope.COMPILE, compileDependency.getScope());
    	assertEquals("The Group ID should be correct", "net.wasdev.wlp.starters.watsonsdk", compileDependency.getGroupId());
    	assertEquals("The Artifact ID should be correct", "compile-pom", compileDependency.getArtifactId());
        assertEquals("The Version should be correct.", "0.0.1", compileDependency.getVersion());
    }
    
    @Test
    public void testLocations() throws Exception {
    	Location repoLocations = provider.getRepoUrl();
    	assertNotNull("The provider should return a repo location", repoLocations);
    	assertNotNull("The url should not be null", repoLocations.getUrl());
    	assertTrue("The location url "+ repoLocations.getUrl() + " should end with the path to the artifacts", repoLocations.getUrl().endsWith("artifacts"));
    }
    
    @Test
    public void testCompileDependency() throws Exception {
    	Location repoLocations = provider.getRepoUrl();
    	assertNotNull("The provider should return a repo location", repoLocations);
    	String repoURL = repoLocations.getUrl();
    	assertNotNull("The url should not be null", repoURL);
    	String compilePom = makeRequest(repoURL + "/net/wasdev/wlp/starters/watsonsdk/compile-pom/0.0.1/compile-pom-0.0.1.pom");
    	assertTrue("The returned file should contain the group Id", compilePom.contains("<groupId>net.wasdev.wlp.starters.watsonsdk</groupId>"));
    }
}

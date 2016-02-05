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
package com.ibm.liberty.starter.service.test.api.v1.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.ibm.liberty.starter.api.v1.model.provider.Dependency;
import com.ibm.liberty.starter.api.v1.model.provider.Location;
import com.ibm.liberty.starter.api.v1.model.provider.Provider;
import com.ibm.liberty.starter.api.v1.model.provider.Sample;

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
        Provider provider =  testEndpoint("/api/v1/provider/", Provider.class);
        assertNotNull("No response from API for provider", provider);
        assertTrue("Description was not found.", provider.getDescription().contains("<h2>Test</h2>"));
        Location location  = provider.getRepoUrl();
        assertNotNull("Repo url null.", location.getUrl());
        assertTrue("Repo url incorrect.", location.getUrl().endsWith("/artifacts"));
        testDependencies(provider.getDependencies());

    }
    
    private void testDependencies(Dependency[] dependencies) {
        boolean scopes[] = new boolean[Dependency.Scope.values().length];
        for (Dependency dependency : dependencies) {
            scopes[dependency.getScope().ordinal()] = checkDependency(dependency);
        }
        for(int i = 0; i < scopes.length; i++) {
            assertTrue(Dependency.Scope.values()[i] + " dependency was incorrect.", scopes[i]);
        }
    }
    
    private boolean checkDependency(Dependency dependency) {
        assertTrue(dependency.getScope().name() + " groupId incorrect.", "net.wasdev.wlp.starters.test".equals(dependency.getGroupId()));
        assertTrue(dependency.getScope().name() + " artifactId incorrect.", (dependency.getScope().name().toLowerCase() + "-pom").equals(dependency.getArtifactId()));
        assertTrue(dependency.getScope().name() + " version incorrect.", "0.0.1".equals(dependency.getVersion()));
        return true;
    }
    
    @Test
    public void testSamples() throws Exception {
    	Sample sample = testEndpoint("/api/v1/provider/samples", Sample.class);
    	assertNotNull("No response from API for sample", sample);
    	if(sample.getLocations() == null) {
    		return;
    	}
    	assertEquals("2 files were expected for sample", 2, sample.getLocations().length);
    }

}

/*******************************************************************************
 * Copyright (c) 2016,17 IBM Corp.
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
package com.ibm.liberty.starter.service.microprofile.api.v1.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.ibm.liberty.starter.api.v1.model.provider.Dependency;
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
        Provider provider = testEndpoint("/api/v1/provider/", Provider.class);
        assertNotNull("No response from API for provider", provider);
        assertTrue("Description was not found.", provider.getDescription().contains("<h2>MicroProfile</h2>"));
        Dependency[] dependencies = provider.getDependencies();
        boolean providedDependency = false;
        boolean runtimeDependency = false;
        for (Dependency dependency : dependencies) {
            if (Dependency.Scope.PROVIDED.equals(dependency.getScope())) {
                assertEquals("groupId incorrect.", "net.wasdev.wlp.starters.microprofile", dependency.getGroupId());
                assertEquals("artifactId incorrect.", "provided-pom", dependency.getArtifactId());
                assertEquals("version incorrect.", "0.0.2", dependency.getVersion());
                providedDependency = true;
            }
            if (Dependency.Scope.RUNTIME.equals(dependency.getScope())) {
                assertEquals("groupId incorrect.", "net.wasdev.wlp.starters.microprofile", dependency.getGroupId());
                assertEquals("artifactId incorrect.", "runtime-pom", dependency.getArtifactId());
                assertEquals("version incorrect.", "0.0.2", dependency.getVersion());
                runtimeDependency = true;
            }
        }
        assertTrue("Provided dependencies were specified.", providedDependency);
        assertTrue("Runtime dependencies were specified.", runtimeDependency);
    }

    @Test
    public void testSamples() throws Exception {
        Sample sample = testEndpoint("/api/v1/provider/samples", Sample.class);
        assertNotNull("No response from API for sample", sample);
        assertNotNull("Expected locations", sample.getLocations());
        assertEquals("Expected no samples.", 0, sample.getLocations().length);
    }

}

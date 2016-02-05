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
package com.ibm.liberty.starter.unit;

import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.ibm.liberty.starter.DependencyHandler;
import com.ibm.liberty.starter.api.v1.model.internal.Services;
import com.ibm.liberty.starter.api.v1.model.provider.Dependency;
import com.ibm.liberty.starter.api.v1.model.provider.Dependency.Scope;
import com.ibm.liberty.starter.api.v1.model.registration.Service;

public class DependencyHandlerTest {
    
    @Test
    public void testSettingDependencies() throws URISyntaxException {
        URI uri = new URI("");
        Dependency[] dependencies = new Dependency[3];
        dependencies[0] = createDependency(Dependency.Scope.PROVIDED, "wibble");
        dependencies[1] = createDependency(Dependency.Scope.RUNTIME, "wibble");
        dependencies[2] = createDependency(Dependency.Scope.COMPILE, "wibble");
        MockServiceConnector serviceConnector = new MockServiceConnector(uri, dependencies);
        String [] services = {"wibble"};
        DependencyHandler depHand = new DependencyHandler(getServicesObject(services), serviceConnector);
        Map<String, Dependency> providedDependency = depHand.getProvidedDependency();
        Set<String> providedKeys = providedDependency.keySet();
        assertTrue("Expected one provided dependency. Found " + providedKeys.size(), providedKeys.size() == 1);
        assertTrue("Expected provided dependency with scope PROVIDED.", Dependency.Scope.PROVIDED.equals(providedDependency.get("wibble").getScope()));
        
        Map<String, Dependency> runtimeDependency = depHand.getRuntimeDependency();
        Set<String> runtimeKeys = runtimeDependency.keySet();
        assertTrue("Expected one runtime dependency. Found " + runtimeKeys.size(), runtimeKeys.size() == 1);
        assertTrue("Expected runtime dependency with scope RUNTIME.", Dependency.Scope.RUNTIME.equals(runtimeDependency.get("wibble").getScope()));
        
        Map<String, Dependency> compileDependency = depHand.getCompileDependency();
        Set<String> compileKeys = compileDependency.keySet();
        assertTrue("Expected one compile dependency. Found " + compileKeys.size(), compileKeys.size() == 1);
        assertTrue("Expected compile dependency with scope COMPILE.", Dependency.Scope.COMPILE.equals(compileDependency.get("wibble").getScope()));
    }
    
    private static Dependency createDependency(Scope scope, String serviceId) {
        Dependency dependency = new Dependency();
        dependency.setGroupId("net.wasdev.wlp.starters." + serviceId);
        dependency.setArtifactId(scope.name().toLowerCase() + "-pom");
        dependency.setScope(scope);
        dependency.setVersion("0.0.1");
        return dependency;
    }
    
    private static Services getServicesObject(String[] serviceIds) {
        Services services = new Services();
        List<Service> serviceList = new ArrayList<Service>();
        for (String serviceId : serviceIds) {
            Service service = new Service();
            service.setId(serviceId);
            serviceList.add(service);
        }
        services.setServices(serviceList);
        return services;
    }

}

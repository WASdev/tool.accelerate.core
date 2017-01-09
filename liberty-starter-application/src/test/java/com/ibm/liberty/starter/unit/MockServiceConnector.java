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

import com.ibm.liberty.starter.ServiceConnector;
import com.ibm.liberty.starter.api.v1.model.internal.Services;
import com.ibm.liberty.starter.api.v1.model.provider.Dependency;
import com.ibm.liberty.starter.api.v1.model.provider.Location;
import com.ibm.liberty.starter.api.v1.model.provider.Provider;
import com.ibm.liberty.starter.api.v1.model.provider.Sample;
import com.ibm.liberty.starter.api.v1.model.registration.Service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class MockServiceConnector extends ServiceConnector {
    
    private Dependency[] dependencies;
    public String capturedTechWorkspaceDir;
    public String capturedOptions;
    public String[] capturedTechs;

    public MockServiceConnector(URI uri, Dependency[] dependencies) {
        super(uri);
        this.dependencies = dependencies;
    }
    
    @Override
    public Services parseServicesJson() {
        Service wibble  = new Service();
        wibble.setId("wibble");
        List<Service> serviceList = new ArrayList<Service>();
        serviceList.add(wibble);
        Services services = new Services();
        services.setServices(serviceList);
        return services;
    }
    
    @Override
    public Provider getProvider(Service service) {
        Provider provider = new Provider();
        provider.setDescription("wibble description");
        provider.setDependencies(dependencies);
        return provider;
    }

    @Override
    public Sample getSample(Service service) {
        Sample sample = new Sample();
        sample.setLocations(new Location[]{});
        return sample;
    }

    @Override
    public String prepareDynamicPackages(Service service, String techWorkspaceDir, String options, String[] techs) {
        capturedTechWorkspaceDir = techWorkspaceDir;
        capturedOptions = options;
        capturedTechs = techs;
        return null;
    }

    @Override
    public String getFeaturesToInstall(Service service) {
        return null;
    }
}

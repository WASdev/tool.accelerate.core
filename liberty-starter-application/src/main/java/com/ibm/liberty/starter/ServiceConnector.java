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
package com.ibm.liberty.starter;

import java.io.InputStream;
import java.net.URI;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.ibm.liberty.starter.api.v1.model.internal.Services;
import com.ibm.liberty.starter.api.v1.model.provider.Provider;
import com.ibm.liberty.starter.api.v1.model.provider.Sample;
import com.ibm.liberty.starter.api.v1.model.registration.Service;

public class ServiceConnector {
    
    private static final Logger log = Logger.getLogger(ServiceConnector.class.getName());
    
    private final Services services;
    private String serverHostPort;
    
    public ServiceConnector(URI uri) {
        String scheme = uri.getScheme();
        String authority = uri.getAuthority();
        serverHostPort = scheme + "://" + authority;
        services = parseServicesJson();
    }
    
    public Services parseServicesJson() {
        log.info("Parsing services json file. SERVER_HOST_PORT=" + serverHostPort);
        if (serverHostPort == null) {
            
        }
        Services services = getObjectFromEndpoint(Services.class, 
                                                  serverHostPort + "/start/api/v1/services", 
                                                  MediaType.APPLICATION_JSON_TYPE);
        log.info("(SysOut) Setting SERVICES object to " + services.getServices());
        return services;
    }
    
    public Services getServices() {
        return services;
    }
    
    // Returns the service object associated with the given id
    public Service getServiceObjectFromId(String id) {
        System.out.println("Return service object for " + id);
        Service service = null;
        for (Service s : services.getServices()) {
            if (id.equals(s.getId())) {
                return s;
            }
        }
        return service;
    }
    
    public Provider getProvider(Service service) {
        String url = urlConstructor("/api/v1/provider", service);
        Provider provider = getObjectFromEndpoint(Provider.class, url, MediaType.APPLICATION_JSON_TYPE);
        return provider;
    }
    
    public Sample getSample(Service service) {
        String url = urlConstructor("/api/v1/provider/samples", service);
        Sample sample = getObjectFromEndpoint(Sample.class, url, MediaType.APPLICATION_JSON_TYPE);
        return sample;
    }
    
    public InputStream getResourceAsInputStream(String url) {
        return getObjectFromEndpoint(InputStream.class, url, MediaType.WILDCARD_TYPE);
    }
    
    public InputStream getArtifactAsInputStream(Service service, String extension) {
        extension = extension.startsWith("/") ? extension.substring(1) : extension;
        String url = urlConstructor("/artifacts/" + extension, service);
        return getObjectFromEndpoint(InputStream.class, url, MediaType.WILDCARD_TYPE);
    }
    
    private String urlConstructor(String extension, Service service) {
        String url = serverHostPort + service.getEndpoint() + extension;
        return url;
    }
    
    public < E > E getObjectFromEndpoint(Class<E> klass, String url, MediaType mediaType) {
        System.out.println("Getting object from url " + url);
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(url);
        Invocation.Builder invoBuild = target.request();
        E object = invoBuild.accept(mediaType).get(klass);
        client.close();
        return object;
    }
    
    public Response getResponseFromEndpoint(String url, MediaType mediaType) {
        System.out.println("Getting object from url " + url);
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(url);
        Invocation.Builder invoBuild = target.request();
        Response response = invoBuild.accept(mediaType).get();
        client.close();
        return response;
    }

}

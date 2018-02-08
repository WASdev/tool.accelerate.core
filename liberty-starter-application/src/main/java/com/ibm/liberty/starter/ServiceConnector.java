/*******************************************************************************
 * Copyright (c) 2016,2017 IBM Corp.
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
import com.ibm.liberty.starter.api.v1.model.registration.Service;

public class ServiceConnector {
    
    private static final Logger log = Logger.getLogger(ServiceConnector.class.getName());
    
    private final Services services;
    private String serverHostPort;
    private final String internalServerHostPort;
    
    public ServiceConnector(URI uri) {
        String scheme = uri.getScheme();
        String authority = uri.getAuthority();
        serverHostPort = scheme + "://" + authority;
        internalServerHostPort = "http://" + authority;
        services = parseServicesJson();
    }

    public ServiceConnector(String hostPort, String internalHostPort) {
        serverHostPort = hostPort;
        internalServerHostPort = internalHostPort;
        services = parseServicesJson();
    }
    
    public Services parseServicesJson() {
        log.fine("Parsing services json file. INTERNAL_SERVER_HOST_PORT=" + internalServerHostPort);
        Services services = getObjectFromEndpoint(Services.class, 
                internalServerHostPort + "/start/api/v1/services", 
                                                  MediaType.APPLICATION_JSON_TYPE);
        log.fine("Setting SERVICES object to " + services.getServices());
        return services;
    }
    
    public Services getServices() {
        return services;
    }
    
    public String getServerHostPort() {
        return serverHostPort;
    }
    
    // Returns the service object associated with the given id
    public Service getServiceObjectFromId(String id) {
        log.fine("Return service object for " + id);
        Service service = null;
        for (Service s : services.getServices()) {
            if (id.equals(s.getId())) {
                return s;
            }
        }
        return service;
    }
    
    public String processUploadedFiles(Service service, String uploadDirectory) {
        log.finer("service=" + service.getId() + " : uploadDirectory=" + uploadDirectory);
        String url = urlConstructor("/api/v1/provider/uploads/process?path=" + uploadDirectory, service);
        String response = getObjectFromEndpoint(String.class, url, MediaType.TEXT_PLAIN_TYPE);
        log.fine("Response of processing uploaded files from " + uploadDirectory + " : " + response);
        return response;
    }
    
    public void prepareDynamicPackages(Service service, String techWorkspaceDir, String options, String[] techs) {
        log.finer("service=" + service.getId() + " : options=" + options + " : techWorkspaceDir=" + techWorkspaceDir + " : techs=" + techs);
        String optionsParam = (options != null && !options.trim().isEmpty()) ? ("&options=" + options) : "";
        String techsParam = "&techs=" + String.join(",", techs);
        String url = urlConstructor("/api/v1/provider/packages/prepare?path=" + techWorkspaceDir + optionsParam + techsParam, service);
        Response response = getResponseFromEndpoint(url, MediaType.TEXT_PLAIN_TYPE);
        String responseString = response.readEntity(String.class);
        if (response.getStatus() == 404) {
            // The service doesn't offer this endpoint, so the exception can be ignored. 
            log.warning("Ignore expected 404 : The service doesn't offer endpoint " + url);
        } else {
            log.fine("Response of preparing dynamic packages from " + techWorkspaceDir + " : " + responseString);
        }
    }
    
    public InputStream getArtifactAsInputStream(Service service, String extension) {
        extension = extension.startsWith("/") ? extension.substring(1) : extension;
        String url = urlConstructor("/artifacts/" + extension, service);
        return getObjectFromEndpoint(InputStream.class, url, MediaType.WILDCARD_TYPE);
    }
    
    private String urlConstructor(String extension, Service service) {
        log.fine("Constructing url:" + internalServerHostPort + "+" + service.getEndpoint() + "+" + extension);
        String url = internalServerHostPort + service.getEndpoint() + extension;
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

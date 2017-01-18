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

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
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

    public ServiceConnector(String hostPort) {
        serverHostPort = hostPort;
        services = parseServicesJson();
    }
    
    public Services parseServicesJson() {
        log.info("Parsing services json file. SERVER_HOST_PORT=" + serverHostPort);
        if (serverHostPort == null) {
            
        }
        Services services = getObjectFromEndpoint(Services.class, 
                                                  "http://localhost:9082/start/api/v1/services", 
                                                  MediaType.APPLICATION_JSON_TYPE);
        log.info("Setting SERVICES object to " + services.getServices());
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
    
    public String processUploadedFiles(Service service, String uploadDirectory) {
    	log.finer("service=" + service.getId() + " : uploadDirectory=" + uploadDirectory);
        String url = urlConstructor("/api/v1/provider/uploads/process?path=" + uploadDirectory, service);
        String response = getObjectFromEndpoint(String.class, url, MediaType.TEXT_PLAIN_TYPE);
        log.fine("Response of processing uploaded files from " + uploadDirectory + " : " + response);
        return response;
    }
    
    public String prepareDynamicPackages(Service service, String techWorkspaceDir, String options, String[] techs) {
    	log.finer("service=" + service.getId() + " : options=" + options + " : techWorkspaceDir=" + techWorkspaceDir + " : techs=" + techs);
    	String optionsParam = (options != null && !options.trim().isEmpty()) ? ("&options=" + options) : "";
    	String techsParam = "&techs=" + String.join(",", techs);
    	String url = urlConstructor("/api/v1/provider/packages/prepare?path=" + techWorkspaceDir + optionsParam + techsParam, service);
    	try{
    		String response = getObjectFromEndpoint(String.class, url, MediaType.TEXT_PLAIN_TYPE);
            log.fine("Response of preparing dynamic packages from " + techWorkspaceDir + " : " + response);
            return response;
    	}catch(javax.ws.rs.NotFoundException e){
    		// The service doesn't offer this endpoint, so the exception can be ignored. 
    		log.finest("Ignore expected exception : The service doesn't offer endpoint " + url + " : " + e);
        }
        return "";
    }
    
    public String getFeaturesToInstall(Service service) {
    	log.finer("service=" + service.getId());
    	String url = urlConstructor("/api/v1/provider/features/install", service);
    	try{
    		String response = getObjectFromEndpoint(String.class, url, MediaType.TEXT_PLAIN_TYPE);
    		log.fine("Features to install for " + service.getId() + " : " + response);
    		return response;
    	}catch(javax.ws.rs.NotFoundException e){
    		// The service doesn't offer this endpoint, so the exception can be ignored. 
    		log.finest("Ignore expected exception : The service doesn't offer endpoint " + url + " : " + e);
    	}
    	return "";
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
        String url = "http://localhost:9082/" + service.getEndpoint() + extension;
        return url;
    }
    
    public < E > E getObjectFromEndpoint(Class<E> klass, String url, MediaType mediaType) {
        System.out.println("Getting object from url " + url);
//        E object = null;
//        try {
//            URL urlObject = new URL(url);
//            HttpURLConnection conn = (HttpURLConnection) urlObject.openConnection();
//            conn.setRequestMethod("GET");
//            InputStream is = (InputStream) conn.getContent();
//            ObjectInputStream objectStream = new ObjectInputStream(is);
//            object = (E) objectStream.readObject();
//            objectStream.close();
//            is.close();
//        } catch (IOException e) {
//            System.out.println("Internal error: " + e);
//        } catch (ClassNotFoundException e) {
//            System.out.println("Internal error: " + e);
//        }
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

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
package com.ibm.liberty.starter.service.watsonsdk.api.v1;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.ibm.liberty.starter.api.v1.model.provider.Dependency;
import com.ibm.liberty.starter.api.v1.model.provider.Dependency.Scope;
import com.ibm.liberty.starter.api.v1.model.provider.Location;
import com.ibm.liberty.starter.api.v1.model.provider.Provider;
import com.ibm.liberty.starter.api.v1.model.provider.ServerConfig;
import com.ibm.liberty.starter.api.v1.model.provider.Tag;

@Path("v1/provider")
public class ProviderEndpoint {

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Provider details(@Context UriInfo info) {
        Provider details = new Provider();
        String description = getStringResource("/description.html");
        details.setDescription(description);
        
        Location repoLocation = new Location();
        String url = info.getBaseUri().resolve("../artifacts").toString();
        repoLocation.setUrl(url);
        details.setRepoUrl(repoLocation);
    	
        Dependency compileDependency = new Dependency();
        compileDependency.setScope(Scope.COMPILE);
        compileDependency.setGroupId("net.wasdev.wlp.starters.watsonsdk");
        compileDependency.setArtifactId("compile-pom");
        compileDependency.setVersion("0.0.6");
     
        Dependency[] dependencies = {compileDependency};
        details.setDependencies(dependencies);
    	return details;
    }
    
    //read the description contained in the index.html file
    private String getStringResource(String path) {
    	InputStream in = getClass().getResourceAsStream(path);
    	
    	StringBuilder index = new StringBuilder();
    	char[] buffer = new char[1024];
    	int read = 0;
    	try(InputStreamReader reader = new InputStreamReader(in)){
    		while((read = reader.read(buffer)) != -1) {
    			index.append(buffer, 0, read);
    		}
    	} catch (IOException e) {
    		//just return what we've got
    		return index.toString();
    	}
    	return index.toString();
    }

    @GET
    @Path("samples")
    @Produces(MediaType.APPLICATION_JSON)
    public Response constructSample(@Context UriInfo info) {
    	StringBuilder json = new StringBuilder("{\n");
    	String base = info.getBaseUri().resolve("../sample").toString();
    	json.append("\"base\" : \"" + base + "\",\n");
    	json.append(getStringResource("/locations.json"));
    	json.append("}\n");
    	return Response.ok(json.toString()).build();
    }
}

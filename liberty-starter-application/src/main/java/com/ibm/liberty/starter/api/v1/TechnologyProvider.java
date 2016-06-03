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
package com.ibm.liberty.starter.api.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.ibm.liberty.starter.api.v1.model.provider.Attribute;
import com.ibm.liberty.starter.api.v1.model.provider.Dependency;
import com.ibm.liberty.starter.api.v1.model.provider.Dependency.Scope;
import com.ibm.liberty.starter.api.v1.model.provider.Provider;
import com.ibm.liberty.starter.api.v1.model.provider.ServerConfig;
import com.ibm.liberty.starter.api.v1.model.provider.Tag;

@Path("v1/provider")

@Api(value="Technology Provider API v1")
public class TechnologyProvider {

	//JAX-RS annotations
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    
    //Swagger annotations
    @ApiOperation(	value = "Provider description and dependencies.",
    				httpMethod = "GET",
    				notes = "A description of the technology being provided together with any dependencies that are required. Compile time dependecies"
    						+ " which are provided should be listed under the provided section - these will not be pacakged into the final artifact. "
    						+ "Runtime dependencies i.e. those which are not provided by the runtime and need to be packaed in the artifact are "
    						+ "entered under the runtime section")	
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "Plain text description")
    })
    public Provider services() {
    	Provider example = new Provider();
    	example.setDescription("This is a technology provider example. You must provide at least one dependency.");
        Dependency providedDependency = new Dependency();
        providedDependency.setScope(Scope.PROVIDED);
     
        Dependency runtimeDependency = new Dependency();
        runtimeDependency.setScope(Scope.RUNTIME);
     
        Dependency[] dependencies = {providedDependency, runtimeDependency};
        example.setDependencies(dependencies);
    	return example;
    }
   
    @GET
    @Path("config")
    @Produces(MediaType.APPLICATION_JSON)
    //Swagger annotations
    @ApiOperation(	value = "server.xml configuration entries",
    				httpMethod = "GET",
    				notes = "A JSON object which matches to server XML configuration entries." )	
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "The server configuration snippet"),
    		@ApiResponse(code = 404, message = "There is no additional server configuration required")
    })
    public ServerConfig getServerConfig() {
    	ServerConfig config = new ServerConfig();
    	Tag[] tags = new Tag[]{new Tag("featureManager"), new Tag("keyStore")};
    	tags[0].setTags(new Tag[]{new Tag("feature", "apiDiscovery-1.0")});
    	tags[1].setAttributes(new Attribute[]{new Attribute("password", "secret")});
    	config.setTags(tags);
    	return config;
    }
}

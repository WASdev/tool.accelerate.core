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
package com.ibm.liberty.starter.api.v1;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.ibm.liberty.starter.PatternValidation;
import com.ibm.liberty.starter.ServiceConnector;
import com.ibm.liberty.starter.PatternValidation.PatternType;
import com.ibm.liberty.starter.api.v1.model.registration.Service;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("v1/tech")
@Api(value = "Technology Finder API v1")
public class TechnologyEndpoint {

    // JAX-RS annotations
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    // Swagger annotations
    @ApiOperation(value = "Retrieve a list of technologies", httpMethod = "GET", notes = "Get a list of the currently registered set of technologies. This should not be cached as it may change at any time.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "The list of technologies") })
    public Response tech(@Context UriInfo info) {
        ServiceConnector serviceConnector = new ServiceConnector(info.getBaseUri());
        return Response.ok(serviceConnector.getServices().getServices(), MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("{tech}")
    @Produces(MediaType.APPLICATION_JSON)
    // Swagger annotations
    @ApiOperation(value = "Retrieve a specific technology", httpMethod = "GET", notes = "Get the details for a currently registered set of technologies. This should not be cached as it may change at any time.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "The technology details"),
            @ApiResponse(code = 404, message = "The technology could not be found") })
    public Response getTechnology(@PathParam("tech") String tech, @Context UriInfo info) {
        if (PatternValidation.checkPattern(PatternType.TECH, tech)) {
            ServiceConnector serviceConnector = new ServiceConnector(info.getBaseUri());
            Service service = serviceConnector.getServiceObjectFromId(tech);
            if (service == null) {
                return Response.status(Status.NOT_FOUND).build();
            } else {
                return Response.ok(service, MediaType.APPLICATION_JSON).build();
            }
        } else {
            return Response.status(Status.BAD_REQUEST).entity("Invalid technology type.").build();
        }
    }

}

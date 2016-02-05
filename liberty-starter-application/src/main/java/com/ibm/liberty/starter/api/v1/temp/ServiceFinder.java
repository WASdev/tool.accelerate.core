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
package com.ibm.liberty.starter.api.v1.temp;

import java.io.InputStream;
import java.net.URI;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

//Temporary class until we put services.json into Cloudant
@Path("v1/services")
public class ServiceFinder {

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getServices() throws Exception {
        JsonObject jsonData = null;
        String jsonLocation = System.getenv("com.ibm.liberty.starter.servicesJsonLocation");
        if (jsonLocation == null) {
            jsonLocation = "/services.json";
        }
        URI uri = new URI(jsonLocation);
        String protocol = uri.getScheme();
        InputStream is = null;
        if ("http".equals(protocol)) {
            try {
                is = uri.toURL().openConnection().getInputStream();
                JsonReader reader = Json.createReader(is);
                jsonData = reader.readObject();
                return Response.ok(jsonData.toString(), MediaType.APPLICATION_JSON_TYPE).build();
            } finally {
                is.close();
            }
        }
        if ("https".equals(protocol)) {
            // TODO: put in system to deal with this.
        }
        if (protocol == null || "classpath".equals(protocol)) {
            try {
                is = ServiceFinder.class.getResourceAsStream(uri.getPath());
                JsonReader reader = Json.createReader(is);
                jsonData = reader.readObject();
                return Response.ok(jsonData.toString(), MediaType.APPLICATION_JSON_TYPE).build();
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }
        return Response.ok("{ \"services\": []}", MediaType.APPLICATION_JSON_TYPE).build();
    }
}

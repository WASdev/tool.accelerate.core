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

import java.util.List;
import java.util.stream.Stream;

import com.ibm.liberty.starter.ProjectConstructor.DeployType;
import com.ibm.liberty.starter.api.v1.model.internal.Services;
import com.ibm.liberty.starter.api.v1.model.registration.Service;

public class ProjectConstructionInputData {
    public final Services services;
    public final ServiceConnector serviceConnector;
    public final String appName;
    public final ProjectConstructor.DeployType deployType;
    public final ProjectConstructor.BuildType buildType;
    public final String workspaceDirectory;
    public final String[] techOptions;
    public final String artifactId;
    public final String groupId;
    public final String generationId;
    public final boolean beta;

    public ProjectConstructionInputData(Services services, ServiceConnector serviceConnector, String appName, ProjectConstructor.DeployType deployType, ProjectConstructor.BuildType buildType, String workspaceDirectory, String[] techOptions, String artifactId, String groupId, String generationId, boolean beta) {
        this.services = services;
        this.serviceConnector = serviceConnector;
        this.appName = appName;
        this.deployType = deployType;
        this.buildType = buildType;
        this.workspaceDirectory = workspaceDirectory;
        this.techOptions = techOptions;
        this.artifactId = artifactId;
        this.groupId = groupId;
        this.generationId = generationId;
        this.beta = beta;
    }
    
    public String toBxJSON() {
        List<Service> serviceList = services.getServices();
        Stream<Service> stream = serviceList.stream();
        StringBuffer technologies = new StringBuffer("\"");
        stream.forEach((service) -> {
            technologies.append(service.getId() + ",");
        });
        String stringresult = technologies.length() == 1 ? "" : technologies.substring(0, technologies.length() - 1) + "\"";
        String json = "{\"technologies\":" + stringresult + ","
                + "\"appName\":\"" + appName + "\","
                + "\"buildType\":\"" + buildType.toString().toLowerCase() + "\","
                + "\"createType\":\"picnmix\"";
        if (deployType.equals(DeployType.BLUEMIX)) {
            json += ",\"platforms\":\"bluemix\"";
        } else {
            json += ",\"platforms\":\"\"";
        }
        if (artifactId != null) {
            json += ",\"artifactId\":\"" + artifactId + "\"";
        }
        if (groupId != null) {
            json += ",\"groupId\":\"" + groupId + "\"";
        }
        if (beta) {
            json += ",\"libertybeta\":\"true\"";
        }
        json += "}";
        return json;
    }
    
    public String toRequestQueryString(String id) {
        List<Service> serviceList = services.getServices();
        Stream<Service> stream = serviceList.stream();
        StringBuffer technologies = new StringBuffer("");
        stream.forEach((service) -> {
            technologies.append("&tech=" + service.getId());
        });
        String techOptionsString = "";
        for(String options : techOptions) {
            techOptionsString += "&techoptions=" + options;
        }
        String[] workspaceArray = workspaceDirectory.split("/");
        String workspaceId = workspaceArray[workspaceArray.length -1];
        String genId = (id == null ? generationId : id);
        return "name=" + appName + technologies + "&deploy=" + deployType.toString().toLowerCase() + "&build=" + buildType.toString() + "&workspace=" + workspaceId + techOptionsString + "&artifactId=" + artifactId + "&groupId=" + groupId + "&generationId=" + genId + "&beta=" + beta;
    }
}

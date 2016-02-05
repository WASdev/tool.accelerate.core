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
package com.ibm.liberty.starter.api.v1.model.provider;

import io.swagger.annotations.ApiModelProperty;

public class Provider {
    private String description;
    private Dependency[] dependencies;
    private Location repoUrl;

    @ApiModelProperty(value = "Description of the technology provider to be inserted into index.html", required = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ApiModelProperty(value = "Dependencies to be specified to the user when they get the download", required = true)
    public Dependency[] getDependencies() {
        return dependencies;
    }

    public void setDependencies(Dependency[] dependencies) {
        this.dependencies = dependencies;
    }

    @ApiModelProperty(value = "The location of the repository for this microservice.")
    public Location getRepoUrl() {
        return repoUrl;
    }

    public void setRepoUrl(Location repoUrl) {
        this.repoUrl = repoUrl;
    }

}

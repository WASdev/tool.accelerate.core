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
package com.ibm.liberty.starter.build.gradle;

import com.ibm.liberty.starter.DependencyHandler;

import java.util.Collections;
import java.util.Map;

public class CreateRepositoryTags {
    private final String repositoryUrl;
    public CreateRepositoryTags(DependencyHandler dependencyHandler) {
        repositoryUrl = dependencyHandler.getRepositoryUrl();
    }
    public Map<String, String> getTags() {
        return Collections.singletonMap("REPOSITORY_URL", repositoryUrl);
    }
}

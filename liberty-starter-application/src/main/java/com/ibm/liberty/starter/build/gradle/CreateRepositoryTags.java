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

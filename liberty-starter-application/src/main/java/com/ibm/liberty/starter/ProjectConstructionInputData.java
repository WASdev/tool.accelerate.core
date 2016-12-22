package com.ibm.liberty.starter;

import com.ibm.liberty.starter.api.v1.model.internal.Services;

/**
 * Created by GB031472 on 21/12/2016.
 */
public class ProjectConstructionInputData {
    public final Services services;
    public final ServiceConnector serviceConnector;
    public final String appName;
    public final ProjectConstructor.DeployType deployType;
    public final ProjectConstructor.BuildType buildType;
    public final String workspaceDirectory;

    public ProjectConstructionInputData(Services services, ServiceConnector serviceConnector, String appName, ProjectConstructor.DeployType deployType, ProjectConstructor.BuildType buildType, String workspaceDirectory) {
        this.services = services;
        this.serviceConnector = serviceConnector;
        this.appName = appName;
        this.deployType = deployType;
        this.buildType = buildType;
        this.workspaceDirectory = workspaceDirectory;
    }
}

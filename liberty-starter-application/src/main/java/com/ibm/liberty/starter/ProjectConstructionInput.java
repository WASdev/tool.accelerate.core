package com.ibm.liberty.starter;

import com.ibm.liberty.starter.api.v1.model.internal.Services;
import com.ibm.liberty.starter.api.v1.model.registration.Service;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ProjectConstructionInput {

    private static final Logger log = Logger.getLogger(ProjectConstructionInput.class.getName());
    private final ServiceConnector serviceConnector;

    public ProjectConstructionInput(ServiceConnector serviceConnector) {
        this.serviceConnector = serviceConnector;
    }

    public ProjectConstructionInputData processInput(String[] techs, String[] techOptions, String name, String deploy, String workspaceId, String build) {
        List<Service> serviceList = new ArrayList<Service>();
        for (String tech : techs) {
            if (PatternValidation.checkPattern(PatternValidation.PatternType.TECH, tech)) {
                Service service = serviceConnector.getServiceObjectFromId(tech);
                if (service != null) {
                    serviceList.add(service);
                    if (workspaceId != null && !workspaceId.trim().isEmpty()) {
                        serviceConnector.prepareDynamicPackages(service, StarterUtil.getWorkspaceDir(workspaceId) + "/" + service.getId(), getTechOptions(techOptions, tech), techs);
                    }
                }
            } else {
                log.info("Invalid tech type: " + tech);
                throw new ValidationException("Invalid technology type.");
            }
        }
        Services services = new Services();
        services.setServices(serviceList);
        if (name == null || name.length() == 0) {
            log.severe("No name passed in.");
            throw new ValidationException();
        }
        if (!PatternValidation.checkPattern(PatternValidation.PatternType.NAME, name)) {
            log.severe("Invalid file name.");
            throw new ValidationException();
        }

        if (name.length() > 50) {
            log.severe("Invalid file name length.");
            throw new ValidationException();
        }

        if (deploy == null) {
            log.severe("No deploy type specified");
            throw new ValidationException();
        }
        ProjectConstructor.DeployType deployType = ProjectConstructor.DeployType.valueOf(deploy.toUpperCase());
        ProjectConstructor.BuildType buildType;
        try {
            buildType = ProjectConstructor.BuildType.valueOf(build.toUpperCase());
        } catch (Exception e) {
            buildType = ProjectConstructor.BuildType.MAVEN;
        }
        return new ProjectConstructionInputData(services, serviceConnector, name, deployType, buildType, StarterUtil.getWorkspaceDir(workspaceId));
    }

    private String getTechOptions(String[] techOptions, String tech) {
        if(techOptions != null && tech != null && !tech.trim().isEmpty()){
            for(String option : techOptions){
                String[] s = option.split(":");
                if(s != null && s[0] != null && s[0].equals(tech)){
                    return option.substring(option.indexOf(":") + 1);
                }
            }
        }

        return "";
    }

}

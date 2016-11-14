package com.ibm.liberty.starter.pom;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.ibm.liberty.starter.DependencyHandler;
import com.ibm.liberty.starter.api.v1.model.provider.Dependency;

public class AddDependenciesCommand implements PomModifierCommand {

    private final DependencyHandler dependencyHandler;
    private static final Logger log = Logger.getLogger(AddDependenciesCommand.class.getName());
    private final Map<String, Dependency> providedPomsToAdd = new HashMap<>();
    private final Map<String, Dependency> runtimePomsToAdd = new HashMap<>();
    private final Map<String, Dependency> compilePomsToAdd = new HashMap<>();
    private final String groupIdBase = "net.wasdev.wlp.starters.";

    public AddDependenciesCommand(DependencyHandler dependencyHandler) {
        this.dependencyHandler = dependencyHandler;
    }

    @Override
    public void modifyPom(Document pom) {
        addDependency(Dependency.Scope.PROVIDED, dependencyHandler.getProvidedDependency());
        addDependency(Dependency.Scope.RUNTIME, dependencyHandler.getRuntimeDependency());
        addDependency(Dependency.Scope.COMPILE, dependencyHandler.getCompileDependency());

        Node dependenciesNode = pom.getElementsByTagName("dependencies").item(0);
        log.log(Level.INFO, "Appending dependency nodes for provided poms");
        appendDependencyNodes(dependenciesNode, providedPomsToAdd, pom);
        log.log(Level.INFO, "Appending dependency nodes for runtime poms");
        appendDependencyNodes(dependenciesNode, runtimePomsToAdd, pom);
        log.log(Level.INFO, "Appending dependency nodes for compile poms");
        appendDependencyNodes(dependenciesNode, compilePomsToAdd, pom);
    }

    private void addDependency(Dependency.Scope scope, Map<String, Dependency> mapToAdd) {
        log.log(Level.INFO, "Setting map for dependencies with scope " + scope + " to " + mapToAdd.toString());
        switch (scope) {
            case PROVIDED:
                providedPomsToAdd.putAll(mapToAdd);
                break;
            case RUNTIME:
                runtimePomsToAdd.putAll(mapToAdd);
                break;
            case COMPILE:
                compilePomsToAdd.putAll(mapToAdd);
                break;
        }
    }

    private void appendDependencyNodes(Node dependenciesNode, Map<String, Dependency> dependencies, Document pom) {
        Set<String> serviceIds = dependencies.keySet();
        log.log(Level.INFO, "Appending nodes for services " + serviceIds);
        for (String serviceId : serviceIds) {
            Dependency dependency = dependencies.get(serviceId);
            String groupId = groupIdBase + serviceId;
            appendDependencyNode(dependenciesNode, groupId, dependency, pom);
        }
    }
    
    private void appendDependencyNode(Node dependenciesNode, String dependencyGroupId, Dependency dependency, Document doc) {
        log.log(Level.INFO, "PomModifier adding dependency with groupId:" + dependencyGroupId
                           + " artifactId:" + dependency.getArtifactId() + " scope:" + dependency.getScope()
                           + " dependencyVersion" + dependency.getVersion());
        Node newDependency = doc.createElement("dependency");
        Node groupId = doc.createElement("groupId");
        groupId.setTextContent(dependencyGroupId);

        Node artifactId = doc.createElement("artifactId");
        artifactId.setTextContent(dependency.getArtifactId());

        Node version = doc.createElement("version");
        version.setTextContent(dependency.getVersion());

        Node type = doc.createElement("type");
        type.setTextContent("pom");

        Node scope = doc.createElement("scope");
        scope.setTextContent(dependency.getScope().name().toLowerCase());

        newDependency.appendChild(groupId);
        newDependency.appendChild(artifactId);
        newDependency.appendChild(version);
        newDependency.appendChild(type);
        newDependency.appendChild(scope);
        dependenciesNode.appendChild(newDependency);
    }

}

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
package com.ibm.liberty.starter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ibm.liberty.starter.ProjectZipConstructor.DeployType;
import com.ibm.liberty.starter.api.v1.model.provider.Dependency;
import com.ibm.liberty.starter.api.v1.model.provider.Dependency.Scope;

public class PomModifier {

    private static final Logger log = Logger.getLogger(PomModifier.class.getName());
    private Document doc;
    private Map<String, Dependency> providedPomsToAdd = new HashMap<>();
    private Map<String, Dependency> runtimePomsToAdd = new HashMap<>();
    private Map<String, Dependency> compilePomsToAdd = new HashMap<>();
    private String groupIdBase = "net.wasdev.wlp.starters.";
    private String appName;
    private DeployType deployType;
    private String repoUrl;

    public PomModifier(DeployType deployType) {
        this.deployType = deployType;
    }

    public void setInputStream(InputStream pomInputStream) throws SAXException, IOException, ParserConfigurationException {
        doc = DomUtil.getDocument(pomInputStream);
    }

    public void addStarterPomDependencies(DependencyHandler depHand) {
        // Actually add nodes when writing POM at end to stop duplicates, it won't stop dup's across invocations of writeToStream but that is OK at the moment
        addDependency(Dependency.Scope.PROVIDED, depHand.getProvidedDependency());
        addDependency(Dependency.Scope.RUNTIME, depHand.getRuntimeDependency());
        addDependency(Dependency.Scope.COMPILE, depHand.getCompileDependency());
        this.repoUrl = depHand.getServerHostPort() + "/start/api/v1/repo";
        appName = depHand.getAppName() != null ? depHand.getAppName() : "LibertyProject";
    }

    private void addDependency(Dependency.Scope scope, Map<String, Dependency> mapToAdd) {
        log.log(Level.INFO, "Setting map for dependencies with scope " + scope + " to " + mapToAdd.toString());
        switch (scope) {
            case PROVIDED:
                providedPomsToAdd = mapToAdd;
                break;
            case RUNTIME:
                runtimePomsToAdd = mapToAdd;
                break;
            case COMPILE:
                compilePomsToAdd = mapToAdd;
                break;
        }
    }

    public byte[] getBytes() throws TransformerException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writeToStream(baos);
        return baos.toByteArray();
    }

    private void writeToStream(OutputStream pomOutputStream) throws TransformerException, IOException {
    	Node dependenciesNode = doc.getElementsByTagName("dependencies").item(0);
        log.log(Level.INFO, "Appending dependency nodes for provided poms");
        appendDependencyNodes(dependenciesNode, providedPomsToAdd);
        log.log(Level.INFO, "Appending dependency nodes for runtime poms");
        appendDependencyNodes(dependenciesNode, runtimePomsToAdd);
        log.log(Level.INFO, "Appending dependency nodes for compile poms");
        appendDependencyNodes(dependenciesNode, compilePomsToAdd);

        NodeList propertiesNodeList = doc.getElementsByTagName("properties");
        appendAppNameProperty(propertiesNodeList);

        appendDeployType();
        appendRepoUrl();

        StarterUtil.identityTransform(new DOMSource(doc), new StreamResult(pomOutputStream), false, true, "4");
    }

    private void appendDependencyNodes(Node dependenciesNode, Map<String, Dependency> dependencies) {
        Set<String> serviceIds = dependencies.keySet();
        log.log(Level.INFO, "Appending nodes for services " + serviceIds);
        for (String serviceId : serviceIds) {
            Dependency dependency = dependencies.get(serviceId);
            String groupId = groupIdBase + serviceId;
            appendDependencyNode(dependenciesNode, groupId, dependency.getArtifactId(), dependency.getScope(), dependency.getVersion());
        }
    }

    private void appendDependencyNode(Node dependenciesNode, String dependencyGroupId, String dependencyArtifactId, Scope dependencyScope, String dependencyVersion) {
        log.log(Level.INFO, "PomModifier adding dependency with groupId:" + dependencyGroupId
                           + " artifactId:" + dependencyGroupId + " scope:" + dependencyScope
                           + " dependencyVersion" + dependencyVersion);
        Node newDependency = doc.createElement("dependency");
        Node groupId = doc.createElement("groupId");
        groupId.setTextContent(dependencyGroupId);

        Node artifactId = doc.createElement("artifactId");
        artifactId.setTextContent(dependencyArtifactId);

        Node version = doc.createElement("version");
        version.setTextContent(dependencyVersion);

        Node type = doc.createElement("type");
        type.setTextContent("pom");

        Node scope = doc.createElement("scope");
        scope.setTextContent(dependencyScope.name().toLowerCase());

        newDependency.appendChild(groupId);
        newDependency.appendChild(artifactId);
        newDependency.appendChild(version);
        newDependency.appendChild(type);
        newDependency.appendChild(scope);
        dependenciesNode.appendChild(newDependency);
    }
    
    private void appendAppNameProperty(NodeList propertiesNodeList) {
        log.log(Level.INFO, "Setting cf.host node to " + appName);
        Node propertiesNode = propertiesNodeList.item(0);
        Node appNameNode = doc.createElement("app.name");
        appNameNode.setTextContent(appName);
        propertiesNode.appendChild(appNameNode);
    }

    private void appendDeployType() {
        String profileId = null;
        switch (deployType) {
            case LOCAL:
                log.log(Level.INFO, "PomModifier adding profile activation for localServer");
                profileId = "localServer";
                break;
            case BLUEMIX:
                log.log(Level.INFO, "PomModifier adding profile activation for bluemix");
                profileId = "bluemix";
                break;
        }
        try {
            Node profileNode = getProfileNodeById(profileId);
            Node activationNode = doc.createElement("activation");
            Node activeByDefault = doc.createElement("activeByDefault");
            activeByDefault.setTextContent("true");
            activationNode.appendChild(activeByDefault);
            profileNode.appendChild(activationNode);
        } catch (UnableToFindNodeException e) {
            log.log(Level.SEVERE, "Unable to find the profile for " + deployType + " so not default activation will be set", e);
        }
    }

    private Node getProfileNodeById(String nodeId) throws UnableToFindNodeException {
        NodeList profileNodeList = doc.getElementsByTagName("profile");
        int length = profileNodeList.getLength();
        for (int i = 0; i < length; i++) {
            Node profileNode = profileNodeList.item(i);
            if (nodeHasId(profileNode, nodeId)) {
                return profileNode;
            }
        }
        throw new UnableToFindNodeException(nodeId);
    }

    private boolean nodeHasId(Node node, String id) {
        // This will ignore white space nodes
        if (node.getNodeType() == Node.ELEMENT_NODE && node.hasChildNodes()) {
            NodeList nodeList = node.getChildNodes();
            int length = nodeList.getLength();
            for (int i = 0; i < length; i++) {
                if (id.equals(nodeList.item(i).getTextContent())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private void appendRepoUrl() throws IOException {
        log.log(Level.INFO, "Append repo url of " + repoUrl);
        NodeList repoNodeList = doc.getElementsByTagName("repository");
        boolean foundRepoNode = false;
        for (int i = 0; i < repoNodeList.getLength(); i++) {
            Element repoNode = (Element) repoNodeList.item(i);
            if (nodeHasId(repoNode, "liberty-starter-maven-repo")) {
                foundRepoNode = true;
                Node urlNode = doc.createElement("url");
                urlNode.setTextContent(repoUrl);
                repoNode.appendChild(urlNode);
                break;
            }
        }
        if (!foundRepoNode) {
            throw new IOException("Repository url node not found in pom input");
        }
    }

    private static class UnableToFindNodeException extends Exception {

        private static final long serialVersionUID = -8095349390659588081L;

        public UnableToFindNodeException(String message) {
            super(message);
        }

    }

}

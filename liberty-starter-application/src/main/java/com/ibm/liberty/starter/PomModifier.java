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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
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
    private FeatureInstallHandler featureInstaller;	//Handles features to be installed during Liberty installation

    public PomModifier(DeployType deployType) {
        this.deployType = deployType;
    }
    
    public PomModifier(FeatureInstallHandler featureInstaller) {
        this.featureInstaller = featureInstaller;
    }

    public void setInputStream(InputStream pomInputStream) throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = domFactory.newDocumentBuilder();
        doc = db.parse(pomInputStream);
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
        if(deployType != null){
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
        }
        
        if(featureInstaller != null){
        	//Add the features returned by FeatureInstallHandler to the wlp config pom.xml to be installed when installing Liberty (using liberty-maven-plugin)
        	List<String> featuresToInstall = featureInstaller.getFeaturesToInstall();
        	
        	if(!featuresToInstall.isEmpty()){
        		Node assemblyInstallDirectory = doc.getElementsByTagName("assemblyInstallDirectory").item(0);
        		if(assemblyInstallDirectory != null){
        			Node configuration = assemblyInstallDirectory.getParentNode();
    	            
    	            if(!StarterUtil.hasNode(configuration, "features")){
    	            	configuration.appendChild(doc.createElement("features"));
    	            }
    	            
    	            Node features =  StarterUtil.getNode(configuration, "features");
    	
    	            if(!StarterUtil.hasNode(features, "acceptLicense")){
    	            	Node acceptLicense = doc.createElement("acceptLicense");
    	                acceptLicense.setTextContent("${accept.license}");
    	                features.appendChild(acceptLicense);
    	                
    	                //Enforce 'accept.license' property using maven-enforcer-plugin
    	                Node plugins = configuration.getParentNode().getParentNode();
    	                Node enforcerPlugin;
    	                if(!StarterUtil.hasNode(plugins, "plugin", "artifactId", "maven-enforcer-plugin")){
    	                	enforcerPlugin = doc.createElement("plugin");
    	                	
    	                	Node groupId = doc.createElement("groupId");
    	                	groupId.setTextContent("org.apache.maven.plugins");
    	                	enforcerPlugin.appendChild(groupId);
    	                	
    	                	Node artifactId = doc.createElement("artifactId");
    	                	artifactId.setTextContent("maven-enforcer-plugin");
    	                	enforcerPlugin.appendChild(artifactId);
    	                	
    	                	Node version = doc.createElement("version");
    	                	version.setTextContent("1.4.1");
    	                	enforcerPlugin.appendChild(version);
    	                	
    	                	plugins.appendChild(enforcerPlugin);
    	                }else{
    	                	Node artifactIdNode = StarterUtil.getNode(plugins, "plugin", "artifactId", "maven-enforcer-plugin");
    	                	enforcerPlugin = artifactIdNode.getParentNode();
    	                }
    	                
    	                Node executions = StarterUtil.getNode(enforcerPlugin, "executions");
    	                if(executions == null){
    	                	executions = doc.createElement("executions");
    	                	enforcerPlugin.appendChild(executions);
    	                }
    	                
    	                Node execution;
    	                if(!StarterUtil.hasNode(executions, "execution", "id", "enforce-property")){
    	                	execution = doc.createElement("execution");
    	                	executions.appendChild(execution);
    	                	
    	                	Node id = doc.createElement("id");
    	                	id.setTextContent("enforce-property");
    	                	execution.appendChild(id);
    	                }else{
    	                	Node enforceProperty = StarterUtil.getNode(executions, "execution", "id", "enforce-property");
    	                	execution = enforceProperty.getParentNode();
    	                }
    	                
    	                Node goals = StarterUtil.getNode(execution, "goals");
    	                if(goals == null){
    	                	goals = doc.createElement("goals");
    	                	execution.appendChild(goals);
    	                }
    	                
    	                Node goal = StarterUtil.getNode(goals, "goal", "enforce");
    	                if(goal == null){
    	                	goal = doc.createElement("goal");
    	                	goal.setTextContent("enforce");
    	                	goals.appendChild(goal);
    	                }
    	                
    	                Node configurationNode = StarterUtil.getNode(execution, "configuration");
    	                if(configurationNode == null){
    	                	configurationNode = doc.createElement("configuration");
    	                	execution.appendChild(configurationNode);
    	                }
    	                
    	                Node rules = StarterUtil.getNode(configurationNode, "rules");
    	                if(rules == null){
    	                	rules = doc.createElement("rules");
    	                	configurationNode.appendChild(rules);
    	                }
    	                
    	                Node requireProperty;
    	                if(!StarterUtil.hasNode(rules, "requireProperty", "property", "accept.license")){
    	                	requireProperty = doc.createElement("requireProperty");
    	                	rules.appendChild(requireProperty);
    	                	
    	                	Node property = doc.createElement("property");
    	                	property.setTextContent("accept.license");
    	                	requireProperty.appendChild(property);
    	                	
    	                	Node message = doc.createElement("message");
    	                	message.setTextContent("You must set a value for the 'accept.license' property defined in myProject-wlpcfg/pom.xml. Please review the license terms and conditions for additional features to be installed and if you accept the license terms and conditions then run the Maven command with '-Daccept.license=true'.");
    	                	requireProperty.appendChild(message);
    	                	
    	                	Node regex = doc.createElement("regex");
    	                	regex.setTextContent("true");
    	                	requireProperty.appendChild(regex);
    	                	
    	                	Node regexMessage = doc.createElement("regexMessage");
    	                	regexMessage.setTextContent("Additional features could not be installed as the license terms and conditions were not accepted. If you accept the license terms and conditions then run the Maven command with '-Daccept.license=true'.");
    	                	requireProperty.appendChild(regexMessage);
    	                }else{
    	                	Node propertyNode = StarterUtil.getNode(rules, "requireProperty", "property", "accept.license");
    	                	requireProperty = propertyNode.getParentNode();
    	                }
    	                
    	                Node fail = StarterUtil.getNode(configurationNode, "fail");
    	                if(fail == null){
    	                	fail = doc.createElement("fail");
    	                	configurationNode.appendChild(fail);
    	                }
    	                fail.setTextContent("true");
    	            }
    	
    	            for(String feature : featuresToInstall){
    	            	Node featureNode = doc.createElement("feature");
    	            	featureNode.setTextContent(feature);
    	                features.appendChild(featureNode);
    	            }
        		}
        	}
        }

        TransformerFactory transformFactory = TransformerFactory.newInstance();
        Transformer transformer = transformFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        DOMSource domSource = new DOMSource(doc);

        StreamResult streamResult = new StreamResult(pomOutputStream);
        transformer.transform(domSource, streamResult);
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

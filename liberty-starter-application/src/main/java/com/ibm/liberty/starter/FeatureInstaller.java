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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.ibm.liberty.starter.api.v1.model.internal.Services;
import com.ibm.liberty.starter.api.v1.model.registration.Service;

public class FeatureInstaller {

    private static final Logger log = Logger.getLogger(FeatureInstaller.class.getName());

    private final ServiceConnector serviceConnector;
    private List<String> listOfFeatures;

    public FeatureInstaller(Services services, ServiceConnector serviceConnector) {
        this.serviceConnector = serviceConnector;
        listOfFeatures = new ArrayList<String>();
        setServices(services);
    }

    private void setServices(Services services) {
        for (Service service : services.getServices()) {
            String features = serviceConnector.getFeaturesToInstall(service);
            if (features != null && !features.trim().isEmpty() && (features.split(",").length > 0)) {
                for (String feature : features.split(",")) {
                    if (!listOfFeatures.contains(feature)) {
                        listOfFeatures.add(feature);
                        log.finer("Added feature : " + feature);
                    }
                }
            }
        }
    }

    public byte[] addFeaturesToInstall(InputStream inputStream) throws TransformerException, IOException, ParserConfigurationException, SAXException {
        Document doc = DomUtil.getDocument(inputStream);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writeToStream(doc, baos);
        return baos.toByteArray();
    }

    private void writeToStream(Document doc, OutputStream outputStream) throws TransformerFactoryConfigurationError, TransformerException {
        if (!listOfFeatures.isEmpty()) {
            Node features;
            Node configuration = getLibertyMavenPluginConfiguration(doc);
            features = DomUtil.findOrAddChildNode(doc, configuration, "features", null);

            if (!DomUtil.hasChildNode(features, "acceptLicense")) {
                DomUtil.addChildNode(doc, features, "acceptLicense", "${accept.features.license}");
                enforceAcceptLicenseProperty(doc, configuration.getParentNode().getParentNode());
            }

            //Add the features
            for (String feature : listOfFeatures) {
                DomUtil.findOrAddChildNode(doc, features, "feature", feature);
            }
        }

        StarterUtil.identityTransform(new DOMSource(doc), new StreamResult(outputStream));
    }

    private Node getLibertyMavenPluginConfiguration(Document doc) {
        Node assemblyInstallDirectory = doc.getElementsByTagName("assemblyInstallDirectory").item(0);
        return assemblyInstallDirectory.getParentNode();
    }

    private void enforceAcceptLicenseProperty(Document doc, Node pluginsNode) {
        //Enforce 'accept.features.license' property using maven-enforcer-plugin
        Node enforcerPlugin;
        Node artifactIdNode = DomUtil.getGrandchildNode(pluginsNode, "plugin", "artifactId", "maven-enforcer-plugin");
        if (artifactIdNode == null) {
            enforcerPlugin = DomUtil.addChildNode(doc, pluginsNode, "plugin", null);
            DomUtil.addChildNode(doc, enforcerPlugin, "groupId", "org.apache.maven.plugins");
            DomUtil.addChildNode(doc, enforcerPlugin, "artifactId", "maven-enforcer-plugin");
            DomUtil.addChildNode(doc, enforcerPlugin, "version", "1.4.1");
        } else {
            enforcerPlugin = artifactIdNode.getParentNode();
        }

        Node executions = DomUtil.findOrAddChildNode(doc, enforcerPlugin, "executions", null);

        Node execution;
        Node enforceProperty = DomUtil.getGrandchildNode(executions, "execution", "id", "enforce-property");
        if (enforceProperty == null) {
            execution = DomUtil.addChildNode(doc, executions, "execution", null);
            DomUtil.addChildNode(doc, execution, "id", "enforce-property");
        } else {
            execution = enforceProperty.getParentNode();
        }

        DomUtil.findOrAddChildNode(doc, execution, "phase", "validate");

        Node goals = DomUtil.findOrAddChildNode(doc, execution, "goals", null);
        DomUtil.findOrAddChildNode(doc, goals, "goal", "enforce");

        Node configurationNode = DomUtil.findOrAddChildNode(doc, execution, "configuration", null);
        Node rules = DomUtil.findOrAddChildNode(doc, configurationNode, "rules", null);
        Node propertyNode = DomUtil.getGrandchildNode(rules, "requireProperty", "property", "accept.features.license");
        if (propertyNode == null) {
            Node requireProperty = DomUtil.addChildNode(doc, rules, "requireProperty", null);
            DomUtil.addChildNode(doc, requireProperty, "property", "accept.features.license");
            DomUtil.addChildNode(doc, requireProperty, "message",
                                 "You must set a value for the 'accept.features.license' property defined in myProject-wlpcfg/pom.xml. Please review the license terms and conditions for additional features to be installed and if you accept the license terms and conditions then run the Maven command with '-Daccept.features.license=true'.");
            DomUtil.addChildNode(doc, requireProperty, "regex", "true");
            DomUtil.addChildNode(doc, requireProperty, "regexMessage",
                                 "Additional features could not be installed as the license terms and conditions were not accepted. If you accept the license terms and conditions then run the Maven command with '-Daccept.features.license=true'.");
        }

        DomUtil.findOrAddChildNode(doc, configurationNode, "fail", "true");
    }
}

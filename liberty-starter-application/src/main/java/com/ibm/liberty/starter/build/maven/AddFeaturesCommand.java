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
package com.ibm.liberty.starter.build.maven;

import com.ibm.liberty.starter.build.FeaturesToInstallProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.IOException;
import java.util.List;

public class AddFeaturesCommand implements PomModifierCommand {

    private final List<String> listOfFeatures;
    
    public AddFeaturesCommand(FeaturesToInstallProvider featureProvider) {
        listOfFeatures = featureProvider.getFeatures();
    }

    @Override
    public void modifyPom(Document doc) throws IOException {
        if (!listOfFeatures.isEmpty()) {
            Node features;
            Node configuration = getLibertyMavenPluginConfiguration(doc);
            features = DomUtil.findOrAddChildNode(doc, configuration, "features", null);

            addInstallFeatureExecution(doc, configuration.getParentNode());

            if (!DomUtil.hasChildNode(features, "acceptLicense")) {
                DomUtil.addChildNode(doc, features, "acceptLicense", "true");
            }

            //Add the features
            for (String feature : listOfFeatures) {
                DomUtil.findOrAddChildNode(doc, features, "feature", feature);
            }
        }
    }

    private void addInstallFeatureExecution(Document doc, Node libertyPluginNode) {
        Node executions = DomUtil.findOrAddChildNode(doc, libertyPluginNode, "executions", null);
        Node execution = DomUtil.addChildNode(doc, executions, "execution", null);
        DomUtil.addChildNode(doc, execution, "id", "install-feature");
        DomUtil.addChildNode(doc, execution, "phase", "prepare-package");
        Node goals = DomUtil.addChildNode(doc, execution, "goals", null);
        DomUtil.addChildNode(doc, goals, "goal", "install-feature");
    }

    private Node getLibertyMavenPluginConfiguration(Document doc) {
        Node assemblyInstallDirectory = doc.getElementsByTagName("assemblyInstallDirectory").item(0);
        return assemblyInstallDirectory.getParentNode();
    }

}
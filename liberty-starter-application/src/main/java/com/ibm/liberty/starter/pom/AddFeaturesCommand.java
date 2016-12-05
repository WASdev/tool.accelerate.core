package com.ibm.liberty.starter.pom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.ibm.liberty.starter.ServiceConnector;
import com.ibm.liberty.starter.api.v1.model.internal.Services;
import com.ibm.liberty.starter.api.v1.model.registration.Service;

public class AddFeaturesCommand implements PomModifierCommand {

    private static final Logger log = Logger.getLogger(AddFeaturesCommand.class.getName());

    private final List<String> listOfFeatures;
    
    public AddFeaturesCommand(Services services, ServiceConnector serviceConnector) {
        listOfFeatures = new ArrayList<String>();
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

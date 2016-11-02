package com.ibm.liberty.starter.pom;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.liberty.starter.DependencyHandler;

public class AppNameCommand implements PomModifierCommand {

    private final String appName;
    private static final Logger log = Logger.getLogger(AppNameCommand.class.getName());
    
    public AppNameCommand(DependencyHandler depdendencyHandler) {
        appName = depdendencyHandler.getAppName() != null ? depdendencyHandler.getAppName() : "LibertyProject";
    }

    @Override
    public void modifyPom(Document pom) {
        log.log(Level.INFO, "Setting app name node to " + appName);
        NodeList propertiesNodeList = pom.getElementsByTagName("properties");
        Node propertiesNode = propertiesNodeList.item(0);
        Node appNameNode = pom.createElement("app.name");
        appNameNode.setTextContent(appName);
        propertiesNode.appendChild(appNameNode);
    }

}

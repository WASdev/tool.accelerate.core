package com.ibm.liberty.starter.pom;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.liberty.starter.ProjectZipConstructor.DeployType;

public class SetDefaultProfileCommand implements PomModifierCommand {

    private final DeployType deployType;
    private static final Logger log = Logger.getLogger(SetDefaultProfileCommand.class.getName());
    
    public SetDefaultProfileCommand(DeployType deployType) {
        this.deployType = deployType;
    }

    @Override
    public void modifyPom(Document pom) {
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
            Node profileNode = getProfileNodeById(profileId, pom);
            Node activationNode = pom.createElement("activation");
            Node activeByDefault = pom.createElement("activeByDefault");
            activeByDefault.setTextContent("true");
            activationNode.appendChild(activeByDefault);
            profileNode.appendChild(activationNode);
        } catch (UnableToFindNodeException e) {
            log .log(Level.SEVERE, "Unable to find the profile for " + deployType + " so not default activation will be set", e);
        }
    }

    private Node getProfileNodeById(String nodeId, Document doc) throws UnableToFindNodeException {
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
    
    private static class UnableToFindNodeException extends Exception {

        private static final long serialVersionUID = -8095349390659588081L;

        public UnableToFindNodeException(String message) {
            super(message);
        }

    }

}

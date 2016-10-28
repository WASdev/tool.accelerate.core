package com.ibm.liberty.starter.pom.unit;

import java.net.URISyntaxException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.liberty.starter.DependencyHandler;
import com.ibm.liberty.starter.DomUtil;
import com.ibm.liberty.starter.pom.AddDependenciesCommand;
import com.ibm.liberty.starter.unit.MockDependencyHandler;

public class AddDependenciesCommandTest {

    @Test
    public void addsDependenciesToPom() throws URISyntaxException, ParserConfigurationException {
        DependencyHandler depHand = MockDependencyHandler.getDefaultInstance();
        AddDependenciesCommand testObject = new AddDependenciesCommand(depHand);
        Document pom = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Node project = DomUtil.addChildNode(pom, pom, "project", null);
        DomUtil.addChildNode(pom, project, "dependencies", null);

        testObject.modifyPom(pom);

        NodeList dependencyNodes = pom.getElementsByTagName("dependency");
        assertThat(dependencyNodes.getLength(), is(2));
        boolean hasRuntimeDependency = false;
        boolean hasProvidedDependency = false;
        for (int i = 0; i < 2; i++) {
            Node dependencyNode = dependencyNodes.item(i);
            DependencyInformation dependencyInformation = new DependencyInformation(dependencyNode);
            assertThat(dependencyInformation.groupId, is("net.wasdev.wlp.starters.wibble"));
            assertThat(dependencyInformation.version, is("0.0.1"));
            assertThat(dependencyInformation.type, is("pom"));
            switch (dependencyInformation.scope) {
                case "runtime":
                    assertThat(dependencyInformation.artifactId, is("runtimeArtifactId"));
                    hasRuntimeDependency = true;
                    break;
                case "provided":
                    assertThat(dependencyInformation.artifactId, is("providedArtifactId"));
                    hasProvidedDependency = true;
                    break;
                default:
                    fail("Unexpected dependency scope: " + dependencyInformation.scope);
            }

        }
        assertThat(hasRuntimeDependency, is(true));
        assertThat(hasProvidedDependency, is(true));
    }

    private static class DependencyInformation {
        private String groupId;
        private String artifactId;
        private String version;
        private String type;
        private String scope;

        public DependencyInformation(Node dependencyNode) {
            NodeList children = dependencyNode.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                switch (child.getNodeName()) {
                    case "groupId":
                        groupId = child.getTextContent();
                        break;
                    case "artifactId":
                        artifactId = child.getTextContent();
                        break;
                    case "version":
                        version = child.getTextContent();
                        break;
                    case "type":
                        type = child.getTextContent();
                        break;
                    case "scope":
                        scope = child.getTextContent();
                        break;
                    default:
                        fail("Unexpected child node: " + child.getNodeName());
                }
            }
        }
    }
}

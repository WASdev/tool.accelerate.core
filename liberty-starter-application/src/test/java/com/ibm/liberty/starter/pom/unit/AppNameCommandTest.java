package com.ibm.liberty.starter.pom.unit;

import java.net.URISyntaxException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.ibm.liberty.starter.DependencyHandler;
import com.ibm.liberty.starter.DomUtil;
import com.ibm.liberty.starter.pom.AppNameCommand;
import com.ibm.liberty.starter.unit.MockDependencyHandler;

public class AppNameCommandTest {

    @Test
    public void appNameIsSet() throws ParserConfigurationException, URISyntaxException {
        String appName = "TestAppName";
        DependencyHandler fakeDepdendencyHandler = MockDependencyHandler.getDependencyHandlerWithName(appName);
        
        runTestOnAppName(appName, fakeDepdendencyHandler);
    }
    
    @Test
    public void appNameDefaultsToLibertyProject() throws Exception {
        String appName = "LibertyProject";
        DependencyHandler fakeDepdendencyHandler = MockDependencyHandler.getDefaultInstance();
        
        runTestOnAppName(appName, fakeDepdendencyHandler);
    }
    
    private void runTestOnAppName(String expectedName, DependencyHandler dependencyHandler) throws ParserConfigurationException {
        Document pom = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Node project = DomUtil.addChildNode(pom, pom, "project", null);
        Node properties = DomUtil.addChildNode(pom, project, "properties", null);
        AppNameCommand testObject = new AppNameCommand(dependencyHandler);
        
        testObject.modifyPom(pom);
        
        assertThat(properties.getChildNodes().getLength(), is(1));
        Node child = properties.getChildNodes().item(0);
        assertThat(child.getNodeName(), is("app.name"));
        assertThat(child.getTextContent(), is(expectedName));
    }
    
}

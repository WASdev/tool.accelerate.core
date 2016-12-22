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
package com.ibm.liberty.starter.build.maven.unit;

import com.ibm.liberty.starter.DependencyHandler;
import com.ibm.liberty.starter.build.maven.AppNameCommand;
import com.ibm.liberty.starter.build.maven.DomUtil;
import com.ibm.liberty.starter.unit.MockDependencyHandler;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.net.URISyntaxException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class AppNameCommandTest {

    @Test
    public void appNameIsSet() throws ParserConfigurationException, URISyntaxException {
        String appName = "TestAppName";
        DependencyHandler fakeDependencyHandler = MockDependencyHandler.getDependencyHandlerWithName(appName);
        
        runTestOnAppName(appName, fakeDependencyHandler);
    }
    
    @Test
    public void appNameDefaultsToLibertyProject() throws Exception {
        String appName = "LibertyProject";
        DependencyHandler fakeDependencyHandler = MockDependencyHandler.getDefaultInstance();
        
        runTestOnAppName(appName, fakeDependencyHandler);
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

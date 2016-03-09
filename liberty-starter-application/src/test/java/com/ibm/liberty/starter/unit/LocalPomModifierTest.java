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
package com.ibm.liberty.starter.unit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.ibm.liberty.starter.DependencyHandler;
import com.ibm.liberty.starter.PomModifier;
import com.ibm.liberty.starter.ProjectZipConstructor.DeployType;

public class LocalPomModifierTest {
    
    private PomModifier pomModifier;

    @Before
    public void createPomWithDependencies() throws SAXException, IOException, ParserConfigurationException {
        String pomString = "<project><dependencies/><properties><!--Testing--></properties>"
                        + "<repositories><repository><id>liberty-starter-maven-repo</id><name>liberty-starter-maven-repo</name></repository></repositories>"
                        + "<profiles><profile><id>localServer</id></profile></profiles></project>";
        try (InputStream inputStream = new ByteArrayInputStream(pomString.getBytes())) {
            pomModifier = new PomModifier(DeployType.LOCAL);
            pomModifier.setInputStream(inputStream);
        }
    }

    @Test
    public void testAddingTech() throws Exception {
        DependencyHandler depHand = MockDependencyHandler.getDefaultInstance();
        
        String outputPom = addTechAndWritePom(depHand);
        String outputPomWithWhitespaceRemoved = outputPom.replaceAll("\\s", "");
        assertTrue("OutputPom should have had a wibble dependency added" + outputPom,
                   outputPomWithWhitespaceRemoved.contains("<dependency><groupId>net.wasdev.wlp.starters.wibble</groupId><artifactId>providedArtifactId</artifactId><version>0.0.1</version><type>pom</type><scope>provided</scope></dependency>"));
        assertTrue("OutputPom should have had a wibble config dependency added" + outputPom,
                   outputPomWithWhitespaceRemoved.contains("<dependency><groupId>net.wasdev.wlp.starters.wibble</groupId><artifactId>runtimeArtifactId</artifactId><version>0.0.1</version><type>pom</type><scope>runtime</scope></dependency>"));
    }

    @Test
    public void testAddingTechWithoutConfigPom() throws Exception {
        DependencyHandler depHand = MockDependencyHandler.getProvidedInstance();
        String outputPom = addTechAndWritePom(depHand);
        String outputPomWithWhitespaceRemoved = outputPom.replaceAll("\\s", "");
        assertTrue("OutputPom should have had a wibble dependency added" + outputPom,
                   outputPomWithWhitespaceRemoved.contains("<dependency><groupId>net.wasdev.wlp.starters.wibble</groupId><artifactId>providedArtifactId</artifactId><version>0.0.1</version><type>pom</type><scope>provided</scope></dependency>"));
        assertFalse("OutputPom should not have had a wibble config dependency added" + outputPom,
                    outputPomWithWhitespaceRemoved.contains("wibble-config"));
        assertFalse("OutputPom should not have had a runtime dependency added" + outputPom,
                    outputPomWithWhitespaceRemoved.contains("<scope>runtime</scope>"));
    }

    @Test
    public void testNoDuplicateEntries() throws Exception {
        DependencyHandler depHand = MockDependencyHandler.getProvidedDuplicateInstance();
        
        String outputPom = addTechAndWritePom(depHand);
        String outputPomWithWhitespaceRemoved = outputPom.replaceAll("\\s", "");
        assertTrue("OutputPom should have had a wibble dependency added" + outputPom,
                   outputPomWithWhitespaceRemoved.contains("<dependency><groupId>net.wasdev.wlp.starters.wibble</groupId><artifactId>providedArtifactId</artifactId><version>0.0.1</version><type>pom</type><scope>provided</scope></dependency>"));
        assertTrue("Only one wibble should be added " + outputPom, outputPomWithWhitespaceRemoved.lastIndexOf("wibble") == outputPomWithWhitespaceRemoved.indexOf("wibble"));
    }
    
    @Test
    public void testAddNameToPom() throws Exception {
        DependencyHandler depHand = MockDependencyHandler.getDependencyHandlerWithName("TestName");
        
        String outputPom = addTechAndWritePom(depHand);
        String outputPomWithWhitespaceRemoved = outputPom.replaceAll("\\s", "");
        assertTrue("OutputPom should have had the app name added " + outputPom,
                   outputPomWithWhitespaceRemoved.contains("<!--Testing--><cf.host>TestName</cf.host>"));
    }
    
    @Test
    public void testActiveLocalDeploy() throws Exception {
        DependencyHandler depHand = MockDependencyHandler.getProvidedInstance();
        String outputPom = addTechAndWritePom(depHand);
        String outputPomWithWhitespaceRemoved = outputPom.replaceAll("\\s", "");
        assertTrue("OutputPom should have had the app name added " + outputPom,
                   outputPomWithWhitespaceRemoved.contains("<id>localServer</id><activation><activeByDefault>true</activeByDefault></activation>"));
    }

    private String addTechAndWritePom(DependencyHandler depHand) throws TransformerException, IOException {
        pomModifier.addStarterPomDependencies(depHand);
        byte[] bytes = pomModifier.getBytes();
        String pomContents = new String(bytes, StandardCharsets.UTF_8);
        return pomContents;
    }

}

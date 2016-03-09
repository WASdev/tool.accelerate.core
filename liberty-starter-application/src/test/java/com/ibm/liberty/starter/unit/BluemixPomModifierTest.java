package com.ibm.liberty.starter.unit;

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

public class BluemixPomModifierTest {

    private PomModifier pomModifier;

    @Before
    public void createPomWithDependencies() throws SAXException, IOException, ParserConfigurationException {
        String pomString = "<project><dependencies/><properties><!--Testing--></properties>"
                        + "<repositories><repository><id>liberty-starter-maven-repo</id><name>liberty-starter-maven-repo</name></repository></repositories>"
                        + "<profiles><profile><id>bluemix</id></profile></profiles></project>";
        try (InputStream inputStream = new ByteArrayInputStream(pomString.getBytes())) {
            pomModifier = new PomModifier(DeployType.BLUEMIX);
            pomModifier.setInputStream(inputStream);
        }
    }
    
    @Test
    public void testActiveBluemixDeploy() throws Exception {
        DependencyHandler depHand = MockDependencyHandler.getProvidedInstance();
        String outputPom = addTechAndWritePom(depHand);
        String outputPomWithWhitespaceRemoved = outputPom.replaceAll("\\s", "");
        assertTrue("OutputPom should have had the app name added " + outputPom,
                   outputPomWithWhitespaceRemoved.contains("<id>bluemix</id><activation><activeByDefault>true</activeByDefault></activation>"));
    }

    private String addTechAndWritePom(DependencyHandler depHand) throws TransformerException, IOException {
        pomModifier.addStarterPomDependencies(depHand);
        byte[] bytes = pomModifier.getBytes();
        String pomContents = new String(bytes, StandardCharsets.UTF_8);
        return pomContents;
    }
    
}

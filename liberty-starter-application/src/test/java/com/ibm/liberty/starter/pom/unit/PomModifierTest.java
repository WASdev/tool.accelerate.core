package com.ibm.liberty.starter.pom.unit;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.ibm.liberty.starter.pom.PomModifier;
import com.ibm.liberty.starter.pom.PomModifierCommand;

public class PomModifierTest {

    private InputStream simplePomInputStream;

    @Before
    public void createSimpleInputStream() {
        byte[] pomBytes = "<project></project>".getBytes();
        simplePomInputStream = new ByteArrayInputStream(pomBytes);
    }

    @Test
    public void canReadAndWritePoms() throws TransformerException, IOException, ParserConfigurationException, SAXException {
        PomModifier testObject = new PomModifier(simplePomInputStream, Collections.emptySet());

        byte[] outputBytes = testObject.getPomBytes();

        assertOutputPomIs(outputBytes, "<?xmlversion=\"1.0\"encoding=\"UTF-8\"?><project/>");
    }

    @Test
    public void canAlterPom() throws TransformerException, IOException, ParserConfigurationException, SAXException {
        PomModifierCommand fakeCommand = new PomModifierCommand() {

            @Override
            public void modifyPom(Document pom) {
                Node wibbleNode = pom.createElement("wibble");
                pom.getDocumentElement().appendChild(wibbleNode);
            }
        };
        PomModifier testObject = new PomModifier(simplePomInputStream, Collections.singleton(fakeCommand));
        
        byte[] outputBytes = testObject.getPomBytes();

        assertOutputPomIs(outputBytes, "<?xmlversion=\"1.0\"encoding=\"UTF-8\"?><project><wibble/></project>");
    }

    private void assertOutputPomIs(byte[] outputBytes, String expected) {
        String outputPom = new String(outputBytes, StandardCharsets.UTF_8);
        outputPom = outputPom.replaceAll("\\s", "");
        assertThat(outputPom, is(expected));
    }
}

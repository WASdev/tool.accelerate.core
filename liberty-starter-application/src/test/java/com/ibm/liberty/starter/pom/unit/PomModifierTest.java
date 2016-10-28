package com.ibm.liberty.starter.pom.unit;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.Test;
import org.xml.sax.SAXException;

import com.ibm.liberty.starter.pom.PomModifier;

public class PomModifierTest {

    @Test
    public void canReadAndWritePoms() throws TransformerException, IOException, ParserConfigurationException, SAXException {
        String pom = "<project></project>";
        byte[] pomBytes = pom.getBytes();
        InputStream inputStream = new ByteArrayInputStream(pomBytes);
        PomModifier testObject = new PomModifier(inputStream);

        byte[] outputBytes = testObject.getPomBytes();

        String outputPom = new String(outputBytes, StandardCharsets.UTF_8);
        outputPom = outputPom.replaceAll("\\s", "");
        assertThat(outputPom, is("<?xmlversion=\"1.0\"encoding=\"UTF-8\"?><project/>"));
    }
}

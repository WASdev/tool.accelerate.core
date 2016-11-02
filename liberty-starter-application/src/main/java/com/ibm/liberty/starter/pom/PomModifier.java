package com.ibm.liberty.starter.pom;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.ibm.liberty.starter.DomUtil;
import com.ibm.liberty.starter.StarterUtil;

public class PomModifier {

    private final Document pom;
    private Set<PomModifierCommand> commands;

    public PomModifier(InputStream inputStream, Set<PomModifierCommand> commands) throws ParserConfigurationException, SAXException, IOException {
        this.pom = DomUtil.getDocument(inputStream);
        this.commands = commands;
    }

    public byte[] getPomBytes() throws TransformerException, IOException {
        processCommands();
        return convertToBytes();
    }

    private void processCommands() throws IOException {
        for (PomModifierCommand command : commands) {
            command.modifyPom(pom);
        }
    }

    private byte[] convertToBytes() throws TransformerException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DOMSource domSource = new DOMSource(pom);
        StreamResult streamResult = new StreamResult(baos);
        StarterUtil.identityTransform(domSource, streamResult);
        return baos.toByteArray();
    }

}

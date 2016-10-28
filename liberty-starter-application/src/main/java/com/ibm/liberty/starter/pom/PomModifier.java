package com.ibm.liberty.starter.pom;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

    public PomModifier(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException {
        pom = DomUtil.getDocument(inputStream);
    }

    public byte[] getPomBytes() throws TransformerException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writeToStream(baos);
        return baos.toByteArray();
    }

    private void writeToStream(OutputStream pomOutputStream) throws TransformerException, IOException {
        DOMSource domSource = new DOMSource(pom);
        StreamResult streamResult = new StreamResult(pomOutputStream);
        StarterUtil.identityTransform(domSource, streamResult, false, true, "4");
    }

}

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

import com.ibm.liberty.starter.build.maven.PomModifier;
import com.ibm.liberty.starter.build.maven.PomModifierCommand;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import static com.ibm.liberty.starter.ByteMatcher.isByteArrayFor;
import static org.junit.Assert.assertThat;

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

        assertThat(outputBytes, isByteArrayFor("<?xmlversion=\"1.0\"encoding=\"UTF-8\"?><project/>"));
    }

    @Test
    public void canAlterPom() throws TransformerException, IOException, ParserConfigurationException, SAXException {
        PomModifierCommand fakeCommand = pom -> {
            Node wibbleNode = pom.createElement("wibble");
            pom.getDocumentElement().appendChild(wibbleNode);
        };
        PomModifier testObject = new PomModifier(simplePomInputStream, Collections.singleton(fakeCommand));

        byte[] outputBytes = testObject.getPomBytes();

        assertThat(outputBytes, isByteArrayFor("<?xmlversion=\"1.0\"encoding=\"UTF-8\"?><project><wibble/></project>"));
    }

}

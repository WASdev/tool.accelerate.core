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

import com.ibm.liberty.starter.ProjectConstructor.DeployType;
import com.ibm.liberty.starter.build.maven.DomUtil;
import com.ibm.liberty.starter.build.maven.SetDefaultProfileCommand;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class SetDefaultProfileCommandTest {

    private Document pom;
    private Node bluemixProfile;

    @Before
    public void setupTemplatePom() throws ParserConfigurationException {
        pom = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Node project = DomUtil.addChildNode(pom, pom, "project", null);
        Node profiles = DomUtil.addChildNode(pom, project, "profiles", null);
        bluemixProfile = addProfile(pom, profiles, "bluemix");
    }

    @Test
    public void settingDeployTypeToLocalDoesntBreakAnything() throws Exception {
        SetDefaultProfileCommand testObject = new SetDefaultProfileCommand(DeployType.LOCAL);

        testObject.modifyPom(pom);

        assertProfileIsNotActiveByDefault(bluemixProfile);
    }

    @Test
    public void canSetBluemixAsDefaultProfile() throws Exception {
        SetDefaultProfileCommand testObject = new SetDefaultProfileCommand(DeployType.BLUEMIX);

        testObject.modifyPom(pom);
        
        assertProfileIsActiveByDefault(bluemixProfile);
    }

    private void assertProfileIsNotActiveByDefault(Node profile) {
        assertThat(profile.getChildNodes().getLength(), is(1));
    }

    private void assertProfileIsActiveByDefault(Node profile) {
        assertThat(profile.getChildNodes().getLength(), is(2));
        Node activationNode = DomUtil.getChildNode(profile, "activation", null);
        assertThat(activationNode, notNullValue());
        Node activeByDefaultNode = DomUtil.getChildNode(activationNode, "activeByDefault", "true");
        assertThat(activeByDefaultNode, notNullValue());
    }

    private Node addProfile(Document pom, Node profiles, String name) {
        Node profile = DomUtil.addChildNode(pom, profiles, "profile", null);
        Node id = DomUtil.addChildNode(pom, profile, "id", null);
        id.setTextContent(name);
        return profile;
    }
}

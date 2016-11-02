package com.ibm.liberty.starter.pom.unit;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.ibm.liberty.starter.DomUtil;
import com.ibm.liberty.starter.pom.SetRepositoryCommand;
import com.ibm.liberty.starter.unit.MockDependencyHandler;

public class SetRepositoryCommandTest {

    @Test
    public void setsLibertyRepositoryUrl() throws Exception {
        Document pom = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Node project = DomUtil.addChildNode(pom, pom, "project", null);
        Node repositories = DomUtil.addChildNode(pom, project, "repositories", null);
        Node repository = DomUtil.addChildNode(pom, repositories, "repository", null);
        DomUtil.addChildNode(pom, repository, "id", "liberty-starter-maven-repo");
        SetRepositoryCommand testObject = new SetRepositoryCommand(MockDependencyHandler.getDefaultInstance());

        testObject.modifyPom(pom);

        Node urlNode = DomUtil.getChildNode(repository, "url", null);
        assertThat(urlNode, notNullValue());
        assertThat(urlNode.getTextContent(), is("http://mock/start/api/v1/repo"));
    }

}

package com.ibm.liberty.starter.pom.unit;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.liberty.starter.ServiceConnector;
import com.ibm.liberty.starter.api.v1.model.internal.Services;
import com.ibm.liberty.starter.api.v1.model.registration.Service;
import com.ibm.liberty.starter.pom.AddFeaturesCommand;
import com.ibm.liberty.starter.pom.DomUtil;

public class AddFeaturesCommandTest {

    @Test
    public void featuresAreAddedToPom() throws Exception {
        Document pom = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Node project = DomUtil.addChildNode(pom, pom, "project", null);
        Node build = DomUtil.addChildNode(pom, project, "build", null);
        Node plugins = DomUtil.addChildNode(pom, build, "plugins", null);
        Node plugin = DomUtil.addChildNode(pom, plugins, "plugin", null);
        Node configuration = DomUtil.addChildNode(pom, plugin, "configuration", null);
        DomUtil.addChildNode(pom, configuration, "assemblyInstallDirectory", "${wibble}");
        Services services = new Services();
        services.setServices(Collections.singletonList(new Service()));
        final String fakeFeatureName = "Wibble";
        AddFeaturesCommand testObject = new AddFeaturesCommand(services, new ServiceConnector(new URI("")) {
            @Override
            public String getFeaturesToInstall(Service service) {
                return fakeFeatureName;
            }

            @Override
            public Services parseServicesJson() {
                return services;
            }
        });

        testObject.modifyPom(pom);

        NodeList acceptLicenseNode = pom.getElementsByTagName("acceptLicense");
        assertThat(acceptLicenseNode.getLength(), is(1));
        assertThat(acceptLicenseNode.item(0).getTextContent(), is("true"));
        assertThat(DomUtil.getGrandchildNode(configuration, "features", "feature", fakeFeatureName), notNullValue());
        Node executions = DomUtil.getChildNode(plugin, "executions", null);
        assertThat(executions, notNullValue());
        Node installFeaturesIdNode = DomUtil.getGrandchildNode(executions, "execution", "id", "install-feature");
        assertThat(installFeaturesIdNode, notNullValue());
        Node goal = DomUtil.getGrandchildNode(installFeaturesIdNode.getParentNode(), "goals", "goal", "install-feature");
        assertThat(goal, notNullValue());
    }
}

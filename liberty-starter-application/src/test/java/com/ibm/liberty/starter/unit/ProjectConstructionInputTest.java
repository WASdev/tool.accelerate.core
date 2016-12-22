package com.ibm.liberty.starter.unit;

import com.ibm.liberty.starter.ProjectConstructionInput;
import com.ibm.liberty.starter.ProjectConstructionInputData;
import com.ibm.liberty.starter.ProjectConstructor;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;
import javax.naming.spi.NamingManager;
import javax.validation.ValidationException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class ProjectConstructionInputTest {

    private ProjectConstructionInput testObject;
    private MockServiceConnector serviceConnector;

    @Before
    public void createTestObject() throws URISyntaxException {
        serviceConnector = new MockServiceConnector(new URI(""), null);
        testObject = new ProjectConstructionInput(serviceConnector);
    }

    @BeforeClass
    public static void createMockInitialContext() throws NamingException {
        NamingManager.setInitialContextFactoryBuilder(new InitialContextFactoryBuilder() {
            @Override
            public InitialContextFactory createInitialContextFactory(Hashtable<?, ?> environment) throws NamingException {
                return new InitialContextFactory() {
                    @Override
                    public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
                        return new InitialContext(environment) {
                            @Override
                            public Object lookup(String name) throws NamingException {
                                if ("serverOutputDir".equals(name)) {
                                    return "foo";
                                } else {
                                    return super.lookup(name);
                                }
                            }
                        };
                    }
                };
            }
        });
    }

    @Test
    public void validInputIsParsedIntoDataObject() throws URISyntaxException, NamingException {
        String techName = "wibble";
        String techOption = "fish";
        String name = "testName";
        String workspaceId = "randomId";

        ProjectConstructionInputData result = testObject.processInput(new String[] {techName}, new String[] {techName + ":" + techOption}, name, "local", workspaceId, "gradle");

        assertThat(result.appName, is(name));
        assertThat(result.buildType, is(ProjectConstructor.BuildType.GRADLE));
        assertThat(result.deployType, is(ProjectConstructor.DeployType.LOCAL));
        assertThat(result.workspaceDirectory, containsString(workspaceId));
        assertThat(result.serviceConnector, is(serviceConnector));
        assertThat(result.services.getServices(), hasSize(1));
        assertThat(result.services.getServices().get(0).getId(), is(techName));
        assertThat(serviceConnector.capturedTechWorkspaceDir, containsString(workspaceId));
        assertThat(serviceConnector.capturedTechs, is(new String[] {techName}));
        assertThat(serviceConnector.capturedOptions, is(techOption));
    }

    @Test(expected = ValidationException.class)
    public void noNameThrowsAValidationException() throws Exception {
        testObject.processInput(new String[] {}, new String[] {}, null, "local", "wibble", "gradle");
    }

    @Test(expected = ValidationException.class)
    public void nameWithInvalidCharactersThrowsAValidationException() throws Exception {
        testObject.processInput(new String[] {}, new String[] {}, "wibble%", "local", "wibble", "gradle");
    }

    @Test(expected = ValidationException.class)
    public void longNameThrowsAValidationException() throws Exception {
        testObject.processInput(new String[] {}, new String[] {}, "ThisIsAReallyLongNameButIsItLongEnoughNotQuiteSoLetsKeepGoing", "local", "wibble", "gradle");
    }

    @Test(expected = ValidationException.class)
    public void noDeployThrowsAValidationException() throws Exception {
        testObject.processInput(new String[] {}, new String[] {}, "wibble", null, "wibble", "gradle");
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidDeployThrowsAIllegalArgumentException() throws Exception {
        testObject.processInput(new String[] {}, new String[] {}, "wibble", "invalid", "wibble", "gradle");
    }

    @Test
    public void noBuildDefaultsToMaven() throws Exception {
        ProjectConstructionInputData result = testObject.processInput(new String[]{}, new String[]{}, "wibble", "local", "wibble", null);

        assertThat(result.buildType, is(ProjectConstructor.BuildType.MAVEN));
    }

    @Test
    public void invalidBuildDefaultsToMaven() throws Exception {
        ProjectConstructionInputData result = testObject.processInput(new String[]{}, new String[]{}, "wibble", "local", "wibble", "wibble");

        assertThat(result.buildType, is(ProjectConstructor.BuildType.MAVEN));
    }
}

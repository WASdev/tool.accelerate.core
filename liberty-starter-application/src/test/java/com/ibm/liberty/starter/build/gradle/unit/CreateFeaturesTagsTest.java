package com.ibm.liberty.starter.build.gradle.unit;

import com.ibm.liberty.starter.api.v1.model.internal.Services;
import com.ibm.liberty.starter.build.FeaturesToInstallProvider;
import com.ibm.liberty.starter.build.gradle.CreateFeaturesTags;
import com.ibm.liberty.starter.build.unit.FeaturesToInstallProviderTest;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CreateFeaturesTagsTest {

    @Test
    public void featuresAreAddedToPom() throws Exception {
        CreateFeaturesTags testObject = new CreateFeaturesTags(FeaturesToInstallProviderTest.createFeaturesToInstallProviderTestObject());

        Map<String, String> tags = testObject.getTags();

        assertThat(tags.size(), is(2));
        assertThat(tags, hasEntry("LIBERTY_FEATURE_CONFIG", "    features {\n" +
                "        name = ['Wibble']\n" +
                "        acceptLicense = true\n" +
                "    }"));
        assertThat(tags, hasEntry("LIBERTY_FEATURE_TASK_SETUP", "installFeature.dependsOn 'installLiberty'\n" +
                "setupServer.dependsOn 'installFeature'"));
    }

    @Test
    public void noFeatureTagsAddedIfThereAreNoFeatures() throws Exception {
        Services services = new Services();
        services.setServices(Collections.emptyList());
        CreateFeaturesTags testObject = new CreateFeaturesTags(new FeaturesToInstallProvider(services, null));

        Map<String, String> tags = testObject.getTags();

        assertThat(tags.size(), is(0));
    }
}

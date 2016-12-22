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

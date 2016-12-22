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

import com.ibm.liberty.starter.DependencyHandler;
import com.ibm.liberty.starter.build.gradle.CreateAppNameTags;
import com.ibm.liberty.starter.unit.MockDependencyHandler;
import org.junit.Test;

import javax.xml.parsers.ParserConfigurationException;
import java.net.URISyntaxException;
import java.util.Map;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CreateAppNameTagsTest {

    @Test
    public void appNameIsSet() throws ParserConfigurationException, URISyntaxException {
        String appName = "TestAppName";
        DependencyHandler fakeDependencyHandler = MockDependencyHandler.getDependencyHandlerWithName(appName);
        CreateAppNameTags testObject = new CreateAppNameTags(fakeDependencyHandler);

        Map<String, String> tags = testObject.getTags();

        assertThat(tags.size(), is(1));
        assertThat(tags, hasEntry("APP_NAME", appName));
    }

    @Test
    public void appNameDefaultsToLibertyProject() throws Exception {
        String appName = "LibertyProject";
        DependencyHandler fakeDependencyHandler = MockDependencyHandler.getDefaultInstance();
        CreateAppNameTags testObject = new CreateAppNameTags(fakeDependencyHandler);

        Map<String, String> tags = testObject.getTags();

        assertThat(tags.size(), is(1));
        assertThat(tags, hasEntry("APP_NAME", appName));
    }

}

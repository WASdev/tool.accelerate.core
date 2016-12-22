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
import com.ibm.liberty.starter.build.gradle.CreateDependencyTags;
import com.ibm.liberty.starter.unit.MockDependencyHandler;
import org.junit.Test;

import java.net.URISyntaxException;
import java.util.Map;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class CreateDependencyTagsTest {

    @Test
    public void addsTagForRuntimeAndProvidedDependencies() throws URISyntaxException {
        DependencyHandler depHand = MockDependencyHandler.getDefaultInstance();
        CreateDependencyTags testObject = new CreateDependencyTags(depHand);

        Map<String, String> tags = testObject.getTags();

        assertThat(tags.keySet(), hasSize(1));
        assertThat(tags, hasEntry("DEPENDENCIES", "    providedCompile 'net.wasdev.wlp.starters.wibble:providedArtifactId:0.0.1'\n\n" +
                "    runtime 'net.wasdev.wlp.starters.wibble:runtimeArtifactId:0.0.1'"));
    }

    @Test
    public void addsTagWhenThereIsOnlyAProvidedDependency() throws URISyntaxException {
        DependencyHandler depHand = MockDependencyHandler.getProvidedInstance();
        CreateDependencyTags testObject = new CreateDependencyTags(depHand);

        Map<String, String> tags = testObject.getTags();

        assertThat(tags.keySet(), hasSize(1));
        assertThat(tags, hasEntry("DEPENDENCIES", "    providedCompile 'net.wasdev.wlp.starters.wibble:providedArtifactId:0.0.1'"));
    }

    @Test
    public void duplicateDependenciesAreAddedAsSingleEntry() throws URISyntaxException {
        DependencyHandler depHand = MockDependencyHandler.getProvidedDuplicateInstance();
        CreateDependencyTags testObject = new CreateDependencyTags(depHand);
        System.out.println(depHand.getProvidedDependency().size());

        Map<String, String> tags = testObject.getTags();

        assertThat(tags.keySet(), hasSize(1));
        assertThat(tags, hasEntry("DEPENDENCIES", "    providedCompile 'net.wasdev.wlp.starters.wibble:providedArtifactId:0.0.1'"));
    }

    @Test
    public void addsTagForCompileDependency() throws URISyntaxException {
        DependencyHandler depHand = MockDependencyHandler.getCompileInstance();
        CreateDependencyTags testObject = new CreateDependencyTags(depHand);

        Map<String, String> tags = testObject.getTags();

        assertThat(tags.keySet(), hasSize(1));
        assertThat(tags, hasEntry("DEPENDENCIES", "    compile 'net.wasdev.wlp.starters.wibble:compileArtifactId:0.0.1'"));
    }
}

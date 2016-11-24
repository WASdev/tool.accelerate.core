package com.ibm.liberty.starter.gradle.unit;

import com.ibm.liberty.starter.DependencyHandler;
import com.ibm.liberty.starter.gradle.CreateDependencyTags;
import com.ibm.liberty.starter.unit.MockDependencyHandler;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import java.net.URISyntaxException;
import java.util.Map;

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

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
package com.ibm.liberty.starter.unit;

import com.ibm.liberty.starter.GitHubConnector;
import com.ibm.liberty.starter.GitHubWriter;
import com.ibm.liberty.starter.ProjectConstructor;
import org.apache.commons.io.FileUtils;
import org.junit.ClassRule;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static com.ibm.liberty.starter.matchers.FileContainsLines.containsLinesInRelativeOrder;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.io.FileMatchers.anExistingDirectory;
import static org.hamcrest.io.FileMatchers.anExistingFile;
import static org.junit.Assert.assertThat;

public class GitHubWriterTest {

    @ClassRule
    public static SetupInitialConext setupInitialConext = new SetupInitialConext();

    @Test
    public void createsProjectOnGitHubWithFileContents() throws Exception {
        Map<String, byte[]> files = new HashMap<>();
        String rootFilePath = "rootFile.txt";
        byte[] rootFileBytes = "wibble".getBytes();
        files.put(rootFilePath, rootFileBytes);
        String nestedFilePath = "file/in/dir.txt";
        byte[] nestedFileBytes = "fish".getBytes();
        files.put(nestedFilePath, nestedFileBytes);
        String repositoryName = "wibble";
        FakeGitHubConnector fakeConnector = new FakeGitHubConnector();
        String baseUri = "http://wibble.fish";
        GitHubWriter testObject = new GitHubWriter(files, repositoryName, ProjectConstructor.BuildType.GRADLE, new URI(baseUri), fakeConnector);

        testObject.createProjectOnGitHub();
        File localGitRepo = fakeConnector.localGitRepo;

        assertThat(localGitRepo, is(anExistingDirectory()));
        assertFileExistsWithContent(rootFilePath, rootFileBytes, localGitRepo);
        assertFileExistsWithContent(nestedFilePath, nestedFileBytes, localGitRepo);
        File readmeFile = new File(localGitRepo, "README.md");
        assertThat(readmeFile, is(anExistingFile()));
        assertThat(readmeFile, containsLinesInRelativeOrder(containsString(repositoryName),
                containsString(baseUri),
                containsString(ProjectConstructor.BuildType.GRADLE.runInstruction)));
    }
    
    @Test
    public void emptyEntriesAreIgnored() throws Exception {
        Map<String, byte[]> files = new HashMap<>();
        String filePath = "emptyFile";
        files.put(filePath, new byte[]{});

        String repositoryName = "wibble";
        FakeGitHubConnector fakeConnector = new FakeGitHubConnector();
        GitHubWriter testObject = new GitHubWriter(files, repositoryName, ProjectConstructor.BuildType.GRADLE, new URI(""), fakeConnector);

        testObject.createProjectOnGitHub();
        File localGitRepo = fakeConnector.localGitRepo;

        assertThat(localGitRepo, is(anExistingDirectory()));
        assertThat(new File(localGitRepo, filePath), is(not(anExistingFile())));
        assertThat(new File(localGitRepo, filePath), is(not(anExistingDirectory())));
    }

    private void assertFileExistsWithContent(String pathToFile, byte[] expectedContents, File localGitRepo) throws IOException {
        File rootFile = new File(localGitRepo, pathToFile);
        assertThat(rootFile, is(anExistingFile()));
        assertThat(Files.readAllBytes(rootFile.toPath()), is(expectedContents));
    }


    private class FakeGitHubConnector extends GitHubConnector {

        boolean calledPush = false;
        boolean calledDelete = false;
        File localGitRepo;

        public FakeGitHubConnector() throws IOException {
            super(null);
        }

        @Override
        public File createGitRepository(String repositoryName) throws IOException {
            String tempDirPath = System.getProperty("liberty.temp.dir", System.getProperty("java.io.tmpdir"));
            File gitDirectory = new File(tempDirPath + "/gitHub/" + repositoryName);
            FileUtils.deleteDirectory(gitDirectory);
            gitDirectory.mkdirs();
            localGitRepo = gitDirectory;
            return gitDirectory;
        }

        @Override
        public void pushAllChangesToGit() throws IOException {
            // No-op
            calledPush = true;
        }

        @Override
        public void deleteLocalRepository() throws IOException {
            // No-op so we can see what we have
            calledDelete = true;
        }
    }
}

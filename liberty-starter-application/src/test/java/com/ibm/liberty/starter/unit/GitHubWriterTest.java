package com.ibm.liberty.starter.unit;

import com.ibm.liberty.starter.GitHubConnector;
import com.ibm.liberty.starter.GitHubWriter;
import org.apache.commons.io.FileUtils;
import org.junit.ClassRule;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
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
        GitHubWriter testObject = new GitHubWriter(files, repositoryName, fakeConnector);

        testObject.createProjectOnGitHub();
        File localGitRepo = fakeConnector.localGitRepo;

        assertThat(localGitRepo, is(anExistingDirectory()));
        assertFileExistsWithContent(rootFilePath, rootFileBytes, localGitRepo);
        assertFileExistsWithContent(nestedFilePath, nestedFileBytes, localGitRepo);
    }
    
    @Test
    public void emptyEntriesAreIgnored() throws Exception {
        Map<String, byte[]> files = new HashMap<>();
        String filePath = "emptyFile";
        files.put(filePath, new byte[]{});

        String repositoryName = "wibble";
        FakeGitHubConnector fakeConnector = new FakeGitHubConnector();
        GitHubWriter testObject = new GitHubWriter(files, repositoryName, fakeConnector);

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

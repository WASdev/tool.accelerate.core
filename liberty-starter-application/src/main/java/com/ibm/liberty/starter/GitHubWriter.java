package com.ibm.liberty.starter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GitHubWriter {

    private static final Logger log = Logger.getLogger(GitHubWriter.class.getName());
    private final Map<String, byte[]> fileMap;
    private final GitHubConnector gitHubConnector;
    private final String projectName;

    public GitHubWriter(Map<String, byte[]> fileMap, String projectName, GitHubConnector gitHubConnector) {
        this.fileMap = fileMap;
        this.gitHubConnector = gitHubConnector;
        this.projectName = projectName;
    }

    public void createProjectOnGitHub() throws IOException {
        File localRepository = gitHubConnector.createGitRepository(projectName);
        writeFilesToLocalRepository(localRepository);
        gitHubConnector.pushAllChangesToGit();
        gitHubConnector.deleteLocalRepository();
    }

    private void writeFilesToLocalRepository(File localRepository) throws IOException {
        log.log(Level.INFO, "Entering method GitHubWriter.writeFilesToLocalRepository()");
        for (Map.Entry<String, byte[]> fileEntry : fileMap.entrySet()) {
            File fileToWrite = new File(localRepository, fileEntry.getKey());
            fileToWrite.getParentFile().mkdirs();
            Files.write(fileToWrite.toPath(), fileEntry.getValue());
        }
    }
}

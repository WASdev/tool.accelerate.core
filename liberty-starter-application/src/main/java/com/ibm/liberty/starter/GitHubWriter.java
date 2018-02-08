/*******************************************************************************
 * Copyright (c) 2016,2017 IBM Corp.
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
            byte[] bytes = fileEntry.getValue();
            if (bytes.length > 0) {
                File fileToWrite = new File(localRepository, fileEntry.getKey());
                fileToWrite.getParentFile().mkdirs();
                Files.write(fileToWrite.toPath(), bytes);
            }
        }
    }
}

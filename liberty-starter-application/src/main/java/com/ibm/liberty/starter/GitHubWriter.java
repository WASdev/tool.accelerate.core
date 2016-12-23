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
package com.ibm.liberty.starter;

import com.ibm.liberty.starter.build.gradle.TemplatedFileToBytesConverter;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GitHubWriter {

    private static final Logger log = Logger.getLogger(GitHubWriter.class.getName());
    private final Map<String, byte[]> fileMap;
    private final GitHubConnector gitHubConnector;
    private final String projectName;
    private final ProjectConstructor.BuildType buildType;
    private final URI baseUri;

    public GitHubWriter(Map<String, byte[]> fileMap, String projectName, ProjectConstructor.BuildType buildType, URI baseUri, GitHubConnector gitHubConnector) {
        this.fileMap = fileMap;
        this.buildType = buildType;
        this.baseUri = baseUri;
        this.gitHubConnector = gitHubConnector;
        this.projectName = projectName;
    }

    public void createProjectOnGitHub() throws IOException {
        File localRepository = gitHubConnector.createGitRepository(projectName);
        addReadme();
        writeFilesToLocalRepository(localRepository);
        gitHubConnector.pushAllChangesToGit();
        gitHubConnector.deleteLocalRepository();
    }

    private void addReadme() throws IOException {
        Map<String, String> buildTags = new HashMap<>();
        buildTags.put("NAME", projectName);
        buildTags.put("GENERATED_FROM", baseUri.toString());
        buildTags.put("RUN_INSTRUCTION", buildType.runInstruction);
        TemplatedFileToBytesConverter fileConverter = new TemplatedFileToBytesConverter(this.getClass().getClassLoader().getResourceAsStream("GitHubReadme.md"), buildTags);
        fileMap.put("README.md", fileConverter.getBytes());
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

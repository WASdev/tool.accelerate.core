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

import org.apache.commons.io.FileUtils;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.github.core.service.UserService;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.TransportCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;

/**
 * This class connects to GitHub to create repositories (using eGit) and push changes (using jGit)
 */
public class GitHubConnector {

    private final String oAuthToken;
    private final File localGitDirectory;
    private Git localRepository;
    private String repositoryLocation;

    public GitHubConnector(String oAuthToken) throws IOException {
        this.oAuthToken = oAuthToken;
        this.localGitDirectory = new File(StarterUtil.getWorkspaceDir(StarterUtil.createCleanWorkspace()) + "/gitHubProject");
    }

    /**
     * Creates a repository on GitHub and in a local temporary directory. This is a one time operation as
     * once it is created on GitHub and locally it cannot be recreated.
     */
    public File createGitRepository(String repositoryName) throws IOException {
        RepositoryService service = new RepositoryService();
        service.getClient().setOAuth2Token(oAuthToken);
        Repository repository = new Repository();
        repository.setName(repositoryName);
        repository = service.createRepository(repository);
        repositoryLocation = repository.getHtmlUrl();

        CloneCommand cloneCommand = Git.cloneRepository()
                .setURI(repository.getCloneUrl())
                .setDirectory(localGitDirectory);
        addAuth(cloneCommand);
        try {
            localRepository = cloneCommand.call();
        } catch (GitAPIException e) {
            throw new IOException("Error cloning to local file system", e);
        }
        return localGitDirectory;
    }

    /**
     * Must be called after #{createGitRepository()}
     */
    public void pushAllChangesToGit() throws IOException {
        if (localRepository == null) {
            throw new IOException("Git has not been created, call createGitRepositoryFirst");
        }
        try {
            UserService userService = new UserService();
            userService.getClient().setOAuth2Token(oAuthToken);
            User user = userService.getUser();
            String name = user.getLogin();
            String email = user.getEmail();
            if (email == null) {
                email = "";
            }

            localRepository.add().addFilepattern(".").call();
            localRepository.commit()
                    .setMessage("Initial commit")
                    .setCommitter(name, email)
                    .call();
            PushCommand pushCommand = localRepository.push();
            addAuth(pushCommand);
            pushCommand.call();
        } catch (GitAPIException e) {
            throw new IOException("Error pushing changes to GitHub", e);
        }
    }

    private void addAuth(TransportCommand command) {
        command.setCredentialsProvider(new UsernamePasswordCredentialsProvider(oAuthToken, ""));
    }

    public void deleteLocalRepository() throws IOException {
        localRepository.close();
        FileUtils.deleteDirectory(localGitDirectory);
    }

    public String getRepositoryLocation() {
        return repositoryLocation;
    }
}

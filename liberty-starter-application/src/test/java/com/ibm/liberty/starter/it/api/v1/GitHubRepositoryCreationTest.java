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
package com.ibm.liberty.starter.it.api.v1;

import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeTrue;

/**
 * This class will test the endpoint for creating a project on GitHub. It will only run if you supply it your GitHub
 * OAuth token with the -DgitHubOauth=&lt;TOKEN&gt; flag when running this test. You can create a token by going
 * <a href="https://github.com/settings/tokens">here</a>. The token has to have the scope <code>public_repo</code>. You
 * can't already have a repository in GitHub called <code>TestAppAcceleratorProject</code>.
 */
public class GitHubRepositoryCreationTest {

    @Test
    public void testGitHubRepositoryCreation() throws Exception {
        String oAuthToken = System.getProperty("gitHubOauth");
        assumeTrue(oAuthToken != null && !oAuthToken.isEmpty());
        Client client = ClientBuilder.newClient();
        String name = "TestAppAcceleratorProject";
        String port = System.getProperty("liberty.test.port");
        String url = "http://localhost:" + port + "/start/api/v1/createGitHubRepository?tech=test&name=" + name + "&deploy=local&oAuthToken=" + oAuthToken;
        System.out.println("Testing " + url);

        Response response = client.target(url).request().get();

        try {
            assertThat(response.getStatus(), is(HttpURLConnection.HTTP_SEE_OTHER));
            String locationHeader = response.getHeaderString("Location");
            assertThat(locationHeader, is(not(nullValue())));
            assertThat(locationHeader, containsString("github.com"));
            assertThat(locationHeader, containsString(name));
        } finally {
            response.close();
        }
    }

}
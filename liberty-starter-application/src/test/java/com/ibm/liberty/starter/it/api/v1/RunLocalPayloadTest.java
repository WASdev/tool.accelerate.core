package com.ibm.liberty.starter.it.api.v1;

import static com.ibm.liberty.starter.it.api.v1.matchers.FileContainsLines.containsLinesInRelativeOrder;
import static com.ibm.liberty.starter.it.api.v1.matchers.Retry.eventually;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.io.FileMatchers.anExistingFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

import com.ibm.liberty.starter.it.api.v1.utils.DownloadedZip;
import com.ibm.liberty.starter.it.api.v1.utils.MvnUtils;

public class RunLocalPayloadTest {

    private final static String tempDir = System.getProperty("liberty.temp.dir") + "/localPayloadTest";
    private final static String installLog = tempDir + "/mvnLog/log.txt";
    private final static File stopServerLog = new File(tempDir + "/mvnLog/stopServer.txt");

    @Rule
    public DownloadedZip zip = new DownloadedZip(tempDir);

    @Test
    public void testLocalMvnInstallRuns() throws Exception {
        File logFile = new File(installLog);
        String pathToOutputZip = zip.getLocation() + "/myProject-wlpcfg/target/TestApp.zip";
        
        runMvnInstallOnSeperateThread(logFile);
        
        Matcher<File> logContainsServerStartedForLocalServer = containsLinesInRelativeOrder(containsString("Building myArtifactId-localServer"), containsString("CWWKF0011I"));
        Matcher<File> logContainsBuildFailure = containsLinesInRelativeOrder(containsString("BUILD FAILURE"));
        assertThat(logFile, eventually(logContainsServerStartedForLocalServer).butNot(logContainsBuildFailure));
        assertThat(new File(pathToOutputZip), is(anExistingFile()));
        testEndpoint();
    }

    @After
    public void stopServer() throws FileNotFoundException {
        PrintStream outputStream = MvnUtils.printStreamForFile(stopServerLog);
        
        int mvnReturnCode = MvnUtils.runMvnCommand(outputStream, tempDir, zip, "liberty:stop-server", "-pl", "myProject-deploy/myProject-localServer");
        
        assertEquals(0, mvnReturnCode);
        assertThat(stopServerLog, containsLinesInRelativeOrder(containsString("BUILD SUCCESS")));
    }

    private void runMvnInstallOnSeperateThread(File logFile) throws FileNotFoundException {
        PrintStream outputStream = MvnUtils.printStreamForFile(logFile);
        System.out.println("mvn output will go to " + logFile.getAbsolutePath());
        
        Thread threadExecutingInstall = new Thread(() -> {
            MvnUtils.runMvnCommand(outputStream, tempDir, zip, "install");
        });
        
        threadExecutingInstall.setDaemon(true);
        threadExecutingInstall.start();
    }
    
    public void testEndpoint() {
        Client client = ClientBuilder.newClient();
        String url = "http://localhost:9080/myLibertyApp/";
        System.out.println("Testing " + url);
        WebTarget target = client.target(url);
        Builder builder = target.request();
        Response response = builder.get();
        int status = response.getStatus();
        assertEquals("Endpoint response status was not 200, found:" + status, status, 200);
        String responseString = response.readEntity(String.class);
        assertThat(responseString, containsString("Welcome to your Liberty Application"));
        assertThat(responseString, containsString("Test"));
    }

}

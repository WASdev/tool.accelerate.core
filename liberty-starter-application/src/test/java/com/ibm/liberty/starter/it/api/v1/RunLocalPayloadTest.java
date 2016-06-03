package com.ibm.liberty.starter.it.api.v1;

import static com.ibm.liberty.starter.it.api.v1.matchers.FileContainsLines.containsLinesInRelativeOrder;
import static com.ibm.liberty.starter.it.api.v1.matchers.Retry.eventually;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.apache.maven.cli.MavenCli;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RunLocalPayloadTest {

    private final static String tempDir = System.getProperty("liberty.temp.dir");
    private final static String mvnMultiModuleProjectDirectory = tempDir + "/mvn/multi_module";
    private final static File extractedZip = new File(tempDir + "/extractedZip");
    private final static String installLog = tempDir + "/mvnLog/log.txt";
    private final static File stopServerLog = new File(tempDir + "/mvnLog/stopServer.txt");

    @Before
    public void downloadZip() throws IOException {
        Client client = ClientBuilder.newClient();
        String port = System.getProperty("liberty.test.port");
        String url = "http://localhost:" + port + "/start/api/v1/data?tech=test&name=TestApp&deploy=local";
        System.out.println("Testing " + url);
        Response response = client.target(url).request("application/zip").get();
        InputStream entityInputStream = response.readEntity(InputStream.class);
        extractZip(entityInputStream);
    }

    @Test
    public void testLocalMvnInstallRuns() throws Exception {
        testMvnCommand(installLog);
        testEndpoint();
    }

    @After
    public void stopServer() throws FileNotFoundException {
        PrintStream outputStream = printStreamToFile(stopServerLog);
        
        int mvnReturnCode = runMvnCommand(outputStream, "liberty:stop-server", "-pl", "myProject-deploy/myProject-localServer");
        
        assertEquals(0, mvnReturnCode);
        assertThat(stopServerLog, containsLinesInRelativeOrder(containsString("BUILD SUCCESS")));
    }

    private void testMvnCommand(String logFileString) throws IOException, InterruptedException {
        File logFile = new File(logFileString);
        
        runMvnInstallOnSeperateThread(logFile);
        
        assertThat(logFile, eventually(containsLinesInRelativeOrder(containsString("Building myArtifactId-localServer"), containsString("CWWKF0011I"))));
    }
    
    private void runMvnInstallOnSeperateThread(File logFile) throws FileNotFoundException {
        PrintStream outputStream = printStreamToFile(logFile);
        System.out.println("mvn output will go to " + logFile.getAbsolutePath());
        
        Thread threadExecutingInstall = new Thread(() -> {
            runMvnCommand(outputStream, "install");
        });
        
        threadExecutingInstall.setDaemon(true);
        threadExecutingInstall.start();
    }
    
    private int runMvnCommand(PrintStream outputStream, String... args) {
        System.setProperty(MavenCli.MULTIMODULE_PROJECT_DIRECTORY, mvnMultiModuleProjectDirectory);
        MavenCli cli = new MavenCli();
        return cli.doMain(args, extractedZip.getAbsolutePath(), outputStream, outputStream);
    }

    private PrintStream printStreamToFile(File file) throws FileNotFoundException {
        file.getParentFile().mkdirs();
        PrintStream outputStream = new PrintStream(new FileOutputStream(file));
        return outputStream;
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
        String[] expectedStrings = { "Welcome to your Liberty Application", "Test" };
        assertTrue("Endpoint response incorrect, expected it to include:" + expectedStrings[0] + ", found:" + responseString, responseString.contains(expectedStrings[0]));
        assertTrue("Endpoint response incorrect, expected it to include:" + expectedStrings[1] + ", found:" + responseString, responseString.contains(expectedStrings[1]));
    }

    private static void extractZip(InputStream entityInputStream) throws IOException {
        // Create a new ZipInputStream from the response InputStream
        ZipInputStream zipIn = new ZipInputStream(entityInputStream);
        ZipEntry inputEntry = null;
        while ((inputEntry = zipIn.getNextEntry()) != null) {
            if (inputEntry.isDirectory()) {
                continue;
            }
            String entryName = inputEntry.getName();
            System.out.println("Creating " + entryName);
            File zipFile = new File(extractedZip, entryName);
            zipFile.getParentFile().mkdirs();
            FileOutputStream fos = new FileOutputStream(zipFile);
            byte[] bytes = new byte[4096];
            int bytesRead = 0;
            while ((bytesRead = zipIn.read(bytes)) >= 0) {
                fos.write(bytes, 0, bytesRead);
            } ;
            fos.close();
        }
    }
}

package com.ibm.liberty.starter.it.api.v1;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class RunLocalPayloadTest {

    private final static String tempDir = System.getProperty("liberty.temp.dir");
    private static String osName;
    private final String extractedZip = "/extractedZip";
    private final static String installLog = tempDir + "/mvnLog/log.txt";
    private final static String cleanLog = tempDir + "/mvnLog/cleanLog.txt";

    @BeforeClass
    // Check that maven is on the classpath
    public static void assertMavenPathSet() throws IOException {
        System.out.println("RunLocalPayloadTest.assertMavenPathSet @BeforeClass entered");
        osName = System.getProperty("os.name");
        assertNotNull(osName);
        File file = new File(tempDir);
        file.mkdir();
        System.out.println("RunLocalPayloadTest.assertMavenPathSet System path is:" + System.getenv("PATH"));
        ProcessBuilder pb = null;
        if (osName.startsWith("Windows")) {
            pb = new ProcessBuilder("cmd", "/c", "mvn", "--version");
        } else {
            System.out.println("RunLocalPayloadTest.assertMavenPathSet os is not windows.");
            pb = new ProcessBuilder("mvn", "--version");
        }
        pb.directory(file);
        Process process = pb.start();
        InputStream is = process.getInputStream();
        InputStream errorStream = process.getErrorStream();
        String output = inputStreamToString(is);
        String error = inputStreamToString(errorStream);
        is.close();
        errorStream.close();
        System.out.println("Output from env is:" + output);
        assertTrue("Std Out:" + output + "/nStd Error:" + error, output.contains("Apache Maven"));
    }

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
        List<String> installCmd = Arrays.asList("install");
        testMvnCommand(installCmd, installLog);
        testEndpoint();
    }
    
    @After
    public void shutdownServer() throws IOException, InterruptedException {
        List<String> cleanCmd = Arrays.asList("clean", "-P", "stopServer");
        testMvnCommand(cleanCmd, cleanLog);
    }
    
    private void testMvnCommand(List<String> args, String logFileString) throws IOException, InterruptedException {
        List<String> logFileList = Arrays.asList("--log-file", logFileString);
        String filePath = tempDir + extractedZip;
        File logFile = new File(logFileString);
        logFile.getParentFile().mkdirs();
        logFile.createNewFile();
        System.out.println("mvn output will go to " + logFileString);
        File file = new File(filePath);
        List<String> cmd;
        if (osName.startsWith("Windows")) {
            cmd = Arrays.asList("cmd", "/c", "mvn");
        } else {
            cmd = Arrays.asList("mvn");
        }
        List<String> processArgs = new ArrayList<String>();
        processArgs.addAll(cmd);
        processArgs.addAll(args);
        processArgs.addAll(logFileList);
        ProcessBuilder pb = new ProcessBuilder(processArgs);
        pb.directory(file);
        Process process = pb.start();
        process.waitFor();
        int exitValue = process.exitValue();
        System.out.println("Exit value is " + exitValue);
        assertEquals("Found incorrect exit value running command:" + processArgs, exitValue, 0);
        FileInputStream fis = new FileInputStream(logFile);
        String logs = inputStreamToString(fis);
        assertTrue("Expected message BUILD SUCCESS in logs located in " + logFile.getAbsolutePath(), logs.contains("BUILD SUCCESS"));
    }
    
    public void testEndpoint() {
        Client client = ClientBuilder.newClient();
        String url = "http://localhost:9080/myLibertyApp/";
        System.out.println("Testing " + url);
        Response response = client.target(url).request().get();
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
        String tempDir = System.getProperty("liberty.temp.dir");
        File file = new File(tempDir + "/TestApp.zip");
        System.out.println("Creating zip file: " + file.toString());
        File extractedZip = new File(tempDir + "/extractedZip");
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
            };
            fos.close();
        }
    }

    private static String inputStreamToString(InputStream inputStream) throws IOException {
        InputStreamReader isr = new InputStreamReader(inputStream);
        char[] chars = new char[1024];
        StringBuilder responseBuilder = new StringBuilder();

        int read;
        while ((read = isr.read(chars)) != -1) {
            responseBuilder.append(chars, 0, read);
        }
        return responseBuilder.toString();
    }

}

package com.ibm.liberty.starter.it.api.v1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class RunLocalPayloadTest {

//    @BeforeClass
//     check that maven is on the classpath
//     public static void assertMavenPathSet() {
//      System.getenv(")
//    }

    @Ignore
    @Test
    public void testLocalMvnInstall() throws Exception {
        String endpoint = "tech=test&name=TestApp&deploy=local";
        Response response = callDataEndpoint(endpoint);
        // extract zip using jar
        InputStream entityInputStream = response.readEntity(InputStream.class);
        extractZip(entityInputStream);
        runMvnInstall();
    }
    
    public static void main(String[] args) throws IOException, InterruptedException {
//        FileInputStream entityInputStream = new FileInputStream(new File("C:\\Users\\IBM_ADMIN\\git\\tool.leap.core\\bin\\StarterServer.zip"));
//        extractZip(entityInputStream);
        System.out.println("main started");
        System.setProperty("liberty.temp.dir", "C:\\Users\\IBM_ADMIN\\git\\tool.leap.core\\liberty-starter-application\\build\\temp");
        runMvnInstall();
    }

    private Response callDataEndpoint(String queryString) throws Exception {
        Client client = ClientBuilder.newClient();
        String port = System.getProperty("liberty.test.port");
        String url = "http://localhost:" + port + "/start/api/v1/data?" + queryString;
        System.out.println("Testing " + url);
        Response response = client.target(url).request("application/zip").get();
        return response;
    }

    private static void extractZip(InputStream entityInputStream) throws IOException {
        // Create a new ZipInputStream from the response InputStream
        ZipInputStream zipIn = new ZipInputStream(entityInputStream);
        String tempDir = System.getProperty("liberty.temp.dir");
//        String tempDir = "C:\\Users\\IBM_ADMIN\\git\\tool.leap.core\\liberty-starter-application\\build\\temp";
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

    private static void runMvnInstall() throws IOException, InterruptedException {
        String filePath = System.getProperty("liberty.temp.dir") + "/extractedZip";
//        File logFile = new File(System.getProperty("liberty.temp.dir") + "/mvnLog/log.txt");
//        logFile.getParentFile().mkdirs();
//        logFile.createNewFile();
        String outputFile = System.getProperty("liberty.temp.dir") + "/mvnLog/log.txt";
        System.out.println("mvn output will go to " + outputFile);
        File file = new File(filePath);
        Process process = Runtime.getRuntime().exec("cmd /c mvn install --log-file " + outputFile, null, file);
        process.waitFor();
        System.out.println("Exit value is " + process.exitValue());
        InputStream is = process.getInputStream();
        byte[] bytes = new byte[4096];
        int bytesRead = 0;
        while ((bytesRead = is.read(bytes)) >= 0) {
            System.out.write(bytes, 0, bytesRead);
        };
    }

}

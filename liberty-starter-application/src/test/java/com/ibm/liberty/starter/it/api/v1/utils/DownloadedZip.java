package com.ibm.liberty.starter.it.api.v1.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.junit.Rule;
import org.junit.rules.ExternalResource;

/**
 * A JUnit {@link Rule} that downloads the payload from the URL <code>http://localhost:${liberty.test.port}/start/api/v1/data?tech=test&name=TestApp&deploy=local</code>. It then
 * extracts it into the location supplied by {@link #getLocation()}.
 */
public class DownloadedZip extends ExternalResource {

    private final File extractedZip;

    public DownloadedZip(String tempDir) {
        this.extractedZip = new File(tempDir + "/extractedZip");
    }

    @Override
    protected void before() throws Throwable {
        Client client = ClientBuilder.newClient();
        String port = System.getProperty("liberty.test.port");
        String url = "http://localhost:" + port + "/start/api/v1/data?tech=test&name=TestApp&deploy=local";
        System.out.println("Testing " + url);
        Response response = client.target(url).request("application/zip").get();
        InputStream entityInputStream = response.readEntity(InputStream.class);
        extractZip(entityInputStream);
    }

    private void extractZip(InputStream entityInputStream) throws IOException {
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

    public String getLocation() {
        return extractedZip.getAbsolutePath();
    }

}

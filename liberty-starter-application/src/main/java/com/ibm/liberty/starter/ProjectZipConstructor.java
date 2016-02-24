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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.IOUtils;
import org.xml.sax.SAXException;

import com.ibm.liberty.starter.api.v1.model.internal.Services;
import com.ibm.liberty.starter.api.v1.model.provider.Location;
import com.ibm.liberty.starter.api.v1.model.provider.Provider;
import com.ibm.liberty.starter.api.v1.model.provider.Sample;
import com.ibm.liberty.starter.api.v1.model.registration.Service;

public class ProjectZipConstructor {
    
    private final ServiceConnector serviceConnector;
    
    private Services services;
    private ConcurrentHashMap<String, byte[]> fileMap = new ConcurrentHashMap<String, byte[]>();
    private static final String SKELETON_JAR_FILENAME = "services/skeletonLibertyBuildImage.jar";
    private static final String BASE_INDEX_HTML = "indexHtml/index.html";
    private static final String INDEX_HTML_PATH = "myProject-application/src/main/webapp/index.html";
    private static final String POM_FILE = "pomXml/pom.xml";
    private String appName;
    
    public ProjectZipConstructor(ServiceConnector serviceConnector, Services services, String appName) {
        this.serviceConnector = serviceConnector;
        this.services = services;
        this.appName = appName;
    }
    
    public ConcurrentHashMap<String, byte[]> getFileMap() {
        return fileMap;
    }
    
    public void buildZip(OutputStream os) throws IOException, SAXException, ParserConfigurationException, TransformerException {
        initializeMap();
        addHtmlToMap();
        addTechSamplesToMap();
        addPomFileToMap();
        ZipOutputStream zos = new ZipOutputStream(os);
        createZipFromMap(zos);
        zos.close();
    }
    
    public void initializeMap() throws IOException {
        System.out.println("Entering method ProjectZipConstructor.initializeMap()");
        InputStream skeletonIS = this.getClass().getClassLoader().getResourceAsStream(SKELETON_JAR_FILENAME);
        ZipInputStream zis = new ZipInputStream(skeletonIS);
        ZipEntry ze;
        while ((ze = zis.getNextEntry()) != null) {
            String path = ze.getName();
            int length = 0;
            byte[] bytes = new byte[1024];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((length = zis.read(bytes)) != -1 ) {
                baos.write(bytes, 0, length);
            }
            putFileInMap(path, baos.toByteArray());
        }
        zis.close();
    }
    
    public void addHtmlToMap() throws IOException {
        System.out.println("Entering method ProjectZipConstructor.addHtmlToMap()");
        byte[] html = getHtmlFile();
        putFileInMap(INDEX_HTML_PATH, html);
    }
    
    public byte[] getHtmlFile() throws IOException {
        InputStream htmlIS = this.getClass().getClassLoader().getResourceAsStream(BASE_INDEX_HTML);
        HashMap<String, byte[]> techDescriptions = new HashMap<String, byte[]>();
        for (Service service : services.getServices()) {
            Provider provider = serviceConnector.getProvider(service);
            String description = provider.getDescription();
            byte[] bytes = description.getBytes();
            techDescriptions.put(service.getId(), bytes);
        }
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final int MAX_SIZE = 200000;
        char[] buffer = new char[MAX_SIZE];
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(htmlIS));
            reader.read(buffer, 0, MAX_SIZE);
        } catch (Exception e ){
            return null;
        } finally {
            reader.close();
        }
        String contents = new String(buffer);
        int index = contents.indexOf("<div id=\"technologies\">");
        if (index != -1) {
            int length = contents.length();
            String first = contents.substring(0, index);
            String last = contents.substring(index, length);
            baos.write(first.getBytes());
            Set<String> keys = techDescriptions.keySet();
            for (String key : keys) {
                baos.write(techDescriptions.get(key));
            }
            baos.write(last.getBytes());
        }
        return baos.toByteArray();
    }
    
    public void addTechSamplesToMap() throws IOException {
        System.out.println("Entering method ProjectZipConstructor.addTechSamplesToMap()");
        for (Service service : services.getServices()) {
            Sample sample = serviceConnector.getSample(service);
            Location[] locations = sample.getLocations();
            String basePath = sample.getBase();
            for (Location location : locations) {
                String fileUrl = location.getUrl();
                InputStream is = serviceConnector.getResourceAsInputStream(basePath + fileUrl);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                IOUtils.copy(is, baos);
                is.close();
                byte[] bytes = baos.toByteArray();
                baos.close();
                if (fileUrl.startsWith("/")) {
                    fileUrl = fileUrl.substring(1);
                }
                putFileInMap(fileUrl, bytes);
            }
        }
    }
    
    public void addPomFileToMap() throws SAXException, IOException, ParserConfigurationException, TransformerException {
        System.out.println("Entering method ProjectZipConstructor.addPomFileToMap()");
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(POM_FILE);
        PomModifier pomModifier = new PomModifier();
        pomModifier.setInputStream(inputStream);
        DependencyHandler depHand = new DependencyHandler(services, serviceConnector, appName);
        pomModifier.addStarterPomDependencies(depHand);
        byte[] bytes = pomModifier.getBytes();
        putFileInMap("pom.xml", bytes);
    }
    
    public void createZipFromMap(ZipOutputStream zos) throws IOException {
        System.out.println("Entering method ProjectZipConstructor.createZipFromMap()");
        Enumeration<String> en = fileMap.keys();
        while (en.hasMoreElements()) {
            String path = en.nextElement();
            byte[] byteArray = fileMap.get(path);
            ZipEntry entry = new ZipEntry(path);
            entry.setSize(byteArray.length);
            entry.setCompressedSize(-1);
            try {
                zos.putNextEntry(entry);
                zos.write(byteArray);
            } catch (IOException e) {
                throw new IOException(e);
            }
        }
    }
    
    public void putFileInMap(String path, byte[] file) {
        System.out.println("Inserting file " + path + " into map.");
        fileMap.put(path, file);
    }

}

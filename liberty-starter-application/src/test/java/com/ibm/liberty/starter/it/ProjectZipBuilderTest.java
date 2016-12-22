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
package com.ibm.liberty.starter.it;

import com.ibm.liberty.starter.api.v1.model.internal.Services;
import com.ibm.liberty.starter.api.v1.model.registration.Service;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.Assert.assertTrue;

public class ProjectZipBuilderTest {

    @Test
    @Ignore
    public void testAddingSourceClassFromSpringBoot() throws Exception {
        Service service = new Service();
        service.setId("springboot");
        //TODO: set other attributes
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Services services = new Services();
        List<Service> serviceList = new ArrayList<Service>();
        serviceList.add(service);
        services.setServices(serviceList);
//        ProjectConstructor testObject = new ProjectConstructor();
//        testObject.buildZip(outputStream);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        ZipInputStream zipIn = new ZipInputStream(inputStream);
        ZipEntry entry;
        boolean foundSpringBootEntry = false;
        boolean foundBaseEntry = false;
        boolean foundIndexHtml = false;
        while ((entry = zipIn.getNextEntry()) != null) {
            System.out.println("Zip has entry: " + entry.getName());
            if ("myProject-application/src/main/java/application/springboot/LibertyHelloController.java".equals(entry.getName())) {
                foundSpringBootEntry = true;
            } else if ("myProject-application/src/main/java/application/Application.java".equals(entry.getName())) {
                foundBaseEntry = true;
            } else if ("myProject-application/src/main/webapp/index.html".equals(entry.getName())) {
                foundIndexHtml = true;
                String indexHtml = inputStreamToString(zipIn);
                assertTrue(indexHtml.contains("<h1>Welcome to your Liberty Application</h1>"));
                assertTrue(indexHtml.contains("<h2>Spring Boot with Spring MVC</h2>"));
            }
        }
        zipIn.close();
        assertTrue(foundSpringBootEntry);
        assertTrue(foundBaseEntry);
        assertTrue(foundIndexHtml);
    }

    @Test
    @Ignore
    public void testAddingIndexHtmlForPersistence() throws Exception {
        Service service = new Service();
        service.setId("persistence");
        //TODO: set other attributes
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Services services = new Services();
        List<Service> serviceList = new ArrayList<Service>();
        serviceList.add(service);
        services.setServices(serviceList);
//        ProjectConstructor testObject = new ProjectConstructor();
//        testObject.buildZip(outputStream);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        ZipInputStream zipIn = new ZipInputStream(inputStream);
        ZipEntry entry;
        boolean foundBaseEntry = false;
        boolean foundIndexHtml = false;
        while ((entry = zipIn.getNextEntry()) != null) {
            System.out.println("Zip has entry: " + entry.getName());
            if ("myProject-application/src/main/java/application/Application.java".equals(entry.getName())) {
                foundBaseEntry = true;
            } else if ("myProject-application/src/main/webapp/index.html".equals(entry.getName())) {
                foundIndexHtml = true;
                String indexHtml = inputStreamToString(zipIn);
                assertTrue(indexHtml.contains("<h1>Welcome to your Liberty Application</h1>"));
                assertTrue(indexHtml.contains("<h2>Persistence</h2>"));
            }
        }
        zipIn.close();
        assertTrue(foundBaseEntry);
        assertTrue(foundIndexHtml);
    }

    @Test
    @Ignore
    // Failing but hopefully will be replaced with arquillian test
    public void testAddingSourceClassFromWeb() throws Exception {
        Service service = new Service();
        service.setId("web");
        //TODO: set other attributes
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Services services = new Services();
        List<Service> serviceList = new ArrayList<Service>();
        serviceList.add(service);
        services.setServices(serviceList);
//        ProjectConstructor testObject = new ProjectConstructor();
//        testObject.buildZip(outputStream);
        
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        ZipInputStream zipIn = new ZipInputStream(inputStream);
        ZipEntry entry;
        boolean foundServletEntry = false;
        boolean foundBaseEntry = false;
        boolean foundIndexHtml = false;
        while ((entry = zipIn.getNextEntry()) != null) {
            System.out.println("Zip has entry: " + entry.getName());
            if ("myProject-application/src/main/java/application/servlet/LibertyServlet.java".equals(entry.getName())) {
                foundServletEntry = true;
            } else if ("myProject-application/src/main/java/application/Application.java".equals(entry.getName())) {
                foundBaseEntry = true;
            } else if ("myProject-application/src/main/webapp/index.html".equals(entry.getName())) {
                foundIndexHtml = true;
                String indexHtml = inputStreamToString(zipIn);
                assertTrue("The index.html file should contain 'Welcome to your Liberty Application', instead found " + indexHtml, 
                           indexHtml.contains("<h1>Welcome to your Liberty Application</h1>"));
                assertTrue("The index.html file should contain 'Servlet', instead found " + indexHtml,
                           indexHtml.contains("<h2>Servlet</h2>"));
            }
        }
        zipIn.close();
        assertTrue(foundServletEntry);
        assertTrue(foundBaseEntry);
        assertTrue(foundIndexHtml);
    }

//    @Test
//    @Ignore
//    public void testFindHtml() throws Exception {
//
//        StringBuilder index = new StringBuilder();
//        char[] buffer = new char[1024];
//        int read = 0;
//        try (InputStreamReader reader = new InputStreamReader(is)) {
//            while ((read = reader.read(buffer)) != -1) {
//                index.append(buffer, 0, read);
//            }
//        } catch (IOException e) {
//            //just return what we've got
//            assertTrue("index was " + index.toString(), false);
//        }
//        assertTrue("index was " + index.toString(), index.toString().contains("<h1>Welcome to your Liberty Application</h1>"));
//        assertTrue("index was " + index.toString(), index.toString().contains("<h2>Servlet</h2>"));
//    }

    private String inputStreamToString(InputStream inputStream) throws IOException {
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

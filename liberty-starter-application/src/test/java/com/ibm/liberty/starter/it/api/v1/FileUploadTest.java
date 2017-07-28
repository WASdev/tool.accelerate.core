/*******************************************************************************
 * Copyright (c) 2017 IBM Corp.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.liberty.starter.build.maven.DomUtil;

public class FileUploadTest {
    
    @Test
    public void testStarterWorkspaceEndpoint() throws Exception {
        String endpoint = "/start/api/v1/workspace";
        Client client = ClientBuilder.newClient();
        String port = System.getProperty("liberty.test.port");
        String url = "http://localhost:" + port + endpoint;
        System.out.println("Testing " + url);
        Response response = client.target(url).request().get();
        try {
            int status = response.getStatus();
            assertEquals("Response incorrect, response status was " + status, 200, status);
            String workspaceId = response.readEntity(String.class);
            assertNotNull("Returned workspace ID was not a valid UUID : " + workspaceId, UUID.fromString(workspaceId));
        } finally {
            response.close();
        }
    }
    
    @Test
    public void testUploadInvalidNoTech() throws Exception {
        int response = invokeUploadEndpoint("","sampleFileNoTech.txt", "");
        assertEquals("Response incorrect, response was " + response, Response.Status.BAD_REQUEST.getStatusCode(), response);
    }
    
    @Test
    public void testUploadInvalidNoWorkspace() throws Exception {
        int response = invokeUploadEndpoint("tech=test", "sampleFileNoWorkspace.txt", "");
        assertEquals("Response incorrect, response was " + response, Response.Status.BAD_REQUEST.getStatusCode(), response);
    }

    @Test
    public void testUploadProcessPackage() throws Exception {
        String uuid = UUID.randomUUID().toString();
        
        // Upload a file
        int responseCode = invokeUploadEndpoint("tech=swagger&workspace=" + uuid, "sampleUpload.json", getBasicSwagger("CollectiveInfo1"));
        assertEquals("Response 1 was incorrect, response was " + responseCode, Response.Status.OK.getStatusCode(), responseCode);
        
        // Upload second file (without cleaning up)
        responseCode = invokeUploadEndpoint("tech=swagger&workspace=" + uuid, "sampleUpload2.json", getBasicSwagger("CollectiveInfo2"));
        assertEquals ("Response 2 was incorrect, response was " + responseCode, Response.Status.OK.getStatusCode(), responseCode);
        
        // Upload third file (after cleaning up existing files) and process the file
        responseCode = invokeUploadEndpoint("tech=swagger&workspace=" + uuid + "&cleanup=true&process=true", "sampleUpload3.json", getBasicSwagger("CollectiveInfo3"));
        assertEquals("Response 3 was incorrect, response was " + responseCode, Response.Status.OK.getStatusCode(), responseCode);
        
        // Invoke the v1/data endpoint to ensure that the packaged files are contained within the zip and the features to 
        // install specified by the 'swagger' tech type are present within pom.xml
        Client client = ClientBuilder.newClient();
        String port = System.getProperty("liberty.test.port");
        String url = "http://localhost:" + port + "/start/api/v1/data?tech=swagger&name=Test&deploy=local&techoptions=swagger:server&workspace=" + uuid;
        System.out.println("Testing " + url);
        Response response = client.target(url).request("application/zip").get();
        try {
            int responseStatus = response.getStatus();
            String output = "";
            if (responseStatus != 200) {
                output = response.readEntity(String.class);
                assumeTrue(!output.contains("Missing project generation configuration: generation URL, starter URL"));
            }
            assertTrue("Expected response status 200, instead found " + responseStatus + " and output " + output, responseStatus == 200);
            // Read the response into an InputStream
            InputStream entityInputStream = response.readEntity(InputStream.class);
            // Create a new ZipInputStream from the response InputStream
            ZipInputStream zipIn = new ZipInputStream(entityInputStream);
            ZipEntry inputEntry = null;
            boolean modelFileExists = false;
            boolean apiFileExists = false;
            boolean deletedFileExists = false;
            boolean foundFeaturesToInstall = false;
            boolean foundPluginNode = false;
            boolean foundInstallNode = false;
            String zipEntries = "";
            Document pomContents = null;
            while ((inputEntry = zipIn.getNextEntry()) != null) {
                String entryName = inputEntry.getName();
                zipEntries += entryName + ",";
                if ("src/main/java/io/swagger/model/CollectiveInfo3.java".equals(entryName)) {
                    modelFileExists = true;
                } else if ("src/main/java/io/swagger/api/IbmApi.java".equals(entryName)) {
                    apiFileExists = true;
                } else if ("src/main/java/io/swagger/model/CollectiveInfo1.java".equals(entryName) || "src/main/java/io/swagger/model/CollectiveInfo2.java".equals(entryName)) {
                    deletedFileExists = true;
                } else if ("pom.xml".equals(entryName)) {
                    DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = domFactory.newDocumentBuilder();
                    // Use an anonymous inner class to delegate to the zip input stream as db.parse closes the stream
                    Document doc = db.parse(new InputStream() {

                        @Override
                        public int read() throws IOException {
                            return zipIn.read();
                        }

                    });
                    pomContents = doc;
                    printPomToSysOut(pomContents);
                    NodeList pluginNodeList = doc.getElementsByTagName("plugin");
                    for (int i = 0; i < pluginNodeList.getLength(); i++) {
                        Element pluginNode = (Element) pluginNodeList.item(i);
                        if (DomUtil.nodeHasId(pluginNode, "liberty-maven-plugin")) {
                            foundPluginNode = true;
                            Node executions = pluginNode.getElementsByTagName("executions").item(0);
                            NodeList executionNodes = ((Element) executions).getChildNodes();
                            for (int j = 0; j < executionNodes.getLength(); j++) {
                                Node execution = executionNodes.item(j);
                                if (DomUtil.nodeHasId(execution, "install-feature")) {
                                    foundInstallNode = true;
                                    Node config = ((Element) execution).getElementsByTagName("configuration").item(0);
                                    Node features = ((Element) config).getElementsByTagName("features").item(0);
                                    assertTrue("Install feature was not found : apiDiscovery-1.0", hasChildNode(features, "feature", "apiDiscovery-1.0"));
                                    assertTrue("acceptLicense node with property true was not found", hasChildNode(features, "acceptLicense", "true"));
                                    foundFeaturesToInstall = true;
                                }
                            }
                        }
                    }
                }
                zipIn.closeEntry();
            }
            zipIn.close();
            assertTrue("Model file doesn't exist at src/main/java/io/swagger/model/CollectiveInfo3.java in the zip file. Entries found:" + zipEntries, modelFileExists);
            assertTrue("API file doesn't exist at src/main/java/io/swagger/api/IbmApi.java in the zip file. Entries found:" + zipEntries, apiFileExists);
            assertTrue("liberty-maven-plugin node was not found", foundPluginNode);
            assertTrue("install-feature node was not found", foundInstallNode);
            assertTrue("Features to install were not found in pom.xml from the zip file.", foundFeaturesToInstall);
            assertFalse("Deleted file exists in the zip file. Entries found:" + zipEntries, deletedFileExists);
        } finally {
            response.close();
        }
    }
    
    private void printPomToSysOut(Document pom) throws TransformerFactoryConfigurationError, TransformerException {
        System.out.println("Found pom.xml with contents:");
        DOMSource domSource = new DOMSource(pom);
        StreamResult streamResult = new StreamResult(System.out);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.transform(domSource, streamResult);
    }
    
    private boolean hasChildNode(Node parentNode, String nodeName, String nodeValue){
        return getChildNode(parentNode, nodeName, nodeValue) != null ? true : false;
    }
    
    /**
         * Get all matching child nodes
         * @param parentNode - the parent node
         * @param name - name of child node to match 
         * @param value - value of child node to match, specify null to not match value
         * @return matching child nodes
         */
        private static List<Node> getChildren(Node parentNode, String name, String value){
                List<Node> childNodes = new ArrayList<Node>();
                if(parentNode == null || name == null){
                        return childNodes;
                }

                if (parentNode.getNodeType() == Node.ELEMENT_NODE && parentNode.hasChildNodes()) {
                        NodeList children = parentNode.getChildNodes();
                        for(int i=0; i < children.getLength(); i++){
                                Node child = children.item(i);
                                if(child != null && name.equals(child.getNodeName()) && (value == null || value.equals(child.getTextContent()))){
                                        childNodes.add(child);
                                }
                        }
                }

                return childNodes;
        }
        
    /**
     * Get the matching child node
     * @param parentNode - the parent node
     * @param name - name of child node to match 
     * @param value - value of child node to match
     * @return the child node if a match was found, null otherwise
     */
    private Node getChildNode(Node parentNode, String name, String value){
        List<Node> matchingChildren = getChildren(parentNode, name, value);
        return (matchingChildren.size() > 0) ? matchingChildren.get(0) : null;
    }
    
    private int invokeUploadEndpoint(String params, String fileName, String fileContent) throws Exception {
        String port = System.getProperty("liberty.test.port");
        String path = "http://localhost:" + port + "/start/api/v1/upload" + ((params != null && !params.trim().isEmpty()) ? ("?" + params) : "");
        System.out.println("Testing " + path);
        
        String boundary = "----WebKitFormBoundarybcoFJqLu81T8NPk8";
        URL url = new URL(path);
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
        httpConnection.setUseCaches(false);
        httpConnection.setDoInput(true);
        httpConnection.setDoOutput(true);        
        
        httpConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        OutputStream outputStream = httpConnection.getOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream), true);
        
        final String NEW_LINE = "\r\n";
        writer.append("--" + boundary).append(NEW_LINE);
        writer.append("Content-Disposition: form-data; name=\"fileFormData\"; filename=\"" + fileName + "\"").append(NEW_LINE);
        writer.append("Content-Type: application/octet-stream").append(NEW_LINE).append(NEW_LINE);
        writer.append(fileContent).append(NEW_LINE);
        writer.append(NEW_LINE).flush();
        writer.append("--" + boundary + "--").append(NEW_LINE);
        writer.close();
        
        httpConnection.disconnect();
        return httpConnection.getResponseCode();
    }
    
    private String getBasicSwagger(String modelName) {
        String swaggerContent = "{\"swagger\": \"2.0\",\"info\": {\"description\": \"Info APIs for Collective\",\"version\": \"1.0.0\"},\"basePath\": \"/\","
                + "\"paths\": {\"/ibm/api/root1/v1/info\": {\"get\": {\"summary\": \"Retrieve collective's core information\","
                + "\"description\": \"Returns a JSON with core information about collective\",\"operationId\": \"getInfo\",\"produces\": "
                + "[\"application/json\"],\"responses\": {\"200\": {\"description\": \"successful operation\","
                + "\"schema\": {\"$ref\": \"#/definitions/" + modelName + "\"}},\"404\": {\"description\": \"Invalid path\"}}}}},\"definitions\": {"
                + "\"" + modelName + "\": {\"properties\": {\"name\": {\"type\": \"string\",\"description\": \"Name of the collective\"}}}}}";
        return swaggerContent;
    }
}

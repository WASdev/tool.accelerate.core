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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class StarterUtil {
	
	private static final Logger log = Logger.getLogger(StarterUtil.class.getName());

	private static String serverOutputDir, wlpInstallDir, javaHome, sharedResourceDir;
	
	public static final String WORKAREA = "workarea";
	public static final String APP_ACCELERATOR_WORKAREA = "appAccelerator";
	public static final String PACKAGE_DIR = "package";
	
	static{
		try{
			sharedResourceDir =  processPath(((String)(new InitialContext().lookup("sharedResourceDir"))));
			serverOutputDir = processPath(((String)(new InitialContext().lookup("serverOutputDir"))));
			javaHome = processPath(((String)(new InitialContext().lookup("javaHome"))));
			wlpInstallDir = processPath(((String)(new InitialContext().lookup("wlpInstallDir"))));
			
			log.info("serverOutputDir=" + serverOutputDir);
			log.info("wlpInstallDir=" + wlpInstallDir);
			log.info("javaHome=" + javaHome);
			log.info("sharedResourceDir=" + sharedResourceDir);
		}catch (NamingException ne){
			log.info("NamingException occurred: " + ne);
		}
	}
	
	private static String processPath(String string) {
		if(string == null){
			return "";
		}
		return string.replace('\\', '/');
	}

	public static String getSharedResourceDir() {
		return sharedResourceDir;
	}

	public static String getJavaHome() {
		return javaHome;
	}

	public static String getWlpInstallDir() {
		return wlpInstallDir;
	}

	public static String getServerOutputDir() {
		return serverOutputDir;
	}
	
	public static String getWorkspaceDir(String workspaceId){
		return getServerOutputDir() + "/" + WORKAREA + "/" + APP_ACCELERATOR_WORKAREA + "/" + workspaceId;
	}
	
	/**
     * Determine if the parent node contains a child node with matching name
     * @param parentNode - the parent node
     * @param nodeName - name of child node to match 
     * @return boolean value to indicate whether the parent node contains matching child node
     */
    public static boolean hasNode(Node parentNode, String nodeName){
    	if(parentNode == null || nodeName == null){
    		return false;
    	}
    	if (parentNode.getNodeType() == Node.ELEMENT_NODE && parentNode.hasChildNodes()) {
    		NodeList children = parentNode.getChildNodes();
        	for(int i=0; i < children.getLength(); i++){
            	Node child = children.item(i);
            	if(child != null && nodeName.equals(child.getNodeName())){
            		return true;
            	}
            }
    	}
    	
    	return false;
    }
    
    /**
     * Determine if the parent node contains a child node with matching name and value
     * @param parentNode - the parent node
     * @param nodeName - name of child node to match 
     * @param nodeValue - value of child node to match
     * @return boolean value to indicate whether the parent node contains matching child node
     */
    public static boolean hasNode(Node parentNode, String nodeName, String nodeValue){
    	if(parentNode == null || nodeName == null || nodeValue == null){
    		return false;
    	}
    	if (parentNode.getNodeType() == Node.ELEMENT_NODE && parentNode.hasChildNodes()) {
    		NodeList children = parentNode.getChildNodes();
        	for(int i=0; i < children.getLength(); i++){
            	Node child = children.item(i);
            	if(child != null && nodeName.equals(child.getNodeName()) && nodeValue.equals(child.getTextContent())){
            		return true;
            	}
            }
    	}
    	
    	return false;
    }
    
    /**
     * Get the matching child node
     * @param parentNode - the parent node
     * @param name - name of child node to match 
     * @return the child node if a match was found, null otherwise
     */
    public static Node getNode(Node parentNode, String name){
    	if(parentNode == null || name == null){
    		return null;
    	}
    	if (parentNode.getNodeType() == Node.ELEMENT_NODE && parentNode.hasChildNodes()) {
    		NodeList children = parentNode.getChildNodes();
        	for(int i=0; i < children.getLength(); i++){
            	Node child = children.item(i);
            	if(child != null && name.equals(child.getNodeName())){
            		return child;
            	}
            }
    	}
    	
    	return null;
    }
    
    /**
     * Generate the list of files in the directory and all of its sub-directories (recursive)
     * 
     * @param dir - The directory
     * @param filesListInDir - List to store the files
     */
    public static void populateFilesList(File dir, List<File> filesListInDir) {
        File[] files = dir.listFiles();
        for(File file : files){
            if(file.isFile()){
            	filesListInDir.add(file);
            }else{
            	populateFilesList(file, filesListInDir);
            }
        }
    }
    
    /**
     * Perform an identity transformation (no XSL stylesheet)
     * 
     * @param source - The source
     * @param result - The result
     * @param omitXmlDeclaration - flag to indicate if the xml declaration should be omitted at the top
     * @param indent - flag to indicate if result should be indented
     * @param indentAmount - value to indent
     * @throws TransformerFactoryConfigurationError
     * @throws TransformerException
     */
    public static void identityTransform(Source source, Result result, boolean omitXmlDeclaration, boolean indent, String indentAmount) throws TransformerFactoryConfigurationError, TransformerException{
    	Transformer transformer = TransformerFactory.newInstance().newTransformer();
    	if(indent){
    		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    	}
    	if(omitXmlDeclaration){
    		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    	}
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", indentAmount);
        transformer.transform(source, result);
    }
    
    public static Document getDocument(byte[] source) throws ParserConfigurationException, SAXException, IOException{
    	ByteArrayInputStream inputStream = new ByteArrayInputStream(source);
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = domFactory.newDocumentBuilder();
        return db.parse(inputStream);
    }
}

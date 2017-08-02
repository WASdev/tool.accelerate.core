/*******************************************************************************
 * Copyright (c) 2016,2017 IBM Corp.
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

import com.ibm.liberty.starter.api.v1.model.registration.Service;
import com.ibm.liberty.starter.client.BxCodegenClient;
import com.ibm.liberty.starter.exception.ProjectGenerationException;

public class ProjectConstructor {
    
    private static final Logger log = Logger.getLogger(ProjectConstructor.class.getName());
    private ConcurrentHashMap<String, byte[]> fileMap = new ConcurrentHashMap<>();
    private final ProjectConstructionInputData inputData;

    public ProjectConstructor(ProjectConstructionInputData inputData) {
        this.inputData = inputData;
    }

    public enum DeployType {
        LOCAL, BLUEMIX
    }
    public enum BuildType {
        MAVEN("mvn install liberty:run-server"),
        GRADLE("gradle build libertyStart");
        public String runInstruction;
        BuildType(String runInstruction) {
            this.runInstruction = runInstruction;
        }
    }
    
    public Map<String, byte[]> getFileMap() {
        return fileMap;
    }
    
    public Map<String, byte[]> buildFileMap() throws IOException, SAXException, ParserConfigurationException, TransformerException, ProjectGenerationException {
        addBxCodegenFilesToMap();
        addDynamicPackages();
        cleanUpDynamicPackages();
        return fileMap;
    }
    
    private void addBxCodegenFilesToMap() throws ProjectGenerationException {
        Map<String, byte[]> map = (new BxCodegenClient()).getFileMap(inputData);
        fileMap.putAll(map);
    }
    
    private void cleanUpDynamicPackages() throws IOException{
        // Delete dynamically generated packages as they were already packaged.
        // ** Note **: Don't delete these packages prior to this stage as other operations may depend on the existence of these packages to perform certain tasks.
        for (Service service : inputData.services.getServices()) {
            String serviceId = service.getId();
            
            String packageLocation = inputData.workspaceDirectory + "/" + serviceId + "/" + StarterUtil.PACKAGE_DIR;
            File packageDir = new File(packageLocation);
            
            if(packageDir.exists() && packageDir.isDirectory()){
                FileUtils.deleteDirectory(packageDir);
                log.log(Level.FINE, "Deleted package directory for " + serviceId + " technology. : " + packageLocation);
            }
        }
    }
    
    private void addDynamicPackages() throws IOException {
        log.log(Level.FINE, "Entering method ProjectConstructor.addDynamicPackages()");
        if(inputData.workspaceDirectory == null || inputData.workspaceDirectory.isEmpty() || !(new File(inputData.workspaceDirectory).exists())){
            log.log(Level.FINE, "No dynamic packages to add since workspace doesn't exist : " + inputData.workspaceDirectory);
            return;
        }
        
        for (Service service : inputData.services.getServices()) {
            String serviceId = service.getId();
            String packageLocation = inputData.workspaceDirectory + "/" + serviceId + "/" + StarterUtil.PACKAGE_DIR;
            File packageDir = new File(packageLocation);
            
            if(packageDir.exists() && packageDir.isDirectory()){
                log.log(Level.FINE, "Package directory for " + serviceId + " technology exists : " + packageLocation);
                List<File> filesListInDir = new ArrayList<File>();
                StarterUtil.populateFilesList(packageDir, filesListInDir);
                
                for(File aFile : filesListInDir){
                    String path = aFile.getAbsolutePath().replace('\\', '/').replace(packageLocation, "");

                    if(path.startsWith("/")){
                        path = path.substring(1);
                    }
                    putFileInMap(path, FileUtils.readFileToByteArray(aFile));
                    log.log(Level.FINE, "Packaged file " + aFile.getAbsolutePath() + " to " + path);
                }
            }
        }
        log.log(Level.FINE, "Exiting method ProjectConstructor.addDynamicPackages()");
    }
    
    private void putFileInMap(String path, byte[] file) {
        log.log(Level.INFO, "Inserting file " + path + " into map.");
        fileMap.put(path, file);
    }

}

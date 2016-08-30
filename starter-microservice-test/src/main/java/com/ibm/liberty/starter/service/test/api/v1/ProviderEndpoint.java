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
package com.ibm.liberty.starter.service.test.api.v1;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.io.FileUtils;

import com.ibm.liberty.starter.api.v1.model.provider.Dependency;
import com.ibm.liberty.starter.api.v1.model.provider.Dependency.Scope;
import com.ibm.liberty.starter.api.v1.model.provider.Location;
import com.ibm.liberty.starter.api.v1.model.provider.Provider;

@Path("v1/provider")
public class ProviderEndpoint {

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Provider details(@Context UriInfo info) {
    	Provider details = new Provider();
    	String description = getStringResource("/description.html");
    	details.setDescription(description);
    	
        Location repoLocation = new Location();
        String url = info.getBaseUri().resolve("../artifacts").toString();
        repoLocation.setUrl(url);
        details.setRepoUrl(repoLocation);
    	
        Dependency providedDependency = new Dependency();
        providedDependency.setScope(Scope.PROVIDED);
        providedDependency.setGroupId("net.wasdev.wlp.starters.test");
        providedDependency.setArtifactId("provided-pom");
        providedDependency.setVersion("0.0.1");
     
        Dependency runtimeDependency = new Dependency();
        runtimeDependency.setScope(Scope.RUNTIME);
        runtimeDependency.setGroupId("net.wasdev.wlp.starters.test");
        runtimeDependency.setArtifactId("runtime-pom");
        runtimeDependency.setVersion("0.0.1");
        
        Dependency compileDependency = new Dependency();
        compileDependency.setScope(Scope.COMPILE);
        compileDependency.setGroupId("net.wasdev.wlp.starters.test");
        compileDependency.setArtifactId("compile-pom");
        compileDependency.setVersion("0.0.1");
     
        Dependency[] dependencies = {providedDependency, runtimeDependency, compileDependency};
        details.setDependencies(dependencies);
    	return details;
    }
    
    //read the description contained in the index.html file
    private String getStringResource(String path) {
    	InputStream in = getClass().getResourceAsStream(path);
    	
    	StringBuilder index = new StringBuilder();
    	char[] buffer = new char[1024];
    	int read = 0;
    	try(InputStreamReader reader = new InputStreamReader(in)){
    		while((read = reader.read(buffer)) != -1) {
    			index.append(buffer, 0, read);
    		}
    	} catch (IOException e) {
    		//just return what we've got
    		return index.toString();
    	}
    	return index.toString();
    }

    @GET
    @Path("samples")
    @Produces(MediaType.APPLICATION_JSON)
    public Response constructSample(@Context UriInfo info) {
    	StringBuilder json = new StringBuilder("{\n");
    	String base = info.getBaseUri().resolve("../sample").toString();
    	json.append("\"base\" : \"" + base + "\",\n");
    	json.append(getStringResource("/locations.json"));
    	json.append("}\n");
    	return Response.ok(json.toString()).build();
    }
    
    @GET
    @Path("features/install")
    @Produces(MediaType.TEXT_PLAIN)
    public String getFeaturesToInstall(){
        return "servlet-3.1,apiDiscovery-1.0";
    }
    
    @GET
    @Path("uploads/process")
    @Produces(MediaType.TEXT_PLAIN)
    public String processUploads(@QueryParam("path") String uploadDirectoryPath) throws IOException {
    	File uploadDirectory;
    	if(uploadDirectoryPath == null || !(uploadDirectory = new File(uploadDirectoryPath)).exists()){
    		return "Couldn't fulfill the request due to internal error";
    	}
    	
    	List<File> filesListInDir = new ArrayList<File>();
    	populateFilesList(uploadDirectory, filesListInDir);
    	for(File uploadedFile : filesListInDir){
    		uploadedFile.renameTo(new File(uploadedFile.getParent() + "/" + uploadedFile.getName() + "_renamed"));
    	}
    	return "success";
    }
    
    @GET
    @Path("packages/prepare")
    public String prepareDynamicPackages(@QueryParam("path") String techWorkspaceDir, @QueryParam("options") String options, @QueryParam("techs") String techs) throws IOException {
    	if(techWorkspaceDir != null && !techWorkspaceDir.trim().isEmpty()){
    		FileUtils.deleteQuietly(new File(techWorkspaceDir + "/package"));
    		
    		File techWorkspace = new File(techWorkspaceDir);
    		if(techs != null && options != null && !options.trim().isEmpty()){
    			String[] techOptions = options.split(",");
        		String testOptionOne = techOptions.length >= 1 ? techOptions[0] : null;

        		if("testoption1".equals(testOptionOne) && techs.contains("test")){
        			String packageTargetDirPath = techWorkspaceDir + "/package/myProject-application";
					File packageTargetDir = new File(packageTargetDirPath);
					FileUtils.copyDirectory(techWorkspace, packageTargetDir);
					return "success";
        		}
    		}
		}
    	return "failure";
    }
    
    private void populateFilesList(File dir, List<File> filesListInDir) {
        File[] files = dir.listFiles();
        for(File file : files){
            if(file.isFile()) filesListInDir.add(file);
            else populateFilesList(file, filesListInDir);
        }
    }
    
}
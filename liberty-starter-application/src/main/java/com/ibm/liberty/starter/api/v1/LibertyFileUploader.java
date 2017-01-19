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
package com.ibm.liberty.starter.api.v1;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import com.ibm.liberty.starter.StarterUtil;
import com.ibm.liberty.starter.PatternValidation;
import com.ibm.liberty.starter.ServiceConnector;
import com.ibm.liberty.starter.PatternValidation.PatternType;
import com.ibm.liberty.starter.api.v1.model.registration.Service;


@WebServlet(urlPatterns={"/api/v1/upload"})
@MultipartConfig
public class LibertyFileUploader extends HttpServlet {

	private static final long serialVersionUID = 5580330429144541971L;

	private static final Logger log = Logger.getLogger(LibertyFileUploader.class.getName());

	private static final String PARAMETER_TECH = "tech";			// required - The technology the uploaded files are related to.
	private static final String PARAMETER_WORKSPACE = "workspace";	// required - unique workspace directory to upload the files to.
	private static final String PARAMETER_CLEANUP = "cleanup";	//optional - Clean the directory before uploading the files. Default value is 'false'
	private static final String PARAMETER_PROCESS = "process";	//optional - Process uploaded file(s) depending on the specified technology. Default value is 'false'

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String tech = request.getParameter(PARAMETER_TECH);
		String workspaceId = request.getParameter(PARAMETER_WORKSPACE);	//specify the unique workspace directory to upload the file(s) to.	
		Collection<Part> filePartCollection = request.getParts();
		
		String serverHostPort = request.getRequestURL().toString().replace(request.getRequestURI(), "");
		int schemeLength = request.getScheme().toString().length();
		String internalHostPort = "http" + serverHostPort.substring(schemeLength);
		log.log(Level.FINER, "serverHostPort : " + serverHostPort);
		final ServiceConnector serviceConnector = new ServiceConnector(serverHostPort, internalHostPort);
		HashMap<Part, String> fileNames = new HashMap<Part, String>();
		if(!isValidRequest(request, response, tech, workspaceId, filePartCollection, serviceConnector, fileNames)){
			return;
		}

		Service techService = serviceConnector.getServiceObjectFromId(tech);
		String techDirPath = StarterUtil.getWorkspaceDir(workspaceId) + "/" + techService.getId();
		File techDir = new File(techDirPath);
		if(techDir.exists() && techDir.isDirectory() && "true".equalsIgnoreCase(request.getParameter(PARAMETER_CLEANUP))){
			FileUtils.cleanDirectory(techDir);
			log.log(Level.FINER, "Cleaned up tech workspace directory : " + techDirPath);
		}

		for (Part filePart : filePartCollection){
			if(!techDir.exists()){
				FileUtils.forceMkdir(techDir);
				log.log(Level.FINER, "Created tech directory :" + techDirPath);
			}
			
			String filePath = techDirPath + "/" + fileNames.get(filePart);
			log.log(Level.FINER, "File path : " + filePath);
			File uploadedFile = new File(filePath);

			Files.copy(filePart.getInputStream(), uploadedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			log.log(Level.FINE, "Copied file to " + filePath);	
		}

		if("true".equalsIgnoreCase(request.getParameter(PARAMETER_PROCESS))){
			// Process uploaded file(s)
			String processResult = serviceConnector.processUploadedFiles(techService, techDirPath);
			if(!processResult.equalsIgnoreCase("success")){
				log.log(Level.INFO, "Error processing the files uploaded to " + techDirPath + " : Result=" + processResult);
				response.sendError(500, processResult);
				return;
			}
			log.log(Level.FINE, "Processed the files uploaded to " + techDirPath);	
		}

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("success");
		out.close();
	}
	
	private boolean isValidRequest(HttpServletRequest req, HttpServletResponse resp, String tech, String wsId, Collection<Part> parts, ServiceConnector sc, HashMap<Part, String> fileNames) throws IOException{
		if(tech == null || tech.trim().isEmpty() || !PatternValidation.checkPattern(PatternType.TECH, tech)){
			log.log(Level.INFO, "Invalid tech parameter");
			resp.sendError(400, "Invalid request: specify valid tech");
			return false;
		}
		if (sc.getServiceObjectFromId(tech) == null) {
			log.log(Level.INFO, "Invalid tech type: " + tech);
			resp.sendError(400, "Invalid technology type : " + tech);
			return false;
		}
		
		if(wsId == null || wsId.trim().isEmpty()){
			log.log(Level.INFO, "Invalid workspace: " + wsId);
			resp.sendError(400, "Invalid workspace : " + wsId);
			return false;
		}
		if (req.getContentType() == null || !req.getContentType().toLowerCase().contains("multipart/form-data") ) {
			log.log(Level.INFO, "Invalid request type : " + req.getContentType());
			resp.sendError(400, "Invalid request type : " + req.getContentType());
			return false;
		}
		
		if(parts == null || parts.size() == 0){
			log.log(Level.INFO, "No file to upload");
			resp.sendError(400, "Invalid request: specify file(s) to upload");
			return false;
		}
		
		for (Part filePart : parts){
			if(filePart == null){
				log.log(Level.INFO, "Invalid filepart :" + parts);
				resp.sendError(400, "Invalid request: filepart can not be null");
				return false;
			}
			
			String fileName = getSubmittedFileName(filePart);
			if(fileName == null || fileName.trim().isEmpty()){
				log.log(Level.INFO, "File name was invalid : filePart=" + filePart);
				resp.sendError(400, "Invalid request: specify a valid file name");
				return false;
			}
			fileNames.put(filePart, fileName);
		}
		
		return true;		
	}

	private static String getSubmittedFileName(Part part) {
		for (String cd : part.getHeader("content-disposition").split(";")) {
			if (cd.trim().startsWith("filename")) {
				String fileName = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
				log.log(Level.FINEST, "fileName=" + fileName + " : part=" + part);	
				return fileName.substring(fileName.lastIndexOf('/') + 1).substring(fileName.lastIndexOf('\\') + 1);
			}
		}
		log.log(Level.FINE, "File name was not retrieved : part=" + part);	
		return null;
	}
}
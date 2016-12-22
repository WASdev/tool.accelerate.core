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

import org.apache.commons.io.FileUtils;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.validation.ValidationException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StarterUtil {

	private static final Logger log = Logger.getLogger(StarterUtil.class.getName());

	private static String serverOutputDir;

	public static final String WORKAREA = "workarea";
	public static final String APP_ACCELERATOR_WORKAREA = "appAccelerator";
	public static final String PACKAGE_DIR = "package";

	private static String processPath(String string) {
		if(string == null){
			return "";
		}
		return string.replace('\\', '/');
	}

	private static String getServerOutputDir() {
		if(serverOutputDir == null){
			try{
				serverOutputDir = processPath(((String)(new InitialContext().lookup("serverOutputDir"))));
				if(!serverOutputDir.endsWith("/")){
					serverOutputDir += "/";
				}
				log.info("serverOutputDir=" + serverOutputDir);
			}catch (NamingException ne){
				log.severe("NamingException occurred while retrieving the value of 'serverOutputDir': " + ne);
				throw new ValidationException("NamingException occurred while retrieving the value of 'serverOutputDir': " + ne);
			}
		}
		return serverOutputDir;
	}

	public static String getWorkspaceDir(String workspaceId){
		return getServerOutputDir() + WORKAREA + "/" + APP_ACCELERATOR_WORKAREA + "/" + workspaceId;
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

	public static String createCleanWorkspace() throws IOException {
		String uuid = UUID.randomUUID().toString();

		//Clean up workspace directory if it already exists (from previous server run)
		String workspaceDirPath = StarterUtil.getWorkspaceDir(uuid);
		File workspaceDir = new File(workspaceDirPath);
		if(workspaceDir.exists()){
			log.log(Level.FINE, "Workspace directory already exists : " + workspaceDirPath);
			FileUtils.deleteDirectory(workspaceDir);
			log.log(Level.FINE, "Deleted workspace directory : " + workspaceDirPath);
		}
		return uuid;
	}
	
}

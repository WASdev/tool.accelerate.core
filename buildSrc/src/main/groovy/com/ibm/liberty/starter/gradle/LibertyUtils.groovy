/*
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
*/ 

package com.ibm.liberty.starter.gradle

import org.gradle.api.*
import org.gradle.api.plugins.JavaPlugin

class LibertyUtils implements Plugin<Project> {

    void apply(Project project) {
        project.extensions.create("libertyutils", LibertyUtilsProperties)
        project.task('addServerEnv') {
            doLast{
                if (project.hasProperty('libertyutils')) {
                    def envFile = new File(project.projectDir.getAbsolutePath() + "/../liberty-starter-wlpcfg/servers/StarterServer/server.env")
                    if (!envFile.exists()) {
                       envFile.createNewFile()
                    }
                    String[] envVariables = project.libertyutils.serverEnv
                    String envFileEntry = ""
                    for (String envVar : envVariables) {
                        envFileEntry += envVar + "\n"
                    }
                    envFile.write(envFileEntry)
                }
            }
		}
		
		
		project.task('removeServerEnv') {
			doLast{
		    	if (project.hasProperty('libertyutils')) {
		    		def envFile = new File(project.projectDir.getAbsolutePath() + "/../liberty-starter-wlpcfg/servers/StarterServer/server.env")
		    		if (envFile.exists()) {
		    		    System.out.println("Deleting server.env file")
		    			if (!envFile.delete()) {
		    			   throw new GradleScriptException("Failed to delete server.env file.")
		    			}
		    		} else {
		    		    System.out.println("server.env file does not exist at location " + project.projectDir.getAbsolutePath() + "/../liberty-starter-wlpcfg/servers/StarterServer/server.env")
		    		}
		    	}
		    }
		}
	}
}

class LibertyUtilsProperties {
    //The server environment variables to set.
    def String[] serverEnv = ''
}
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
package com.ibm.liberty.starter.build.maven;

import com.ibm.liberty.starter.DependencyHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AppNameCommand implements PomModifierCommand {

    private final String appName;
    private static final Logger log = Logger.getLogger(AppNameCommand.class.getName());
    
    public AppNameCommand(DependencyHandler dependencyHandler) {
        appName = dependencyHandler.getAppName();
    }

    @Override
    public void modifyPom(Document pom) {
        log.log(Level.INFO, "Setting app name node to " + appName);
        NodeList propertiesNodeList = pom.getElementsByTagName("properties");
        Node propertiesNode = propertiesNodeList.item(0);
        Node appNameNode = pom.createElement("app.name");
        appNameNode.setTextContent(appName);
        propertiesNode.appendChild(appNameNode);
    }

}

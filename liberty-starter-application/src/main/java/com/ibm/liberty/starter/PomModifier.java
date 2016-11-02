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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import com.ibm.liberty.starter.ProjectZipConstructor.DeployType;
import com.ibm.liberty.starter.pom.AddDependenciesCommand;
import com.ibm.liberty.starter.pom.AppNameCommand;
import com.ibm.liberty.starter.pom.PomModifierCommand;
import com.ibm.liberty.starter.pom.SetDefaultProfileCommand;
import com.ibm.liberty.starter.pom.SetRepositoryCommand;

public class PomModifier {

    private InputStream pomInputStream;
    private DependencyHandler dependencyHandler;
    private DeployType deployType;

    public PomModifier(DeployType deployType) {
        this.deployType = deployType;
    }

    public void setInputStream(InputStream pomInputStream) {
        this.pomInputStream = pomInputStream;
    }

    public void addStarterPomDependencies(DependencyHandler depHand) {
        this.dependencyHandler = depHand;
    }

    public byte[] getBytes() throws TransformerException, IOException, ParserConfigurationException, SAXException {
        Set<PomModifierCommand> commands = new HashSet<>();
        commands.add(new AddDependenciesCommand(dependencyHandler));
        commands.add(new AppNameCommand(dependencyHandler));
        commands.add(new SetDefaultProfileCommand(deployType));
        commands.add(new SetRepositoryCommand(dependencyHandler));
        com.ibm.liberty.starter.pom.PomModifier delegate = new com.ibm.liberty.starter.pom.PomModifier(this.pomInputStream, commands);
        return delegate.getPomBytes();
    }

}

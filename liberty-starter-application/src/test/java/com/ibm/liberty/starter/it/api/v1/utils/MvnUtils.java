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
package com.ibm.liberty.starter.it.api.v1.utils;

import org.apache.maven.cli.MavenCli;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class MvnUtils {

    public static PrintStream printStreamForFilePath(String filePath) throws FileNotFoundException {
        File logFile = new File(filePath);
        return printStreamForFile(logFile);
    }

    public static PrintStream printStreamForFile(File logFile) throws FileNotFoundException {
        logFile.getParentFile().mkdirs();
        PrintStream outputStream = new PrintStream(new FileOutputStream(logFile));
        return outputStream;
    }

    public static int runMvnCommand(PrintStream outputStream, String tempDir, DownloadedZip zip, String... args) {
        String mvnMultiModuleProjectDirectory = tempDir + "/mvn/multi_module";
        System.setProperty(MavenCli.MULTIMODULE_PROJECT_DIRECTORY, mvnMultiModuleProjectDirectory);
        MavenCli cli = new MavenCli();
        return cli.doMain(args, zip.getLocation(), outputStream, outputStream);
    }

}

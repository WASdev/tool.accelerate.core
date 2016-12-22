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
package com.ibm.liberty.starter.it.api.v1;

import com.ibm.liberty.starter.it.api.v1.utils.DownloadedZip;
import com.ibm.liberty.starter.it.api.v1.utils.MvnUtils;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.PrintStream;

import static com.ibm.liberty.starter.it.api.v1.matchers.FileContainsLines.containsLinesInRelativeOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.io.FileMatchers.anExistingFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class RunnableJarTest {

    private final static String tempDir = System.getProperty("liberty.temp.dir") + "/runnableJarTest";
    private final static String installLog = tempDir + "/mvnLog/runnable_jar_install.log";

    @Rule
    public DownloadedZip downloadedZip = new DownloadedZip(tempDir);

    @Test
    public void testLocalMvnInstallRuns() throws Exception {
        PrintStream logFilePrintStream = MvnUtils.printStreamForFilePath(installLog);
        String pathToOutputJar = downloadedZip.getLocation() + "/target/TestApp.jar";

        int mvnReturnCode = MvnUtils.runMvnCommand(logFilePrintStream, tempDir, downloadedZip, "install", "-P runnable");

        assertEquals(0, mvnReturnCode);
        assertThat(new File(installLog), containsLinesInRelativeOrder(containsString("BUILD SUCCESS")));
        assertThat(new File(pathToOutputJar), is(anExistingFile()));
    }
}

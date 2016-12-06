package com.ibm.liberty.starter.it.api.v1;

import static com.ibm.liberty.starter.it.api.v1.matchers.FileContainsLines.containsLinesInRelativeOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.io.FileMatchers.anExistingFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.PrintStream;

import org.junit.Rule;
import org.junit.Test;

import com.ibm.liberty.starter.it.api.v1.utils.DownloadedZip;
import com.ibm.liberty.starter.it.api.v1.utils.MvnUtils;

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

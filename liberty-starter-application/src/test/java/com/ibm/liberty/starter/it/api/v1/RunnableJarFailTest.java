package com.ibm.liberty.starter.it.api.v1;

import static com.ibm.liberty.starter.it.api.v1.matchers.FileContainsLines.containsLinesInRelativeOrder;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.PrintStream;

import org.junit.Rule;
import org.junit.Test;

import com.ibm.liberty.starter.it.api.v1.utils.DownloadedZip;
import com.ibm.liberty.starter.it.api.v1.utils.MvnUtils;

public class RunnableJarFailTest {

    private final static String tempDir = System.getProperty("liberty.temp.dir") + "/runnableJarFailTest";
    private final static String installLogFail = tempDir + "/mvnLog/runnable_jar_fail_install.log";
    
    @Rule
    public DownloadedZip downloadedZip = new DownloadedZip(tempDir);

    @Test
    public void testLocalMvnInstallFails() throws Exception {
        PrintStream logFilePrintStream = MvnUtils.printStreamForFilePath(installLogFail);

        int mvnReturnCode = MvnUtils.runMvnCommand(logFilePrintStream, tempDir, downloadedZip, "install", "-P runnable", "-Daccept.features.license=false");

        assertThat(new File(installLogFail), containsLinesInRelativeOrder(containsString("Additional features could not be installed ")));
    }
}

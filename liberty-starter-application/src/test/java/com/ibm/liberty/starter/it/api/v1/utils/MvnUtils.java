package com.ibm.liberty.starter.it.api.v1.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.apache.maven.cli.MavenCli;

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

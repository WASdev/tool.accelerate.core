package com.ibm.liberty.starter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipWriter {

    private static final Logger log = Logger.getLogger(ZipWriter.class.getName());
    private final Map<String, byte[]> fileMap;

    public ZipWriter(Map<String, byte[]> fileMap) {
        this.fileMap = fileMap;
    }

    public void buildZip(OutputStream os) throws IOException {
        ZipOutputStream zos = new ZipOutputStream(os);
        createZipFromMap(zos);
        zos.close();
    }

    private void createZipFromMap(ZipOutputStream zos) throws IOException {
        log.log(Level.INFO, "Entering method ProjectConstructor.createZipFromMap()");
        for (Map.Entry<String, byte[]> fileEntry : fileMap.entrySet()) {
            byte[] byteArray = fileEntry.getValue();
            ZipEntry entry = new ZipEntry(fileEntry.getKey());
            entry.setSize(byteArray.length);
            entry.setCompressedSize(-1);
            try {
                zos.putNextEntry(entry);
                zos.write(byteArray);
            } catch (IOException e) {
                throw new IOException(e);
            }
        }
    }
}

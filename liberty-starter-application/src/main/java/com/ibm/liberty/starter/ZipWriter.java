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
import java.io.OutputStream;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipWriter {

    private static final Logger log = Logger.getLogger(ZipWriter.class.getName());
    private final Map<String, byte[]> fileMap;

    public ZipWriter(Map<String, byte[]> fileMap) {
        this.fileMap = new TreeMap<>(fileMap);
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

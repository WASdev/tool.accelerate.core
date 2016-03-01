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
package com.ibm.liberty.starter.unit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Test;

import com.ibm.liberty.starter.ProjectZipConstructor;
import com.ibm.liberty.starter.api.v1.model.internal.Services;

public class ZipConstructorTest {
    
    @Test
    public void testMapInitializer() throws IOException {
        Services services = new Services();
        ProjectZipConstructor zipConstructor = new ProjectZipConstructor(null, services, null, null);
        zipConstructor.initializeMap();
        ConcurrentHashMap<String, byte[]> map = zipConstructor.getFileMap();
        assertFalse(map.isEmpty());
        Enumeration<String> en = map.keys();
        boolean pomExists = false;
        while (en.hasMoreElements()) {
            String path = en.nextElement();
            if ("myProject-application/pom.xml".equals(path)) {
                pomExists = true;
                byte[] byteArray = map.get(path);
                assertTrue(map.toString(), byteArray.length > 0);
            }
        }
        assertTrue(map.toString(), pomExists);
    }

}

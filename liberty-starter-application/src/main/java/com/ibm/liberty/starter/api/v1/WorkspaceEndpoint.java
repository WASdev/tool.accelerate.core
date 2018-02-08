/*******************************************************************************
 * Copyright (c) 2016,2017 IBM Corp.
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
package com.ibm.liberty.starter.api.v1;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;

import com.ibm.liberty.starter.StarterUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("v1/workspace")
@Api(value = "Workspace Identifier")
public class WorkspaceEndpoint {
    
    private static final Logger log = Logger.getLogger(WorkspaceEndpoint.class.getName());
    
    @GET
    @Path("/")
    @Produces(MediaType.TEXT_PLAIN)
    // Swagger annotations
    @ApiOperation(value = "Retrieve a unique workspace identifier", httpMethod = "GET", notes = "Get a unique workspace to store files.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully created a unique workspace identifier") })
    public Response workspace() throws IOException {
        String uuid = StarterUtil.createCleanWorkspace();
        return Response.ok(uuid, MediaType.TEXT_PLAIN).build();
    }
    
    @GET
    @Path("files")
    @Produces("application/zip")
    //Swagger annotations
    @ApiOperation(value = "Retrieve a zip of the content from a directory in the workspace", httpMethod = "GET", notes = "Get zip containing files from the workspace.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully produced a zip of the required workspace files") })
    public Response getFiles(@QueryParam("workspace") String workspaceId, @QueryParam("serviceId") String serviceId, @QueryParam("dir") String dir) throws IOException {
        log.info("GET request for /workspace/files");
        String techWorkspaceDir = StarterUtil.getWorkspaceDir(workspaceId) + "/";
        String filesDir = techWorkspaceDir + "/" + serviceId + "/" + dir;
        File directory = new File(filesDir);
        if(directory.exists() && directory.isDirectory()) {
            IOFileFilter filter;
            if("swagger".equals(serviceId)) {
                filter = FileFilterUtils.notFileFilter(new NameFileFilter(new String[]{"RestApplication.java", "AndroidManifest.xml"}));
            } else {
                filter = FileFilterUtils.trueFileFilter();
            }
            Iterator<File> itr = FileUtils.iterateFilesAndDirs(directory, filter, FileFilterUtils.trueFileFilter());
            StreamingOutput so = (OutputStream os) -> {
                ZipOutputStream zos = new ZipOutputStream(os);
                while(itr.hasNext()) {
                    File file = itr.next();
                    if(file.isFile()) {
                        byte[] byteArray = FileUtils.readFileToByteArray(file);
                        String path = file.getAbsolutePath().replace('\\', '/');
                        int index = path.indexOf(serviceId + "/" + dir);
                        String relativePath = path.substring(index);
                        ZipEntry entry = new ZipEntry(relativePath);
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
                zos.close();
            };
            log.info("Copied files from " + filesDir + " to zip.");
            return Response.ok(so, "application/zip").header("Content-Disposition", "attachment; filename=\"swagger.zip\"").build();
        } else {
            log.severe("File directory doesn't exist : " + filesDir);
            return Response.status(Status.BAD_REQUEST).entity("File directory specified doesn't exist").build();
        }
    }


}

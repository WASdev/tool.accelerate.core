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
package com.ibm.liberty.starter.api.v1.model.provider;

import io.swagger.annotations.ApiModelProperty;

public class Location {
	private String path;
	private String url;
	
	public Location(String path, String url) {
		this.path = path;
		this.url = url;
	}
	
	public Location(String url) {
		this.url = url;
	}
	
	public Location() {
		//JSON serialisation
	}
	
	@ApiModelProperty(value="Relative path to insert the file within the sample project structure", required=false)
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	@ApiModelProperty(value="URL to retrieve the file from", required=true)
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	
}

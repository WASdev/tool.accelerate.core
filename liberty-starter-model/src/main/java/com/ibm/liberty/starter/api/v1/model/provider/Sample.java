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

public class Sample {
	private String base;
	private Location[] locations;
	
	@ApiModelProperty(value="If specified, then this path will be used to create a relative path from any location URLs which do not have a path specified.", required=false)
	public String getBase() {
		return base;
	}
	public void setBase(String base) {
		this.base = base;
	}
	
	@ApiModelProperty(value="Files which make up the sample", required=true)
	public Location[] getLocations() {
		return locations;
	}
	public void setLocations(Location[] locations) {
		this.locations = locations;
	}
	
	
}

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

public class Tag {
	private String name;
	private String value;
	private Attribute[] attributes;
	private Tag[] tags;
	
	public Tag(String name) {
		this.name = name;
	}
	
	public Tag(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	public Tag() {
		//default
	}
	
	@ApiModelProperty(value="Tag name", required=true)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@ApiModelProperty(value="Tag attributes", required=false)
	public Attribute[] getAttributes() {
		return attributes;
	}
	public void setAttributes(Attribute[] attributes) {
		this.attributes = attributes;
	}

	@ApiModelProperty(value="Tag value", required=false)
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Tag[] getTags() {
		return tags;
	}

	public void setTags(Tag[] tags) {
		this.tags = tags;
	}
	
 }

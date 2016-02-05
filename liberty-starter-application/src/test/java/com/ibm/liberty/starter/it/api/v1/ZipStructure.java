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
package com.ibm.liberty.starter.it.api.v1;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ZipStructure {

	private String fileName = "";
	private Map<String, String> fileMap = new HashMap<String, String>();
	
	public ZipStructure(String fileName) {
		this.fileName = fileName;
	}

	public void addFile(String entryName, String entryContents) {
		fileMap.put(entryName, entryContents);
		
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("File name: " + fileName + "\n");
		for (Entry<String, String> entry : fileMap.entrySet()) {
			sb.append("  " + entry.getKey() + ": " + entry.getValue() + "\n");
		}
		return sb.toString();
	}

	public boolean looksLike(Object zipUnderTest) {
		if (this == zipUnderTest)
			return true;
		if (zipUnderTest == null)
			return false;
		if (getClass() != zipUnderTest.getClass())
			return false;
		ZipStructure other = (ZipStructure) zipUnderTest;
		// Check that we have the same size map
		if (fileMap.size() != other.fileMap.size())
			return false;
					
		for (Entry<String, String> entry : fileMap.entrySet()) {
			if (!other.fileMap.containsKey(entry.getKey())) {
				return false;
			}
			
			// Get out clause, if this zip has a null, then we don't do the check.
			// This is the reason this isn't just an equals method
			// Note that the zip under test has to have something
			String otherValue = other.fileMap.get(entry.getKey());
			if (entry.getValue() != null && !entry.getValue().equals(otherValue)) {
				return false;
			}
		}
		return true;
	}
	

	
}

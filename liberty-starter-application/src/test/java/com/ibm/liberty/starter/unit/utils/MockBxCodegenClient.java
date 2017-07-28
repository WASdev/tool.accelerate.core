/*******************************************************************************
 * Copyright (c) 2017 IBM Corp.
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
package com.ibm.liberty.starter.unit.utils;

import java.util.HashMap;
import java.util.Map;

import com.ibm.liberty.starter.client.BxCodegenClient;

public class MockBxCodegenClient extends BxCodegenClient {

    private String status;
    public int statusCount = 0;
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    @Override
    protected String checkStatus(String id) {
        statusCount++;
        return status;
    }
    
    @Override
    protected String callBxCodegen(String payload) {
        return "1234";
    }
    
    @Override
    protected Map<String, byte[]>getProjectMap(String id) {
        Map<String, byte[]> map = new HashMap<String, byte[]>();
        map.put(id, "test".getBytes());
        return map;
    }
    
}

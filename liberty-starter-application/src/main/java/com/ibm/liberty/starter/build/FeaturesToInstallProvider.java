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
package com.ibm.liberty.starter.build;

import com.ibm.liberty.starter.ServiceConnector;
import com.ibm.liberty.starter.api.v1.model.internal.Services;
import com.ibm.liberty.starter.api.v1.model.registration.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class FeaturesToInstallProvider {

    private final List<String> listOfFeatures = new ArrayList<>();
    private static final Logger log = Logger.getLogger(FeaturesToInstallProvider.class.getName());

    public FeaturesToInstallProvider(Services services, ServiceConnector serviceConnector) {
        for (Service service : services.getServices()) {
            String features = serviceConnector.getFeaturesToInstall(service);
            if (features != null && !features.trim().isEmpty() && (features.split(",").length > 0)) {
                for (String feature : features.split(",")) {
                    if (!listOfFeatures.contains(feature)) {
                        listOfFeatures.add(feature);
                        log.finer("Added feature : " + feature);
                    }
                }
            }
        }
    }

    public List<String> getFeatures() {
        return listOfFeatures;
    }
}

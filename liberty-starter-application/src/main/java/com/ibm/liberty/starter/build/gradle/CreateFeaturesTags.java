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
package com.ibm.liberty.starter.build.gradle;

import com.ibm.liberty.starter.build.FeaturesToInstallProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CreateFeaturesTags {
    private final List<String> featuresToInstall;

    public CreateFeaturesTags(FeaturesToInstallProvider featuresToInstallProvider) {
        featuresToInstall = featuresToInstallProvider.getFeatures();
    }

    public Map<String, String> getTags() {
        Map<String, String> tags = new HashMap<>();
        if (!featuresToInstall.isEmpty()) {
            addFeatureInstallConfig(tags);
            addTaskSetupTag(tags);
        }
        return tags;
    }

    private void addFeatureInstallConfig(Map<String, String> tags) {
        tags.put("LIBERTY_FEATURE_CONFIG", "    features {\n" +
                        "        name = [" + featuresAsCommasSeparatedList() + "]\n" +
                        "        acceptLicense = true\n" +
                        "    }");
    }

    private String featuresAsCommasSeparatedList() {
        return featuresToInstall.stream().map(feature -> "'" + feature + "'").collect(Collectors.joining(", "));
    }

    private void addTaskSetupTag(Map<String, String> tags) {
        tags.put("LIBERTY_FEATURE_TASK_SETUP", "installFeature.dependsOn 'installLiberty'\n" +
                "setupServer.dependsOn 'installFeature'");
    }
}

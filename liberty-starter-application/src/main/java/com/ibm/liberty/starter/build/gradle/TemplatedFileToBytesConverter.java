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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TemplatedFileToBytesConverter {

    private final Pattern tagPattern = Pattern.compile("(%(.*)%)");
    private final InputStream inputStream;
    private final Map<String, String> tags;

    public TemplatedFileToBytesConverter(InputStream inputStream, Map<String, String> tags) {
        this.inputStream = inputStream;
        this.tags = tags;
    }

    public byte[] getBytes() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        return reader.lines()
                .map(this::replaceTags)
                .filter(Objects::nonNull)
                .collect(Collectors.joining("\n")).getBytes();
    }

    private String replaceTags(String line) {
        Matcher matcher = tagPattern.matcher(line);
        if (matcher.find()) {
            String lineWithTagsReplaced = replaceFoundTag(matcher);
            return nullIfEmpty(lineWithTagsReplaced);
        }
        return line;
    }

    private String nullIfEmpty(String lineWithTagsReplaced) {
        if (lineWithTagsReplaced.trim().isEmpty()) {
            return null;
        }
        return lineWithTagsReplaced;
    }

    private String replaceFoundTag(Matcher matcher) {
        String key = matcher.group(2);
        String replacement;
        if (tags.containsKey(key)) {
            replacement = tags.get(key);
        } else {
            replacement = "";
        }
        return matcher.replaceAll(replacement);
    }
}

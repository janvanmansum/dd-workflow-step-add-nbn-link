/*
 * Copyright (C) 2022 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.knaw.dans.wf.nbnlink.core;

import com.jayway.jsonpath.JsonPath;

import java.util.List;

import static com.jayway.jsonpath.JsonPath.compile;
import static com.jayway.jsonpath.JsonPath.parse;

public class NbnLinkFinder {
    private static final JsonPath descriptionValues = compile("$..metadataBlocks.citation.fields[?(@.typeName == 'dsDescription')].value[*].dsDescriptionValue.value");
    private final String metadata;
    private final String marker;

    public NbnLinkFinder(String metadata, String marker) {
        this.metadata = metadata;
        this.marker = marker;
    }

    public boolean isNbnLinkAdded() {
        List<?> values = parse(metadata).read(descriptionValues, List.class);
        return values.stream().anyMatch(v -> ((String) v).trim().endsWith(marker));
    }

}

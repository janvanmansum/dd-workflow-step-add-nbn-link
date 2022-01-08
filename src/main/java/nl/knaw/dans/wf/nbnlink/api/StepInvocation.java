/*
 * Copyright (C) 2021 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
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

package nl.knaw.dans.wf.nbnlink.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;

// TODO: candidate for dans-dataverse-client-lib?
/**
 * The message received from Dataverse that invokes an external workflow step.
 */
public class StepInvocation {

    @NotEmpty
    private final String invocationId;

    @NotEmpty
    private final String globalId;

    @NotEmpty
    private final String datasetId;

    @NotEmpty
    private final String majorVersion;

    @NotEmpty
    private final String minorVersion;

    @JsonCreator
    public StepInvocation(@JsonProperty("invocationId") String invocationId, @JsonProperty("globalId") String globalId, @JsonProperty("datasetId") String datasetId,
                          @JsonProperty("majorVersion") String majorVersion, @JsonProperty("minorVersion") String minorVersion) {
        this.invocationId = invocationId; this.globalId = globalId; this.datasetId = datasetId; this.majorVersion = majorVersion; this.minorVersion = minorVersion;
    }

    @Override
    public String toString() {
        return "InvocationMessage{" + "invocationId='" + invocationId + '\'' + ", globalId='" + globalId + '\'' + ", datasetId='" + datasetId + '\'' + ", majorVersion='" + majorVersion + '\''
            + ", minorVersion='" + minorVersion + '\'' + '}';
    }

    @JsonProperty
    public String getMinorVersion() {
        return minorVersion;
    }

    @JsonProperty
    public String getMajorVersion() {
        return majorVersion;
    }

    @JsonProperty
    public String getDatasetId() {
        return datasetId;
    }

    @JsonProperty
    public String getGlobalId() {
        return globalId;
    }

    @JsonProperty
    public String getInvocationId() {
        return invocationId;
    }
}

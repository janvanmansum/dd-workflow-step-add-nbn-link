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

package nl.knaw.dans.wf.nbnlink;

import io.dropwizard.Configuration;
import nl.knaw.dans.lib.util.DataverseClientFactory;
import nl.knaw.dans.lib.util.ExecutorServiceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class DdWorkflowStepAddNbnLinkConfiguration extends Configuration {
    @NotNull
    @Valid
    private DataverseClientFactory dataverse;

    @NotNull
    @Valid
    private ExecutorServiceFactory taskQueue;

    public DataverseClientFactory getDataverse() {
        return dataverse;
    }

    public void setDataverse(DataverseClientFactory dataverse) {
        this.dataverse = dataverse;
    }

    public void setTaskQueue(ExecutorServiceFactory taskExecutorThreadPool) {
        this.taskQueue = taskExecutorThreadPool;
    }

    public ExecutorServiceFactory getTaskQueue() {
        return taskQueue;
    }
}
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

import io.dropwizard.setup.Environment;
import nl.knaw.dans.lib.dataverse.DataverseClient;
import nl.knaw.dans.lib.util.ExecutorServiceFactory;
import nl.knaw.dans.wf.nbnlink.core.Resumer;

public class ResumerFactory {
    private RetryPolicy retryPolicy;
    private ExecutorServiceFactory resumeQueue;

    public Resumer build(DataverseClient dataverseClient, Environment environment) {
        return new Resumer(dataverseClient, retryPolicy, resumeQueue.build(environment));
    }

    public RetryPolicy getRetryPolicy() {
        return retryPolicy;
    }

    public void setRetryPolicy(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
    }

    public ExecutorServiceFactory getResumeQueue() {
        return resumeQueue;
    }

    public void setResumeQueue(ExecutorServiceFactory resumeQueue) {
        this.resumeQueue = resumeQueue;
    }
}

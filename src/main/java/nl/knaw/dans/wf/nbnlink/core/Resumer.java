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

import nl.knaw.dans.lib.dataverse.DataverseClient;
import nl.knaw.dans.lib.dataverse.DataverseException;
import nl.knaw.dans.lib.dataverse.model.workflow.ResumeMessage;
import nl.knaw.dans.wf.nbnlink.RetryPolicy;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Executor;

import static java.lang.Thread.sleep;

// TODO: MOVE TO dans-java-utils or dans-dataverse-client-libs
public class Resumer {
    private static final Logger log = LoggerFactory.getLogger(Resumer.class);

    enum Status {
        Success,
        Failure
    };

    private final DataverseClient dataverseClient;
    private final RetryPolicy retryPolicy;
    private final Executor resumeExecutor;

    public Resumer(DataverseClient dataverseClient, RetryPolicy retryPolicy, Executor resumeExecutor) {
        this.dataverseClient = dataverseClient;
        this.retryPolicy = retryPolicy;
        this.resumeExecutor = resumeExecutor;
    }

    public void executeResume(String invocationId, Status status) {
        executeResume(invocationId, status, "", "");
    }

    public void executeResume(String invocationId, Status status, String reason, String message) {
        resumeExecutor.execute(new Runnable() {
            @Override
            public void run() {
                log.trace("run");
                boolean success = false;

                for (int i = 0; i < retryPolicy.getMaxTries(); ++i) {
                    log.debug("Trying to resume, attempt nr {} of {} times", i + 1, retryPolicy.getMaxTries());
                    success = tryResume();

                    if (success)
                        return;
                    log.warn("Resume failed. Tried {} of {} times", i + 1, retryPolicy.getMaxTries());
                    try {
                        sleep(retryPolicy.getTimeBetweenTries().toMilliseconds());
                    } catch (InterruptedException e) {
                        log.warn("Sleep got interrupted", e);
                    }
                }
                log.warn("Could not resume invocation {} after maximum number of {} tries", invocationId, retryPolicy.getMaxTries());
            }

            private boolean tryResume() {
                try {
                    dataverseClient.workflows().resume(invocationId, new ResumeMessage(status.name(), reason, message));
                    return true;
                } catch (DataverseException e) {
                    if (e.getStatus() == HttpStatus.SC_NOT_FOUND) return false;
                    throw new IllegalStateException("Failed to call resume for invocation " + invocationId, e);
                } catch (IOException e) {
                    throw new IllegalStateException("Failed to call resume for invocation " + invocationId, e);
                }
            }
        });
    }
}



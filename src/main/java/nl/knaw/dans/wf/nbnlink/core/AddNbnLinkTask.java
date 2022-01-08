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
import nl.knaw.dans.wf.nbnlink.api.StepInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddNbnLinkTask implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(AddNbnLinkTask.class);

    private final StepInvocation stepInvocation;
    private final DataverseClient dataverseClient;
    private final Resumer resumer;

    public AddNbnLinkTask(StepInvocation stepInvocation, DataverseClient dataverseClient, Resumer resumer) {
        this.stepInvocation = stepInvocation;
        this.dataverseClient = dataverseClient;
        this.resumer = resumer;
    }

    @Override
    public String toString() {
        return "AddNbnLinkTask{" +
                "stepInvocation=" + stepInvocation +
                '}';
    }

    @Override
    public void run() {
        log.trace("run");
        /*  TODO:
            1. Retrieve the metadata of the draft version.
            2. Get the NBN from it
            3. Remove any description element with the marker in it
            4. Build a persistent identifier link
            5. Add a new description element based on the template and the persistent identifier link
        */
        resumer.executeResume(stepInvocation.getInvocationId(), dataverseClient);
        log.debug("Scheduled resume of invocation {}", stepInvocation.getInvocationId());
    }
}

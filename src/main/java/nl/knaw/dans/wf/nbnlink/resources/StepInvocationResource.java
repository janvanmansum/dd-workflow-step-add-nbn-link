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
package nl.knaw.dans.wf.nbnlink.resources;

import nl.knaw.dans.lib.dataverse.DataverseClient;
import nl.knaw.dans.wf.nbnlink.api.StepInvocation;
import nl.knaw.dans.wf.nbnlink.core.AddNbnLinkTask;
import nl.knaw.dans.wf.nbnlink.core.NbnLinkCreator;
import nl.knaw.dans.wf.nbnlink.core.Resumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.concurrent.Executor;

@Path("/invoke")
@Produces(MediaType.APPLICATION_JSON)
public class StepInvocationResource {

    private static final Logger log = LoggerFactory.getLogger(StepInvocationResource.class);

    private final Executor taskExecutor;
    private final NbnLinkCreator nbnLinkCreator;
    private final Resumer resumer;
    private final DataverseClient dataverseClient;

    public StepInvocationResource(Executor taskExecutor, NbnLinkCreator nbnLinkCreator, Resumer resumer, DataverseClient dataverseClient) {
        this.taskExecutor = taskExecutor;
        this.nbnLinkCreator = nbnLinkCreator;
        this.resumer = resumer;
        this.dataverseClient = dataverseClient;
    }

    @POST
    public void run(@Valid StepInvocation inv) throws IOException {
        log.info("Received invocation: {}", inv);
        taskExecutor.execute(new AddNbnLinkTask(inv, dataverseClient, nbnLinkCreator, resumer));
        log.info("Added new task to queue");
    }

}

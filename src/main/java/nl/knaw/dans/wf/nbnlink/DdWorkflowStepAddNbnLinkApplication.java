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

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import nl.knaw.dans.lib.dataverse.DataverseClient;
import nl.knaw.dans.wf.nbnlink.resources.StepInvocationResource;
import nl.knaw.dans.wf.nbnlink.resources.StepRollbackResource;

import java.util.concurrent.ExecutorService;

public class DdWorkflowStepAddNbnLinkApplication extends Application<DdWorkflowStepAddNbnLinkConfiguration> {

    public static void main(final String[] args) throws Exception {
        new DdWorkflowStepAddNbnLinkApplication().run(args);
    }

    @Override
    public String getName() {
        return "Dd Workflow Step Add Nbn Link";
    }

    @Override
    public void initialize(final Bootstrap<DdWorkflowStepAddNbnLinkConfiguration> bootstrap) {
    }

    @Override
    public void run(final DdWorkflowStepAddNbnLinkConfiguration configuration, final Environment environment) {
        final ExecutorService executorService = configuration.getTaskQueue().build(environment);
        DataverseClient client = configuration.getDataverse().build();
        environment.jersey().register(new StepInvocationResource(executorService, client));
        environment.jersey().register(new StepRollbackResource(executorService, client));
    }

}
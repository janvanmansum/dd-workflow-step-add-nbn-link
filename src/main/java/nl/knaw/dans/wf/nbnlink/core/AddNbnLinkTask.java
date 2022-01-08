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
import nl.knaw.dans.lib.dataverse.DataverseResponse;
import nl.knaw.dans.lib.dataverse.model.dataset.DatasetVersion;
import nl.knaw.dans.lib.dataverse.model.dataset.MetadataBlock;
import nl.knaw.dans.lib.dataverse.model.dataset.PrimitiveSingleValueField;
import nl.knaw.dans.wf.nbnlink.api.StepInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        Map<String, MetadataBlock> metadata = getMetadata();

        MetadataBlock vaultMetadata = metadata.get("dansDataVaultMetadata");
        if (vaultMetadata == null) {
            throw new IllegalStateException("No vault metadata set in dataset" + stepInvocation.getGlobalId());
        }
        String nbn = getNbn(vaultMetadata);
        /*  TODO:
            3. Remove any description element with the marker in it
            4. Build a persistent identifier link
            5. Add a new description element based on the template and the persistent identifier link
        */

        log.debug("Found nbn = {}", nbn);


        resumer.executeResume(stepInvocation.getInvocationId(), dataverseClient);
        log.debug("Scheduled resume of invocation {}", stepInvocation.getInvocationId());
    }

    private Map<String, MetadataBlock> getMetadata() {
        try {
            DataverseResponse<DatasetVersion> r = dataverseClient.dataset(stepInvocation.getGlobalId()).getVersion(":draft");
            return r.getData().getMetadataBlocks();
        } catch (IOException | DataverseException e) {
            throw new IllegalStateException("Could not retrieve metadata for dataset " + stepInvocation.getGlobalId(), e);
        }

    }

    private String getNbn(MetadataBlock vaultMetadata) {
        List<String> nbns = vaultMetadata.getFields()
                .stream()
                .filter(f -> "dansNbn".equals(f.getTypeName()))
                .map(f -> (PrimitiveSingleValueField) f)
                .map(PrimitiveSingleValueField::getValue)
                .collect(Collectors.toList());
        if (nbns.isEmpty())
            throw new IllegalStateException("Cannot find NBN for dataset " + stepInvocation.getGlobalId());
        if (nbns.size() > 1)
            throw new IllegalStateException("Multiple NBNs found for dataset " + stepInvocation.getGlobalId());
        return nbns.get(0);
    }


}

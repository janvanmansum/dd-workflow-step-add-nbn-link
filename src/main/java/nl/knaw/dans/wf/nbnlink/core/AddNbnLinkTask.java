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

import nl.knaw.dans.lib.dataverse.CompoundFieldBuilder;
import nl.knaw.dans.lib.dataverse.DatasetApi;
import nl.knaw.dans.lib.dataverse.DataverseClient;
import nl.knaw.dans.lib.dataverse.DataverseException;
import nl.knaw.dans.lib.dataverse.DataverseResponse;
import nl.knaw.dans.lib.dataverse.model.dataset.DatasetVersion;
import nl.knaw.dans.lib.dataverse.model.dataset.FieldList;
import nl.knaw.dans.lib.dataverse.model.dataset.MetadataBlock;
import nl.knaw.dans.lib.dataverse.model.dataset.PrimitiveSingleValueField;
import nl.knaw.dans.wf.nbnlink.api.StepInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class AddNbnLinkTask implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(AddNbnLinkTask.class);
    private static final String BLOCK_NAME_DATAVAULT_METADATA = "dansDataVaultMetadata";

    private final StepInvocation stepInvocation;
    private final DataverseClient dataverseClient;
    private final NbnLinkCreator nbnLinkCreator;
    private final Resumer resumer;

    public AddNbnLinkTask(StepInvocation stepInvocation, DataverseClient dataverseClient, NbnLinkCreator nbnLinkCreator, Resumer resumer) {
        this.stepInvocation = stepInvocation;
        this.dataverseClient = dataverseClient;
        this.nbnLinkCreator = nbnLinkCreator;
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
        try {
            DatasetApi datasetApi = dataverseClient.dataset(stepInvocation.getGlobalId(), stepInvocation.getInvocationId());
            DataverseResponse<DatasetVersion> draft = getDraftVersion(datasetApi);

            /*
             * Even though it seems Dataverse will not add multiple descriptions that are exactly equal, we check if the NBN link
             * is already present. If so, we take the cautious road and skip adding it.
             */
            if (new NbnLinkFinder(draft.getEnvelopeAsString(), nbnLinkCreator.getMarker()).isNbnLinkAdded()) {
                log.debug("NBN link already present, skipping");
            }
            else {
                MetadataBlock vaultMetadata = getVaultMetadata(draft);
                if (vaultMetadata == null) {
                    throw new IllegalStateException("No vault metadata set in dataset" + stepInvocation.getGlobalId());
                }
                String nbn = getNbn(vaultMetadata);
                log.debug("Found nbn = {}", nbn);
                FieldList fieldList = createNbnLinkDescription(nbn);
                datasetApi.editMetadata(fieldList, false);
            }
            resumer.executeResume(stepInvocation.getInvocationId(), Resumer.Status.Success);
            log.debug("Scheduled resume of invocation {}", stepInvocation.getInvocationId());
        }
        catch (DataverseException | IOException | IllegalStateException e) {
            log.warn("Could not add NBN link. Failing PrePublishDataset Workflow", e);
            resumer.executeResume(stepInvocation.getInvocationId(), Resumer.Status.Failure, "Could not add NBN link", "Pre-publication workflow returned an error");
        }
    }

    private DataverseResponse<DatasetVersion> getDraftVersion(DatasetApi datasetApi) {
        try {
            return datasetApi.getVersion(":draft");
        }
        catch (IOException | DataverseException e) {
            throw new IllegalStateException("Could not retrieve metadata for dataset " + stepInvocation.getGlobalId(), e);
        }
    }

    private MetadataBlock getVaultMetadata(DataverseResponse<DatasetVersion> versionResponse) {
        try {
            return versionResponse.getData().getMetadataBlocks().get(BLOCK_NAME_DATAVAULT_METADATA);
        }
        catch (IOException e) {
            throw new IllegalStateException("Could not read dataset version response", e);
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

    private FieldList createNbnLinkDescription(String nbn) {
        FieldList fieldList = new FieldList();
        fieldList.add(new CompoundFieldBuilder("dsDescription", true)
            .addSubfield("dsDescriptionValue", nbnLinkCreator.create(nbn))
            .addSubfield("dsDescriptionDate", "")
            .build());
        return fieldList;
    }

}

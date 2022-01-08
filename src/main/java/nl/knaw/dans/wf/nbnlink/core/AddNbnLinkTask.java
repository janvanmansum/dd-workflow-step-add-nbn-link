package nl.knaw.dans.wf.nbnlink.core;

import nl.knaw.dans.lib.dataverse.DataverseClient;
import nl.knaw.dans.wf.nbnlink.api.StepInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddNbnLinkTask implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(AddNbnLinkTask.class);

    private final StepInvocation stepInvocation;
    private final DataverseClient dataverseClient;

    public AddNbnLinkTask(StepInvocation stepInvocation, DataverseClient dataverseClient) {
        this.stepInvocation = stepInvocation;
        this.dataverseClient = dataverseClient;
    }

    @Override
    public String toString() {
        return "AddNbnLinkTask{" +
                "stepInvocation=" + stepInvocation +
                '}';
    }

    @Override
    public void run() {
        log.trace("ENTER");
        /*  TODO:
            1. Retrieve the metadata of the draft version.
            2. Get the NBN from it
            3. Remove any description element with the marker in it
            4. Build a persistent identifier link
            5. Add a new description element based on the template and the persistent identifier link
        */
    }
}

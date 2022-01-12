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

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NbnLinkFinderTest {

    @Test
    public void canFindLinkIfPresent() throws Exception {
        final String metadata = FileUtils.readFileToString(new File("src/test/resources/testinput/DatasetVersionWithNbnLink.json"), StandardCharsets.UTF_8);
        final String marker = "<input type=\"hidden\" value=\"NBN\"></input>";
        assertTrue(new NbnLinkFinder(metadata, marker).isNbnLinkAdded());
    }

    @Test
    public void canFindLinkIfPresentTrailingWsIgnored() throws Exception {
        final String metadata = FileUtils.readFileToString(new File("src/test/resources/testinput/DatasetVersionWithNbnLinkTrailingWs.json"), StandardCharsets.UTF_8);
        final String marker = "<input type=\"hidden\" value=\"NBN\"></input>";
        assertTrue(new NbnLinkFinder(metadata, marker).isNbnLinkAdded());
    }


    @Test
    public void cannotFindLinkIfAbsent() throws Exception {
        final String metadata = FileUtils.readFileToString(new File("src/test/resources/testinput/DatasetVersionWithoutNbnLink.json"), StandardCharsets.UTF_8);
        final String marker = "<input type=\"hidden\" value=\"NBN\"></input>";
        assertFalse(new NbnLinkFinder(metadata, marker).isNbnLinkAdded());
    }
}

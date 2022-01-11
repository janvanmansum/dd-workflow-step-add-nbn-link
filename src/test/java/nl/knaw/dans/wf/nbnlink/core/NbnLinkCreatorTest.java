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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NbnLinkCreatorTest {

    @Test
    public void createsLinkFromValidInput() throws Exception {
        final String baseUrl = "http://www.persistent-identifier.nl?identifier=";
        final String nbn = "urn:nbn:nl:ui:13-blah-blah";
        final String marker = "<input type=\"hidden\" value=\"NBN\"></input>";

        NbnLinkCreator creator = new NbnLinkCreator(
            baseUrl,
            marker,
            "A copy of this dataset is stored in EASY, the DANS CTS certified repository, at %s");

        String description = creator.create(nbn);
        assertEquals("A copy of this dataset is stored in EASY, the DANS CTS certified repository, at <a href=\"" + baseUrl + nbn + "\">" + nbn + "</a>" + marker, description);
    }

}

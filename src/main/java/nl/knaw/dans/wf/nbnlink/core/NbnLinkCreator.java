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

import java.net.URI;

/**
 * Creates the description element text based on an NBN value.
 */
public class NbnLinkCreator {

    private final String baseUrl;
    private final String marker;
    private final String descriptionTemplate;

    public NbnLinkCreator(String baseUrl, String marker, String descriptionTemplate) {
        this.baseUrl = baseUrl;
        this.marker = marker;
        this.descriptionTemplate = descriptionTemplate;
    }

    public String create(String nbn) {
        String href = baseUrl + nbn;
        String link = String.format("<a href=\"%s\">%s</a>", href, nbn);
        return String.format(descriptionTemplate + marker, link);
    }
}

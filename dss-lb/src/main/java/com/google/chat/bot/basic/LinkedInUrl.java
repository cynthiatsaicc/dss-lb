/*
 * Copyright (c) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.chat.bot.basic;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.util.Key;

/**
 * URL for Dailymotion API.
 *
 * @author
 */
public class LinkedInUrl extends GenericUrl {

    @Key
    private String fields;

    public LinkedInUrl(String encodedUrl) {
        super(encodedUrl);
    }

    /**
     * @return the fields
     */
    public String getFields() {
        return fields;
    }

    /**
     * @param fields the fields to set
     */
    public void setFields(String fields) {
        this.fields = fields;
    }
}


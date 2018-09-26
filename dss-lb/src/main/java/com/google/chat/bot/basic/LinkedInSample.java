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

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import java.io.IOException;
import java.util.Arrays;


/**
 * A sample application that demonstrates how the Google OAuth2 library can be used to authenticate
 * against Daily Motion.
 *
 * @author Ravi Mistry
 */
public class LinkedInSample {

    /** Directory to store user credentials. */
    private static final java.io.File DATA_STORE_DIR =
            new java.io.File(System.getProperty("user.home"), ".store/linkedin_sample");

    /**
     * Global instance of the {@link DataStoreFactory}. The best practice is to make it a single
     * globally shared instance across your application.
     */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** OAuth 2 scope. */
    private static final String SCOPE = "read";

    /** Global instance of the HTTP transport. */
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    /** Global instance of the JSON factory. */
    static final JsonFactory JSON_FACTORY = new JacksonFactory();

    private static final String TOKEN_SERVER_URL = "https://www.linkedin.com/oauth/v2/accessToken";
    private static final String AUTHORIZATION_SERVER_URL =
            "https://www.linkedin.com/oauth/v2/authorization";

    /** Authorizes the installed application to access user's protected data. */
    private static Credential authorize() throws Exception {
        System.out.println("starting lis authorize");
        OAuth2ClientCredentials.errorIfNotSpecified();
        // set up authorization code flow
        AuthorizationCodeFlow flow = new AuthorizationCodeFlow.Builder(BearerToken
                .authorizationHeaderAccessMethod(),
                HTTP_TRANSPORT,
                JSON_FACTORY,
                new GenericUrl(TOKEN_SERVER_URL),
                new ClientParametersAuthentication(
                        OAuth2ClientCredentials.API_KEY, OAuth2ClientCredentials.API_SECRET),
                OAuth2ClientCredentials.API_KEY,
                AUTHORIZATION_SERVER_URL).setScopes(Arrays.asList(SCOPE))
                .setDataStoreFactory(DATA_STORE_FACTORY).build();
        // authorize
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setHost(
                OAuth2ClientCredentials.DOMAIN).setPort(OAuth2ClientCredentials.PORT).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    private static void run(HttpRequestFactory requestFactory) throws IOException {
        System.out.println("starting lis run");
        LinkedInUrl url = new LinkedInUrl("https://api.linkedin.com/v1/people/~?format=json");
        url.setFields("firstName, headline, id, lastName, siteStandardProfileRequest");

        HttpRequest request = requestFactory.buildGetRequest(url);
        PersonFeed personFeed = request.execute().parseAs(PersonFeed.class);
        System.out.println("Preparing request");
        if (personFeed.list.isEmpty()) {
            System.out.println("No people found.");
        } else {
            if (personFeed.hasMore) {
                System.out.print("First ");
            }
            System.out.println(personFeed.list.size() + " people found");
            for (Person person : personFeed.list) {
                System.out.println();
                System.out.println("-----------------------------------------------");
                System.out.println("ID: " + person.id);
                System.out.println("First Name: " + person.firstName);
                System.out.println("Last Name: " + person.lastName);
                System.out.println("Headline: " + person.headline);
            }
        }
    }

    public static void authMain(java.lang.String args) {
        System.out.println("starting lis main");
        try {
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
            final Credential credential = authorize();
            HttpRequestFactory requestFactory =
                    HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
                        @Override
                        public void initialize(HttpRequest request) throws IOException {
                            credential.initialize(request);
                            request.setParser(new JsonObjectParser(JSON_FACTORY));
                        }
                    });
            run(requestFactory);
            System.out.println("Success!");
            return;
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        System.exit(1);
    }
}
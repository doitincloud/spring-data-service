/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * History:
 *   created by Sam Wen @ 08/17/2017
 */

package com.doitincloud.client;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.doitincloud.library.helper.HalRest;
import com.doitincloud.library.helper.Utils;
import org.springframework.http.ResponseEntity;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

// A simple text based HAL browser for demonstration purpose
//
@SpringBootApplication
public class Application {

    public static void main(String[] args) {

        // to keep browser history
        LinkedList<String[]> history = new LinkedList<String[]>();
        int index = -1; // current position

        // default start
        String urlName = "start";
        String url = "http://localhost:8080/ds/v1";

        // Use the helper class
        HalRest access = new HalRest();

        // get user input for starting url
        System.out.print("\nEnter base url (" + url + "): ");
        System.out.flush();
        String input = System.console().readLine();
        input = input.replaceAll("\\s+","");
        if (input.length() > 0) url = input;

        while (true) {

            ResponseEntity response = null;
            try {
                response = access.get(url);

            } catch (Exception e) {

                System.out.println("Failed to connect to "+url);
                return;

            }

            System.out.println(Utils.toPrettyJson(response));

            if (!response.getStatusCode().toString().equals("200")) {
                System.out.println("Failed to get response!");
                return;
            }

            // save to history, except in back and forward mode
            if (!input.equals("B") && !input.equals("F")) {
                String[] entry = { urlName, url };
                history.add(entry);
                index = history.size() - 1;
            }

            // summary of the response for demonstration purpose
            //
            System.out.println("\n****************************************************************\n");
            System.out.println("Url: "+url);

            Map body = (Map) response.getBody();
            Map page = (Map) access.getMetadata(body);
            Map profile = (Map) access.getProfile(body);

            if (null == page) {
                Map content = (Map) access.getContent(body);
                if (null == content) {
                    if (null == profile) {
                        System.out.println("Type: index");
                    } else {
                        System.out.println("Type: profile");
                    }
                } else {
                    System.out.print("Type: single item - ");
                    System.out.println(content.size() + (content.size() > 1 ? " attributes" : " attribute"));
                }
            } else {
                Collection<Map> content = (Collection<Map>) access.getContent(body);
                System.out.print("Type: multiple items - ");
                System.out.println(content.size() + (content.size() > 1 ? " items" : " item"));
            }

            Map links = (Map) access.getLinks(body);
            if (null != links) {
                System.out.println((links.size() > 1 ? "Links: " : "Link: ") + links.size());
                if (links.size() == 0) return;
            } else if (null != profile) {
                String version = access.getProfileVersion(profile);
                System.out.print("Version: "+version);
                Collection<Map> descriptors = access.getProfileDescriptors(profile);
                System.out.println(" - "+descriptors.size()+(descriptors.size() > 1 ? " descriptors" : " descriptor"));
            } else {
                System.out.println("unknown scenario!");
                return;
            }

            // print out possible links to go
            //
            System.out.println("\n----------------------------------------------------------------\n");

            String[] names = null, hrefs = null;
            int i = 0;
            System.out.println(i + ": EXIT");
            if (null != links) {

                names = new String[links.size()];
                hrefs = new String[links.size()];
                Iterator<Map.Entry<String, Object>> iterator = links.entrySet().iterator();
                while (iterator.hasNext()) {
                    i++;
                    Map.Entry<String, Object> entry = (Map.Entry<String, Object>) iterator.next();
                    String name = entry.getKey();
                    String href = ((Map<String, String>) entry.getValue()).get("href");
                    String[] items = href.split("\\{\\?");
                    if (items.length == 1) {
                        items = href.split("\\{\\&");
                    }
                    names[i - 1] = name;
                    hrefs[i - 1] = items[0];
                    System.out.println(i + ": " + name + " => " + items[0]);
                }
            }

            if (index > 0 && history.size() > 1) {
                System.out.print("B: BACK    ");
            }
            if (index >= 0 && index < history.size() - 1 ) {
                System.out.println("F: FORWARD");
            } else {
                System.out.println();
            }
            System.out.println();

            System.out.print("Your selection (0): ");
            System.out.flush();
            input = System.console().readLine();
            input = input.replaceAll("\\s+", "");
            if (input.length() == 0) return;

            // handle backward selection
            //
            if (input.equals("B")) {
                if (index <= 0) {
                    System.out.println("No more history to back");
                    return;
                }
                index--;
                String[] entry = history.get(index);
                if (null == entry) {
                    System.out.println("Failed to go back");
                    return;
                }
                urlName = entry[0];
                url = entry[1];
                continue;
            }

            // handle forward selection
            //
            if (input.equals("F")) {
                if (index < 0 || index == history.size() - 1) {
                    System.out.println("No more history to forward");
                    return;
                }
                index++;
                String[] entry = history.get(index);
                if (null == entry) {
                    System.out.println("Failed to go forward");
                    return;
                }
                urlName = entry[0];
                url = entry[1];
                continue;
            }

            // not in back or forward mode, remove all after index
            //
            int index2 = index + 1;
            while (history.size() > index2) {
                history.remove(index2);
            }

            // handle number selection
            //
            try {
                int selection = Integer.parseInt(input);
                if (selection == 0 || selection > i) {
                    System.out.println("Selection is out of range");
                    return;
                }
                if (null != hrefs) {
                    selection--;
                    urlName = names[selection];
                    url = hrefs[selection];
                } else {
                    System.out.println("Failed to go the url");
                    return;
                }
            } catch (Exception e) {
                System.out.println("Failed to handle input");
                return;
            }
        }

    }
}

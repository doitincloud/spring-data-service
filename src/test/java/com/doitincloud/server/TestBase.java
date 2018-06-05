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

package com.doitincloud.server;

import com.doitincloud.library.helper.HalRest;
import com.doitincloud.library.helper.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.TypeConstrainedMappingJackson2HttpMessageConverter;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestBase extends HalRest {

    @Value("${local.server.port}")
    protected int port;

    protected String apiBaseUrl;

    public void setup() {

        // to remove timezone side effect
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        apiBaseUrl = String.format("http://localhost:%d/ds/v1", port);
    }

    public void VerifyApiUrl(String url) {

        System.out.println("\n****** verify api url ****************************************************************\n");

        System.out.println("url = "+url);

        ResponseEntity response = get(url);

        System.out.println("response status code = "+response.getStatusCode().toString());
        assertThat(response.getStatusCode().toString()).isEqualTo("200");

        Map body = (Map) response.getBody();

        assertThat(body.containsKey("_embedded")).isTrue();
        assertThat(body.containsKey("_links")).isTrue();
        assertThat(body.containsKey("page")).isTrue();
    }

    public <T> void readToVerifyTheRecord(String url, T t) {

        System.out.println("\n****** read to verify the record *****************************************************\n");

        System.out.println("url = "+url);

        ResponseEntity response = get(url);

        System.out.println("response status code = "+response.getStatusCode().toString());
        assertThat(response.getStatusCode().toString()).isEqualTo("200");

        Map body = (Map) response.getBody();
        Map content = (Map) getContent(body);
        Map tMap = Utils.toMap(t);

        assertThat(Utils.ifMapsEqual(content, tMap)).isTrue();
    }

    public void readToVerifyTheDelete(String url) {

        System.out.println("\n****** read to verify the delete *****************************************************\n");

        System.out.println("url = "+url);

        try {
            ResponseEntity response = get(url);

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isNotEqualTo("200");

        } catch (HttpClientErrorException e) {

            System.out.println("response status code = "+e.getStatusCode().toString());
            assertThat(e.getStatusCode().toString()).isEqualTo("404");
        }
    }

    public void FollowLinks(List<Link> links, String name) {

        String prevHref = null, nextHref = null;

        {
            System.out.println("\n****** visit links except self ***************************************************\n");

            Iterator<Link> iterator = links.iterator();
            while (iterator.hasNext()) {
                Link link = iterator.next();
                if (link.getRel().equals("self")) continue;  // visited
                if (link.getRel().equals(name)) continue;    // visited
                if (link.getRel().equals("next")) {
                    nextHref = link.getHref();
                    continue; // do it after
                }
                if (link.getRel().equals("prev")) {
                    prevHref = link.getHref();
                    continue; // do it after
                }

                System.out.println("rel: "+link.getRel()+" href: "+link.getHref());

                ResponseEntity response = get(link.getHref());

                System.out.println("response status code = "+response.getStatusCode().toString());
                assertThat(response.getStatusCode().toString()).isEqualTo("200");
            }
        }

        {
            System.out.println("\n****** walk through next links ***************************************************\n");

            int count = 0;
            while (nextHref!=null) {

                System.out.println("href("+count+"): "+nextHref);
                count++;

                ResponseEntity response = get(nextHref);

                System.out.println("response status code = "+response.getStatusCode().toString());
                assertThat(response.getStatusCode().toString()).isEqualTo("200");

                Map body = (Map) response.getBody();

                nextHref = getLinkHref(body, "next");

                // in case of loop, break it
                if (count > 2048) break;
            }
        }

        {
            System.out.println("\n****** walk through prev links ***************************************************\n");

            int count = 0;
            while (prevHref!=null) {

                System.out.println("href("+count+"): "+prevHref);
                count++;

                ResponseEntity response = get(prevHref);

                System.out.println("response status code = "+response.getStatusCode().toString());
                assertThat(response.getStatusCode().toString()).isEqualTo("200");

                Map body = (Map) response.getBody();

                prevHref = getLinkHref(body, "prev");

                // in case of loop, break it
                if (count > 2048) break;
            }
        }
    }
}

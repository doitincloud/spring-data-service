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

package com.doitincloud.library.helper;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.hateoas.*;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.hateoas.mvc.TypeConstrainedMappingJackson2HttpMessageConverter;
import org.springframework.hateoas.mvc.TypeReferences;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.*;

public class HalRest {

    private String baseUrl;

    public HalRest(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    public HalRest() {}
      
    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    private HttpHeaders headers;

    public HttpHeaders getHeaders() {

        if (null == headers) headers = new HttpHeaders();
        return headers;
    }

    public void setHeaders(HttpHeaders headers) {

        this.headers = headers;
    }

    // get response as Map return one record and multiple records in Map
    //
    public ResponseEntity<Map> get(String url) {

        if (null != baseUrl) url = baseUrl + url;

        HttpEntity entity = new HttpEntity(getHeaders());

        ResponseEntity<Map> response = getTemplate().exchange(
                url,
                HttpMethod.GET,
                entity,
                Map.class);

        return response;
    }

    // get one record as T typed object (without links)
    //
    public <T> ResponseEntity<T> get(String url, Class<T> expectedType) {

        if (null != baseUrl) url = baseUrl + url;

        HttpEntity entity = new HttpEntity(getHeaders());

        ResponseEntity<T> response = getTemplate().exchange(
                url,
                HttpMethod.GET,
                entity,
                expectedType);

        return response;
    }

    // get one record as resource<T> object
    //
    public <T> ResponseEntity<Resource<T>> get(String url, TypeReferences.ResourceType<T> expectedType) {

        if (null != baseUrl) url = baseUrl + url;

        HttpEntity entity = new HttpEntity(getHeaders());

        ResponseEntity<Resource<T>> response = getTemplate().exchange(
                url,
                HttpMethod.GET,
                entity,
                expectedType);

        return response;
    }

    // get multiple records as paged resources
    //
    public <T> ResponseEntity<PagedResources<Resource<T>>> get(String url, TypeReferences.PagedResourcesType<Resource<T>> expectedType) {

        if (null != baseUrl) url = baseUrl + url;

        HttpEntity entity = new HttpEntity(getHeaders());

        ResponseEntity<PagedResources<Resource<T>>> response = getTemplate().exchange(
                url,
                HttpMethod.GET,
                entity,
                expectedType);

        return response;
    }

    // create a record using Map input and return Map
    //
    public ResponseEntity<Map> post(String url, Map data) {

        if (null != baseUrl) url = baseUrl + url;

        HttpEntity entity = new HttpEntity(data, getHeaders());

        ResponseEntity<Map> response = getTemplate().exchange(
                url,
                HttpMethod.POST,
                entity,
                Map.class);

        return response;
    }

    // create a record using typed T input return typed T (without links)
    //
    public <T> ResponseEntity<T> post(String url, T data, Class<T> expectedType) {

        if (null != baseUrl) url = baseUrl + url;

        HttpEntity entity = new HttpEntity(data, getHeaders());

        ResponseEntity<T> response = getTemplate().exchange(
                url,
                HttpMethod.POST,
                entity,
                expectedType);

        return response;
    }

    // create a record using typed T input return Resource<T>
    //
    public <T> ResponseEntity<Resource<T>> post(String url, T data, TypeReferences.ResourceType<T> expectedType) {

        if (null != baseUrl) url = baseUrl + url;

        HttpEntity entity = new HttpEntity(data, getHeaders());

        ResponseEntity<Resource<T>> response = getTemplate().exchange(
                url,
                HttpMethod.POST,
                entity,
                expectedType);

        return response;
    }

    // update a record using Map input and return Map
    //
    public ResponseEntity put(String url, Map data) {

        if (null != baseUrl) url = baseUrl + url;

        HttpEntity entity = new HttpEntity(data, getHeaders());

        ResponseEntity response = getTemplate().exchange(
                url,
                HttpMethod.PUT,
                entity,
                Map.class);

        return response;
    }

    // update a record using typed T return typed T (without links)
    //
    public <T> ResponseEntity<T> put(String url, T data, Class<T> expectedType) {

        if (null != baseUrl) url = baseUrl + url;

        HttpEntity entity = new HttpEntity(data, getHeaders());

        ResponseEntity<T> response = getTemplate().exchange(
                url,
                HttpMethod.PUT,
                entity,
                expectedType);

        return response;
    }

    // update a record using typed T return resource<T>
    //
    public <T> ResponseEntity<Resource<T>> put(String url, T data, TypeReferences.ResourceType<T> expectedType) {

        if (null != baseUrl) url = baseUrl + url;

        HttpEntity entity = new HttpEntity(data, headers);

        ResponseEntity<Resource<T>> response = getTemplate().exchange(
                url,
                HttpMethod.PUT,
                entity,
                expectedType);

        return response;
    }

    // patch a record using Map input return Map
    //
    public ResponseEntity<Map> patch(String url, Map data) {

        if (null != baseUrl) url = baseUrl + url;

        HttpHeaders headers = getHeaders();
        headers.setContentType(new MediaType("application", "merge-patch+json"));

        HttpEntity entity = new HttpEntity(data, headers);

        ResponseEntity<Map> response = getTemplate().exchange(
                url,
                HttpMethod.PATCH,
                entity,
                Map.class);

        return response;
    }

    // patch a record using Map input return typed T (without links)
    //
    public <T> ResponseEntity<T> patch(String url, Map data, Class<T> expectedType) {

        if (null != baseUrl) url = baseUrl + url;

        HttpHeaders headers = getHeaders();
        headers.setContentType(new MediaType("application", "merge-patch+json"));

        HttpEntity entity = new HttpEntity(data, headers);

        ResponseEntity<T> response = getTemplate().exchange(
                url,
                HttpMethod.PATCH,
                entity,
                expectedType);

        return response;
    }

    // patch a record using typed T return typed T (without links)
    //
    public <T> ResponseEntity<T> patch(String url, T data, Class<T> expectedType) {

        if (null != baseUrl) url = baseUrl + url;

        HttpHeaders headers = getHeaders();
        headers.setContentType(new MediaType("application", "merge-patch+json"));

        HttpEntity entity = new HttpEntity(data, headers);

        ResponseEntity<T> response = getTemplate().exchange(
                url,
                HttpMethod.PATCH,
                entity,
                expectedType);

        return response;
    }

    // patch a record use typed T return typed Resource<T>
    //
    public <T> ResponseEntity<Resource<T>> patch(String url, T data, TypeReferences.ResourceType<T> expectedType) {

        if (null != baseUrl) url = baseUrl + url;

        HttpHeaders headers = getHeaders();
        headers.setContentType(new MediaType("application", "merge-patch+json"));

        HttpEntity entity = new HttpEntity(data, headers);

        ResponseEntity<Resource<T>> response = getTemplate().exchange(
                url,
                HttpMethod.PATCH,
                entity,
                expectedType);

        return response;
    }

    // delete a record
    //
    public ResponseEntity delete(String url) {

        if (null != baseUrl) url = baseUrl + url;

        HttpEntity entity = new HttpEntity(headers);

        ResponseEntity response = getTemplate().exchange(
                url,
                HttpMethod.DELETE,
                entity,
                Map.class);

        return response;
    }

    //****************************************************************************************
    // helper functions for map, resource and resources

    // get single object map or collection of object maps
    // 
    public Object getContent(Map map) {

        if (null == map) return null;
        if (map.containsKey("_embedded")) {
            Map<String, Object> embedded = (Map<String, Object>) map.get("_embedded");
            Set<Map.Entry<String, Object>> entries = embedded.entrySet();
            for(Map.Entry<String, Object> entry:entries) {
                // the first one
                return entry.getValue();
            }
        } else if (map.containsKey("_links")) {
            Map clone = new HashMap(map);
            clone.remove("_links");
            if (0 == clone.size()) return null;
            return clone;
        }
        return null;
    }


    // get profile
    //
    public Map getProfile(Map map) {

        if (null == map) return null;
        if (map.containsKey("alps")) {
            return  (Map) map.get("alps");
        }
        return null;
    }

    // get profile version
    //
    public String getProfileVersion(Map map) {

        if (null == map) return null;
        if (map.containsKey("version")) {
            return (String) map.get("version");
        } else if (map.containsKey("alps")) {
            Map alps = (Map) map.get("alps");
            if (alps.containsKey("version")) {
                return (String) alps.get("version");
            }
        }
        return null;
    }

    // get profile version
    //
    public Collection<Map> getProfileDescriptors(Map map) {

        if (null == map) return null;
        if (map.containsKey("descriptors")) {
            return (Collection<Map>) map.get("descriptors");
        } else if (map.containsKey("alps")) {
            Map alps = (Map) map.get("alps");
            if (alps.containsKey("descriptors")) {
                return (Collection<Map>) map.get("descriptors");
            }
        }
        return null;
    }

    // get links
    // 
    public Map getLinks(Map map) {

        if (null == map) return null;
        if (!map.containsKey("_links")) return null;
        return (Map) map.get("_links");
    }

    // get href per rel for Links
    //
    public String getLinkHref(List<Link> links, String rel) {

        Iterator<Link> iterator = links.iterator();
        while (iterator.hasNext()) {
            Link link = iterator.next();
            if (link.getRel().equals(rel)) {
                return link.getHref();
            }
        }
        return null;
    }

    // get href per rel for map
    //
    public String getLinkHref(Map map, String rel) {

        if (map.containsKey(rel)) {
            Map link = (Map) map.get(rel);
            return (String) link.get("href");
        }
        Map links = getLinks(map);
        if (null == links) return null;
        if (!links.containsKey(rel)) return null;
        Map link = (Map) links.get(rel);
        return (String) link.get("href");
    }

    // get page meta data
    //
    public Map getMetadata(Map map) {

        if (null == map) return null;
        if (!map.containsKey("page")) return null;
        return (Map) map.get("page");
    }

    //****************************************************************************************
    // implementation details

    private HttpMessageConverter halMessageConverter() {

        ObjectMapper mapper = new ObjectMapper();

        // https://github.com/spring-projects/spring-hateoas/issues/524
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        ObjectMapper objectMapper = mapper.registerModule(new Jackson2HalModule());

        TypeConstrainedMappingJackson2HttpMessageConverter halConverter =
                new TypeConstrainedMappingJackson2HttpMessageConverter(ResourceSupport.class);
        halConverter.setSupportedMediaTypes(Collections.singletonList(MediaTypes.HAL_JSON));
        halConverter.setObjectMapper(objectMapper);

        return halConverter;
    }

    private RestTemplate restTemplate;

    private RestTemplate getTemplate() {

        if (null != restTemplate) return restTemplate;

        restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

        // add HAL support
        List<HttpMessageConverter<?>> existingConverters = restTemplate.getMessageConverters();
        List<HttpMessageConverter<?>> newConverters = new ArrayList<>();
        newConverters.add(halMessageConverter());
        newConverters.addAll(existingConverters);
        restTemplate.setMessageConverters(newConverters);

        return restTemplate;
    }
}

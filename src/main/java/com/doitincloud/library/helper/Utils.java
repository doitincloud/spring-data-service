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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.fasterxml.jackson.databind.MapperFeature.SORT_PROPERTIES_ALPHABETICALLY;

public class Utils {

    public static String decode(String s) {

        try {
            return URLDecoder.decode(s,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }

    }

    public static String encode(String s) {

        try {
            return URLEncoder.encode(s,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }

    }

    public static Map<String, String> getQueryParams(String query) {

        Map<String, String> params = new HashMap<String, String>();
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            String key = decode(pair[0]);
            if ("".equals(key)) continue;
            String value = "";
            if (pair.length > 1) {
                value = decode(pair[1]);
            }
            params.put(key, value);
        }
        return params;

    }

    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    public static String dateToString(Date date) {

        return df.format(date);

    }

    public static boolean ifMapsEqual(Map map1, Map map2) {
      
      if (map1.size() != map2.size()) return false;
      
      for (Map.Entry<String, Object> entry : ((Map<String, Object>) map1).entrySet()) {
          String key = entry.getKey();
          String value1 = entry.getValue().toString();
          String value2 = map2.get(key).toString();
          if (!value1.equals(value2)) return false;
      }
      return true;
    }
    
    private static ObjectMapper objectMapper;

    public static ObjectMapper getObjectMapper() {

        if (null != objectMapper) return objectMapper;

        objectMapper = new ObjectMapper();

        return objectMapper;
    }

    // convert json string to JsonNode
    //
    public static JsonNode fromString(String s) {
        try {
            return getObjectMapper().readTree(s);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // convert json string to T typed object
    //
    public static <T> T fromString(String s, Class<T> expectedType) {
        JsonNode jsonNode = fromString(s);
        if (null == jsonNode) return null;
        return getObjectMapper().convertValue(jsonNode, expectedType);
    }

    // convert an object to json string
    //
    public static String toJson(Object o) {
        try {
            return getObjectMapper().writeValueAsString(o);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}";
        }
    }

    // convert an object to pretty json string
    //
    public static String toPrettyJson(Object o) {
        try {
            return getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(o);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}";
        }
    }

    // convert an object to a map
    //
    public static Map toMap(Object o) {

        return getObjectMapper().convertValue(o, Map.class);
    }

    // convert a map to expected type object
    //
    public static <T> T toObject(Map map, Class<T> expectedType) {

        return getObjectMapper().convertValue(map, expectedType);
    }

}

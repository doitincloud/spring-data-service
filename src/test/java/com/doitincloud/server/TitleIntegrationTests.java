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

import com.doitincloud.library.employees.TitleId;
import com.doitincloud.library.helper.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.TypeReferences;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

import com.doitincloud.library.employees.Title;
import com.doitincloud.library.employees.Employee;

@RunWith(SpringRunner.class)
public class TitleIntegrationTests extends TestBase {

    @Test
    @Sql({"/schema.sql", "/delete-all.sql"})
    public void CRUDTests() {

        setup();

        String apiUrl = apiBaseUrl + "/titles";
        String apiUrlPer;

        System.out.println("\n=> "+this.getClass().getSimpleName());
        System.out.println("****** CRUD Operations Test ****************************************************************\n");

        VerifyApiUrl(apiUrl);

        Title title;

        Employee employee = new Employee();
        employee.setFirstName("Samual");
        employee.setLastName("Adam");
        employee.setBirthDate(java.sql.Date.valueOf("1990-02-12"));
        employee.setGender("M");
        employee.setHireDate(java.sql.Date.valueOf("2017-07-01"));

        System.out.println("employee = "+ Utils.toJson(employee));

        {
            System.out.println("\n****** 11 create employee ************************************************************\n");

            System.out.println("url = "+apiBaseUrl+"/employees");

            ResponseEntity<Employee> response = post(apiBaseUrl+"/employees", employee, Employee.class);

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("201");

            Employee body = response.getBody();

            assertThat(body.getEmpNo()).isNotZero();

            employee.setEmpNo(body.getEmpNo());

            readToVerifyTheRecord(apiBaseUrl+"/employees/"+body.getEmpNo(), employee);
        }

        {
            System.out.println("\n****** 12 create a new record ********************************************************\n");

            TitleId titleId = new TitleId(employee.getEmpNo(), "Senior Cloud Solution Architect",
                    java.sql.Date.valueOf("2017-08-01"), java.sql.Date.valueOf("9999-01-01"));

            System.out.println("url = "+apiUrl);

            ResponseEntity<Title> response = post(apiUrl, titleId, Title.class);

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("201");

            Title body = response.getBody();

            assertThat(body.equals(titleId)).isTrue();

            title = body;

            apiUrlPer = apiUrl+"/empNo="+title.getEmpNo()+"&title="+Utils.encode(title.getTitle())+
                    "&fromDate="+Utils.dateToString(title.getFromDate());

            readToVerifyTheRecord(apiUrlPer, title);
        }

        {
            System.out.println("\n****** 21 edit use put - required all fields *****************************************\n");

            TitleId titleId = new TitleId(title);
            titleId.setToDate(java.sql.Date.valueOf("2017-09-01"));

            System.out.println("url = "+apiUrlPer);

            ResponseEntity<Title> response = put(apiUrlPer, titleId, Title.class);

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("200");

            Title body = response.getBody();

            assertThat(titleId.equals(body)).isTrue();

            title.setToDate(java.sql.Date.valueOf("2017-09-01"));

            readToVerifyTheRecord(apiUrlPer, title);
        }

        {
            System.out.println("\n****** 31 edit with patch - requires only the fields needed to change  ***************\n");

            Map<String, Object> patch = new HashMap<String, Object>();
            patch.put("toDate", "2018-01-01");

            System.out.println("patch:"+Utils.toJson(patch));

            System.out.println("url = "+apiUrlPer);

            ResponseEntity<Title> response = patch(apiUrlPer, patch, Title.class);

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("200");

            Title body = response.getBody();

            assertThat(body.getToDate()).isEqualTo(java.sql.Date.valueOf("2018-01-01"));

            title = body;

            readToVerifyTheRecord(apiUrlPer, title);
        }

        {
            System.out.println("\n****** 41 delete *********************************************************************\n");

            System.out.println("url = "+apiUrlPer);

            ResponseEntity response = delete(apiUrlPer);

            assertThat(response.getStatusCode().toString()).isEqualTo("204");

            System.out.println("response status code = " + response.getStatusCode());

            readToVerifyTheDelete(apiUrlPer);
        }

        {
            System.out.println("\n****** 42 delete employee ************************************************************\n");

            System.out.println("url = "+apiBaseUrl+"/employees/"+employee.getEmpNo());

            ResponseEntity response = delete(apiBaseUrl+"/employees/"+employee.getEmpNo());

            System.out.println("response status code = " + response.getStatusCode());
            assertThat(response.getStatusCode().toString()).isEqualTo("204");

            readToVerifyTheDelete(apiBaseUrl+"/employees/"+employee.getEmpNo());
        }

        System.out.println("\n<= "+this.getClass().getSimpleName());
        System.out.println("****** Done with CRUD Tests ****************************************************************\n");
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    public void TestsWithSampleData() {

        setup();

        String apiUrl = apiBaseUrl + "/titles";
        int pageSize = 20;

        String apiUrlPer = apiUrl + "/empNo=10001&title=Senior+Engineer&fromDate=1986-06-26";

        System.out.println("\n=> " + this.getClass().getSimpleName());
        System.out.println("****** Tests with Sample Data **************************************************************\n");

        System.out.println("api url = " + apiUrl);

        {
            System.out.println("\n****** 11 read one item **********************************************************\n");

            System.out.println("url = "+apiUrlPer);

            ResponseEntity<Resource<Title>> response = get(apiUrlPer, new TypeReferences.ResourceType<Title>() {});

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("200");

            Resource<Title> resource = response.getBody();
            Title title = resource.getContent();

            assertThat(resource.hasLink(Link.REL_SELF)).isTrue();
            assertThat(resource.hasLink("title")).isTrue();
            assertThat(resource.getLink("title").getHref()).isEqualTo(resource.getLink(Link.REL_SELF).getHref());
            assertThat(resource.getLink("title").getHref()).isEqualTo(apiUrlPer);

            assertThat(title.getEmpNo()).isEqualTo(10001);
            assertThat(title.getFromDate()).isEqualTo(java.sql.Date.valueOf("1986-06-26"));
            assertThat(title.getTitle()).isEqualTo("Senior Engineer");
            assertThat(title.getToDate()).isEqualTo(java.sql.Date.valueOf("9999-01-01"));

            FollowLinks(resource.getLinks(), "title");
        }

        {
            System.out.println("\n****** 21 first view of items list ***********************************************\n");

            System.out.println("url = "+apiUrl+"?size="+pageSize);

            ResponseEntity<PagedResources<Resource<Title>>> response = get(apiUrl+"?size="+pageSize,
                    new TypeReferences.PagedResourcesType<Resource<Title>>() {});

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("200");

            PagedResources<Resource<Title>> resources = response.getBody();

            Collection<Resource<Title>> content = resources.getContent();

            assertThat(content.size()).isLessThanOrEqualTo(pageSize);

            FollowLinks(resources.getLinks(), "title");
        }

        System.out.println("\n<= "+this.getClass().getSimpleName());
        System.out.println("****** Done Tests With Sample Data *********************************************************\n");
    }
}

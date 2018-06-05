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

import com.doitincloud.library.employees.Employee;

@RunWith(SpringRunner.class)
public class EmployeeIntegrationTests extends TestBase {

    @Test
    @Sql({"/schema.sql", "/delete-all.sql"})
    public void CRUDTests() {

        setup();

        String apiUrl = apiBaseUrl + "/employees";

        String apiUrlPer;

        System.out.println("\n=> "+this.getClass().getSimpleName());
        System.out.println("****** CRUD Operations Test ****************************************************************\n");

        VerifyApiUrl(apiUrl);

        Employee  employee = new Employee();
        employee.setFirstName("Samual");
        employee.setLastName("Adam");
        employee.setBirthDate(java.sql.Date.valueOf("1990-02-12"));
        employee.setGender("M");
        employee.setHireDate(java.sql.Date.valueOf("2017-07-01"));

        System.out.println("employee = " + Utils.toJson(employee));

        {
            System.out.println("\n****** 11 create a new record ********************************************************\n");

            System.out.println("url = "+apiUrl);

            ResponseEntity<Employee> response = post(apiUrl, employee, Employee.class);

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("201");

            Employee body = response.getBody();

            assertThat(body.getEmpNo()).isNotZero();

            employee.setEmpNo(body.getEmpNo());

            apiUrlPer = apiUrl + "/" + employee.getEmpNo();

            readToVerifyTheRecord(apiUrlPer, employee);
        }

        {
            System.out.println("\n****** 21 edit use put - required all fields *****************************************\n");

            employee.setFirstName("Sam");

            System.out.println("url = "+apiUrlPer);

            ResponseEntity<Employee> response = put(apiUrlPer, employee, Employee.class);

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("200");

            Employee body = response.getBody();

            assertThat(employee.equals(body)).isTrue();

            readToVerifyTheRecord(apiUrlPer, employee);
        }

        {
            System.out.println("\n****** 31 edit with patch - requires only the fields needed to change  ***************\n");

            Map<String, String> patch = new HashMap<String, String>();
            patch.put("firstName", "Samual");

            System.out.println("patch:"+Utils.toJson(patch));

            System.out.println("url = "+apiUrlPer);

            ResponseEntity<Employee> response = patch(apiUrlPer, patch, Employee.class);

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("200");

            Employee body = response.getBody();

            assertThat(body.getFirstName()).isEqualTo("Samual");

            employee.setFirstName("Samual");

            readToVerifyTheRecord(apiUrlPer, employee);
        }

        {
            System.out.println("\n****** 41 delete *********************************************************************\n");

            System.out.println("url = "+apiUrlPer);

            ResponseEntity response = delete(apiUrlPer);

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("204");

            System.out.println("response status code = " + response.getStatusCode());

            readToVerifyTheDelete(apiUrlPer);
        }

        System.out.println("\n<= "+this.getClass().getSimpleName());
        System.out.println("****** Done with CRUD Tests ****************************************************************\n");
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    public void TestsWithSampleData() {

        setup();

        String apiUrl = apiBaseUrl + "/employees";
        int pageSize = 20;

        String apiUrlPer = apiUrl + "/10001";

        System.out.println("\n=> "+this.getClass().getSimpleName());
        System.out.println("****** Tests With Sample Data **************************************************************\n");

        System.out.println("api url = " + apiUrl);

        {
            System.out.println("\n****** 11 read one item **************************************************************\n");

            System.out.println("url = "+apiUrlPer);

            ResponseEntity<Resource<Employee>> response = get(apiUrlPer, new TypeReferences.ResourceType<Employee>() {});

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("200");

            Resource<Employee> resource = response.getBody();
            Employee employee = resource.getContent();

            assertThat(resource.hasLink(Link.REL_SELF)).isTrue();
            assertThat(resource.hasLink("employee")).isTrue();
            assertThat(resource.getLink("employee").getHref()).isEqualTo(resource.getLink(Link.REL_SELF).getHref());
            assertThat(resource.getLink("employee").getHref()).isEqualTo(apiUrlPer);

            assertThat(employee.getEmpNo()).isEqualTo(10001);
            assertThat(employee.getFirstName()).isEqualTo("Georgi");
            assertThat(employee.getLastName()).isEqualTo("Facello");
            assertThat(employee.getGender()).isEqualTo("M");
            assertThat(employee.getBirthDate()).isEqualTo(java.sql.Date.valueOf("1953-09-02"));
            assertThat(employee.getHireDate()).isEqualTo(java.sql.Date.valueOf("1986-06-26"));

            FollowLinks(resource.getLinks(), "employee");
        }

        {
            System.out.println("\n****** 21 first view of items list ***************************************************\n");

            System.out.println("url = "+apiUrl+"?size="+pageSize);

            ResponseEntity<PagedResources<Resource<Employee>>> response = get(apiUrl+"?size="+pageSize,
                    new TypeReferences.PagedResourcesType<Resource<Employee>>() {});

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("200");

            PagedResources<Resource<Employee>> resources = response.getBody();

            Collection<Resource<Employee>> content = resources.getContent();

            assertThat(content.size()).isLessThanOrEqualTo(pageSize);

            FollowLinks(resources.getLinks(), "employee");
        }

        System.out.println("\n<= "+this.getClass().getSimpleName());
        System.out.println("****** Done Tests With Sample Data *********************************************************\n");
    }

    //@Test
    @Sql({"/data.sql"})
    public void TestsWithSampleDataBySearch() {

        setup();

        String apiUrl = apiBaseUrl + "/employees";
        int pageSize = 3;

        System.out.println("\n=> "+this.getClass().getSimpleName());
        System.out.println("****** Tests With Sample Data By Search ****************************************************\n");

        System.out.println("api url = " + apiUrl);

        {
            System.out.println("\n****** 11 maxID **********************************************************************\n");

            String url = apiUrl+"/search/maxId";

            System.out.println("url = "+url);

            ResponseEntity<Long> response = get(url, Long.class);

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("200");

            Long body = response.getBody();

            assertThat(body).isEqualTo(111133);
        }

        {
            System.out.println("\n****** 21 findByLastName *************************************************************\n");

            String url = apiUrl+"/search/findByLastName?name=Leonhardt&size=" + pageSize;
            System.out.println("url = "+url);

            ResponseEntity<PagedResources<Resource<Employee>>> response = get(apiUrl+"?size="+pageSize,
                    new TypeReferences.PagedResourcesType<Resource<Employee>>() {});

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("200");

            System.out.println("href: " + apiUrl + "?size=" + pageSize);

            PagedResources<Resource<Employee>> resources = response.getBody();

            Collection<Resource<Employee>> content = resources.getContent();

            assertThat(content.size()).isEqualTo(1);

            PagedResources.PageMetadata metaData = resources.getMetadata();

            assertThat(metaData.getTotalPages()).isEqualTo(1);
            assertThat(metaData.getTotalElements()).isEqualTo(1);
            assertThat(metaData.getNumber()).isEqualTo(0);
            assertThat(metaData.getSize()).isEqualTo(3);

            Iterator<Resource<Employee>> iterator = content.iterator();

            assertThat(iterator.hasNext()).isTrue();

            Resource<Employee> resource = iterator.next();

            Employee employee1 = resource.getContent();

            String text = "{\"birthDate\":\"1952-08-06\",\"firstName\":\"Gino\",\"lastName\":\"Leonhardt\",\"gender\":\"F\",\"hireDate\":\"1989-04-08\",\"empNo\":10063}";

            Employee employee2 = Utils.fromString(text, Employee.class);

            assertThat(employee1).isEqualTo(employee2);

            FollowLinks(resources.getLinks(), "employee");
        }

        {
            System.out.println("\n****** 31 findByLastNameLike *********************************************************\n");

            String url = apiUrl+"/search/findByLastNameLike?name=ch&size=" + pageSize;
            System.out.println("url = "+url);

            ResponseEntity<PagedResources<Resource<Employee>>> response = get(apiUrl+"?size="+pageSize,
                    new TypeReferences.PagedResourcesType<Resource<Employee>>() {});

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("200");

            System.out.println("href: " + apiUrl + "?size=" + pageSize);

            PagedResources<Resource<Employee>> resources = response.getBody();

            Collection<Resource<Employee>> content = resources.getContent();

            assertThat(content.size()).isEqualTo(3);

            PagedResources.PageMetadata metaData = resources.getMetadata();

            assertThat(metaData.getTotalPages()).isEqualTo(3);
            assertThat(metaData.getTotalElements()).isEqualTo(9);
            assertThat(metaData.getNumber()).isEqualTo(0);
            assertThat(metaData.getSize()).isEqualTo(3);

            String[] employees_strings = {
                    "{\"birthDate\":\"1953-02-08\",\"firstName\":\"Alain\",\"lastName\":\"Chappelet\",\"gender\":\"M\",\"hireDate\":\"1988-09-05\",\"empNo\":10035}",
                    "{\"birthDate\":\"1952-06-29\",\"firstName\":\"Zvonko\",\"lastName\":\"Nyanchama\",\"gender\":\"M\",\"hireDate\":\"1989-03-31\",\"empNo\":10047}",
                    "{\"birthDate\":\"1961-02-26\",\"firstName\":\"Heping\",\"lastName\":\"Nitsch\",\"gender\":\"M\",\"hireDate\":\"1988-05-21\",\"empNo\":10052}"
            };

            Employee[] employees = new Employee[employees_strings.length];
            for (int i = 0; i < employees_strings.length; i++) {
                employees[i] = Utils.fromString(employees_strings[i], Employee.class);
            }

            Iterator<Resource<Employee>> iterator = content.iterator();

            for (int i = 0; i < employees.length; i++) {

                assertThat(iterator.hasNext()).isTrue();
                Resource<Employee> resource = iterator.next();
                Employee employee = resource.getContent();

                assertThat(employee).isEqualTo(employees[i]);
            }

            FollowLinks(resources.getLinks(), "employee");
        }

        {
            System.out.println("\n****** 41 findByLastNameNotLike ******************************************************\n");

            String url = apiUrl+"/search/findByLastNameNotLike?name=ch&size=" + pageSize;
            System.out.println("url = "+url);

            ResponseEntity<PagedResources<Resource<Employee>>> response = get(apiUrl+"?size="+pageSize,
                    new TypeReferences.PagedResourcesType<Resource<Employee>>() {});

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("200");

            System.out.println("href: " + apiUrl + "?size=" + pageSize);

            PagedResources<Resource<Employee>> resources = response.getBody();

            Collection<Resource<Employee>> content = resources.getContent();

            assertThat(content.size()).isEqualTo(pageSize);

            assertThat(content.size()).isEqualTo(3);

            PagedResources.PageMetadata metaData = resources.getMetadata();

            assertThat(metaData.getTotalPages()).isEqualTo(33);
            assertThat(metaData.getTotalElements()).isEqualTo(98);
            assertThat(metaData.getNumber()).isEqualTo(0);
            assertThat(metaData.getSize()).isEqualTo(3);

            String[] employees_strings = {
                    "{\"birthDate\":\"1953-09-02\",firstName\":\"Georgi\",lastName\":\"Facello\",gender\":\"M\",hireDate\":\"1986-06-26\",empNo\":10001}",
                    "{\"birthDate\":\"1964-06-02\",firstName\":\"Bezalel\",lastName\":\"Simmel\",gender\":\"F\",hireDate\":\"1985-11-21\",empNo\":10002}",
                    "{\"birthDate\":\"1959-12-03\",firstName\":\"Parto\",lastName\":\"Bamford\",gender\":\"M\",hireDate\":\"1986-08-28\",empNo\":10003}"
            };

            Employee[] employees = new Employee[employees_strings.length];
            for (int i = 0; i < employees_strings.length; i++) {
                employees[i] = Utils.fromString(employees_strings[i], Employee.class);
            }

            Iterator<Resource<Employee>> iterator = content.iterator();

            for (int i = 0; i < employees.length; i++) {

                assertThat(iterator.hasNext()).isTrue();
                Resource<Employee> resource = iterator.next();
                Employee employee = resource.getContent();

                assertThat(employee).isEqualTo(employees[i]);
            }

            FollowLinks(resources.getLinks(), "employee");
        }

        {
            System.out.println("\n****** 51 birthdaysOfMonth ***********************************************************\n");

            String url = apiUrl+"/search/birthdaysOfMonth?today=2017-07-01&size=" + pageSize;
            System.out.println("url = "+url);

            ResponseEntity<PagedResources<Resource<Employee>>> response = get(apiUrl+"?size="+pageSize,
                    new TypeReferences.PagedResourcesType<Resource<Employee>>() {});

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("200");

            System.out.println("href: " + apiUrl + "?size=" + pageSize);

            PagedResources<Resource<Employee>> resources = response.getBody();

            Collection<Resource<Employee>> content = resources.getContent();

            assertThat(content.size()).isEqualTo(3);

            PagedResources.PageMetadata metaData = resources.getMetadata();
            assertThat(metaData.getTotalPages()).isEqualTo(4);
            assertThat(metaData.getTotalElements()).isEqualTo(12);
            assertThat(metaData.getNumber()).isEqualTo(0);
            assertThat(metaData.getSize()).isEqualTo(3);

            String[] employees_strings = {
                    "{\"birthDate\":\"1958-07-06\",\"firstName\":\"Cristinel\",\"lastName\":\"Bouloucos\",\"gender\":\"F\",\"hireDate\":\"1993-08-03\",\"empNo\":10017}",
                    "{\"birthDate\":\"1952-07-08\",\"firstName\":\"Shahaf\",\"lastName\":\"Famili\",\"gender\":\"M\",\"hireDate\":\"1995-08-22\",\"empNo\":10022}",
                    "{\"birthDate\":\"1962-07-10\",\"firstName\":\"Divier\",\"lastName\":\"Reistad\",\"gender\":\"F\",\"hireDate\":\"1989-07-07\",\"empNo\":10027}"
            };

            Employee[] employees = new Employee[employees_strings.length];
            for (int i = 0; i < employees_strings.length; i++) {
                employees[i] = Utils.fromString(employees_strings[i], Employee.class);
            }

            Iterator<Resource<Employee>> iterator = content.iterator();

            for (int i = 0; i < employees.length; i++) {

                assertThat(iterator.hasNext()).isTrue();
                Resource<Employee> resource = iterator.next();
                Employee employee = resource.getContent();

                assertThat(employee).isEqualTo(employees[i]);
            }

            FollowLinks(resources.getLinks(), "employee");
        }

        {
            System.out.println("\n****** 61 birthdaysOfTheMonth ********************************************************\n");

            String url = apiUrl+"/search/birthdaysOfTheMonth?size=" + pageSize;
            System.out.println("url = "+url);

            ResponseEntity<PagedResources<Resource<Employee>>> response = get(apiUrl+"?size="+pageSize,
                    new TypeReferences.PagedResourcesType<Resource<Employee>>() {});

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("200");

            System.out.println("href: " + apiUrl + "?size=" + pageSize);

            PagedResources<Resource<Employee>> resources = response.getBody();

            Collection<Resource<Employee>> content = resources.getContent();

            assertThat(content.size()).isLessThanOrEqualTo(pageSize);

            FollowLinks(resources.getLinks(), "employee");

        }

        System.out.println("\n<= "+this.getClass().getSimpleName());
        System.out.println("****** Done Tests With Sample Data By Search ***********************************************\n");
    }

}
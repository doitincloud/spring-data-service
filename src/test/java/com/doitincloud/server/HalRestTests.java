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

import com.doitincloud.library.employees.Employee;
import com.doitincloud.library.helper.HalRest;
import com.doitincloud.library.helper.Utils;
import com.doitincloud.server.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.TypeReferences;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HalRestTests {

    @Value("${local.server.port}")
    protected int port;

    @Test
    @Sql({"/schema.sql", "/delete-all.sql"})
    public void SingleItemTests() {

        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        HalRest access = new HalRest(String.format("http://localhost:%d/ds/v1", port));

        long empNo1, empNo2, empNo3;

        System.out.println("\n=> "+this.getClass().getSimpleName());
        System.out.println("****** Single Item HalRest Tests ***********************************************************\n");

        // map
        Map<String, Object> employee1 = new HashMap<String, Object>();
        employee1.put("birthDate", java.sql.Date.valueOf("1975-07-21"));
        employee1.put("firstName", "Johnson");
        employee1.put("lastName", "Adam");
        employee1.put("gender", "M");
        employee1.put("hireDate", java.sql.Date.valueOf("1999-02-16"));

        //System.out.println("===> map employee1: \n" + Utils.toPrettyJson(employee1));

        // typed
        Employee employee2 = new Employee(0,
                java.sql.Date.valueOf("1985-02-12"),
                "Samual", "Adam",
                "M",
                java.sql.Date.valueOf("2010-03-12"));

        //System.out.println("===> typed employee2: \n" + Utils.toPrettyJson(employee2));

        // typed
        Employee employee3 = new Employee(0,
                java.sql.Date.valueOf("1989-09-1"),
                "Tom", "Adam",
                "M",
                java.sql.Date.valueOf("2015-07-17"));


        //System.out.println("===> typed employee3: \n" + Utils.toPrettyJson(employee3));

        {
            System.out.println("\n****** 11 create a new record with map ***********************************************\n");

            ResponseEntity response = access.post("/employees", employee1);

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("201");

            Map body = (Map) response.getBody();
            Map content = (Map) access.getContent(body);
            Map links = (Map) access.getLinks(body);
            //System.out.println("===> getContent: \n" + Utils.toPrettyJson(content));
            //System.out.println("===> getLinks: \n" + Utils.toPrettyJson(links));

            empNo1 = Long.valueOf(content.get("empNo").toString());

            assertThat(empNo1).isEqualTo(1L);

            System.out.println("empNo: "+empNo1);
            employee1.put("empNo", empNo1);
        }

        {
            System.out.println("\n****** 12 check the created record with map ******************************************\n");

            ResponseEntity response = access.get("/employees/"+empNo1);

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("200");

            Map body = (Map)response.getBody();
            Map content = (Map) access.getContent(body);

            //System.out.println("===> getContent: \n" + Utils.toPrettyJson(content));
            assertThat(Utils.toJson(content)).isEqualTo(Utils.toJson(employee1));

            Map links = (Map) access.getLinks(body);
            //System.out.println("===> getLinks: \n" + Utils.toPrettyJson(links));
            assertThat(access.getLinkHref(links, "self")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo1);
            assertThat(access.getLinkHref(links, "employee")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo1);
            assertThat(access.getLinkHref(links, "departments")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo1+"/departments");
            assertThat(access.getLinkHref(links, "titles")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo1+"/titles");
            assertThat(access.getLinkHref(links, "deptEmps")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo1+"/deptEmps");
            assertThat(access.getLinkHref(links, "salaries")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo1+"/salaries");
        }

        {
            System.out.println("\n****** 21 create a new record with typed return typed resource ***********************\n");

            ResponseEntity<Resource<Employee>> response = access.post("/employees", employee2,
                    new TypeReferences.ResourceType<Employee>() {});

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("201");

            Resource<Employee> body = response.getBody();
            Employee content = body.getContent();
            List<Link> links = body.getLinks();
            //System.out.println("===> getContent: \n" + Utils.toPrettyJson(content));
            //System.out.println("===> getLinks: \n" + Utils.toPrettyJson(links));

            empNo2 = content.getEmpNo();

            assertThat(empNo2).isEqualTo(2L);

            System.out.println("empNo: "+empNo2);
            employee2.setEmpNo(empNo2);

        }

        {
            System.out.println("\n****** 22 check the created record with typed return typed resource ******************\n");

            ResponseEntity<Resource<Employee>> response = access.get("/employees/"+empNo2, new TypeReferences.ResourceType<Employee>() {});

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("200");

            Resource<Employee> body = response.getBody();
            Employee content = body.getContent();
            List<Link> links = body.getLinks();

            //System.out.println("===> getContent: \n" + Utils.toPrettyJson(content));
            assertThat(Utils.toJson(content)).isEqualTo(Utils.toJson(employee2));

            //System.out.println("===> getLinks: \n" + Utils.toPrettyJson(links));
            assertThat(access.getLinkHref(links, "self")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo2);
            assertThat(access.getLinkHref(links, "employee")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo2);
            assertThat(access.getLinkHref(links, "departments")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo2+"/departments");
            assertThat(access.getLinkHref(links, "titles")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo2+"/titles");
            assertThat(access.getLinkHref(links, "deptEmps")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo2+"/deptEmps");
            assertThat(access.getLinkHref(links, "salaries")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo2+"/salaries");
        }

        {
            System.out.println("\n****** 31 create a new record with typed return typed (no links) *********************\n");

            ResponseEntity response = access.post("/employees", employee3, Employee.class);

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("201");

            Employee body = (Employee) response.getBody();

            empNo3 = body.getEmpNo();

            assertThat(empNo3).isEqualTo(3L);

            System.out.println("empNo: "+empNo3);
            employee3.setEmpNo(empNo3);

        }

        {
            System.out.println("\n****** 32 check the created record with typed return typed (no links) ****************\n");

            ResponseEntity response = access.get("/employees/"+empNo3, Employee.class);

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("200");

            Employee body = (Employee) response.getBody();

            //System.out.println("===> getBody: \n" + Utils.toPrettyJson(body));
            assertThat(Utils.toJson(body)).isEqualTo(Utils.toJson(employee3));
        }

        {
            System.out.println("\n****** 41 put update with map ********************************************************\n");

            employee1.put("lastName", "Wen");

            ResponseEntity response = access.put("/employees/"+empNo1, employee1);

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("200");

            Map body = (Map) response.getBody();
            Map content = (Map) access.getContent(body);
            //Map links = access.getLinks(body);
            //System.out.println("===> getContent: \n" + Utils.toPrettyJson(content));
            //System.out.println("===> getLinks: \n" + Utils.toPrettyJson(links));

            String lastName = content.get("lastName").toString();

            assertThat(lastName).isEqualTo("Wen");
        }

        {
            System.out.println("\n****** 42 check the updated record with map ******************************************\n");

            ResponseEntity response = access.get("/employees/"+empNo1);

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("200");

            Map body = (Map)response.getBody();
            Map content = (Map) access.getContent(body);

            //System.out.println("===> getContent: \n" + Utils.toPrettyJson(content));
            assertThat(Utils.toJson(content)).isEqualTo(Utils.toJson(employee1));

            Map links = (Map) access.getLinks(body);
            //System.out.println("===> getLinks: \n" + Utils.toPrettyJson(links));
            assertThat(access.getLinkHref(links, "self")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo1);
            assertThat(access.getLinkHref(links, "employee")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo1);
            assertThat(access.getLinkHref(links, "departments")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo1+"/departments");
            assertThat(access.getLinkHref(links, "titles")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo1+"/titles");
            assertThat(access.getLinkHref(links, "deptEmps")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo1+"/deptEmps");
            assertThat(access.getLinkHref(links, "salaries")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo1+"/salaries");
        }

        {
            System.out.println("\n****** 43 put update with typed return typed resource ********************************\n");

            employee2.setFirstName("Sam");

            ResponseEntity<Resource<Employee>> response = access.put("/employees/"+empNo2, employee2,
                    new TypeReferences.ResourceType<Employee>() {});

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("200");

            Resource<Employee> body = (Resource<Employee>) response.getBody();
            Employee content = body.getContent();
            List<Link> links = body.getLinks();
            //System.out.println("===> getContent: \n" + Utils.toPrettyJson(content));
            //System.out.println("===> getLinks: \n" + Utils.toPrettyJson(links));

            String firstName = content.getFirstName();

            assertThat(firstName).isEqualTo("Sam");
        }

        {
            System.out.println("\n****** 44 check the updated record with typed return typed resource ******************\n");

            ResponseEntity<Resource<Employee>> response = access.get("/employees/"+empNo2, new TypeReferences.ResourceType<Employee>() {});

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("200");

            Resource<Employee> body = response.getBody();
            Employee content = body.getContent();
            List<Link> links = body.getLinks();

            //System.out.println("===> getContent: \n" + Utils.toPrettyJson(content));
            assertThat(Utils.toJson(content)).isEqualTo(Utils.toJson(employee2));

            //System.out.println("===> getLinks: \n" + Utils.toPrettyJson(links));
            assertThat(access.getLinkHref(links, "self")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo2);
            assertThat(access.getLinkHref(links, "employee")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo2);
            assertThat(access.getLinkHref(links, "departments")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo2+"/departments");
            assertThat(access.getLinkHref(links, "titles")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo2+"/titles");
            assertThat(access.getLinkHref(links, "deptEmps")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo2+"/deptEmps");
            assertThat(access.getLinkHref(links, "salaries")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo2+"/salaries");
        }

        {
            System.out.println("\n****** 45 put update with typed return typed (no links) ******************************\n");

            employee3.setFirstName("Tim");

            ResponseEntity response = access.put("/employees/"+empNo3, employee3, Employee.class);

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("200");

            Employee body = (Employee) response.getBody();

            String firstName = body.getFirstName();

            assertThat(firstName).isEqualTo("Tim");
        }

        {
            System.out.println("\n****** 46 check the updated record with typed return typed (no links) ****************\n");

            ResponseEntity response = access.get("/employees/"+empNo3, Employee.class);

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("200");

            Employee body = (Employee) response.getBody();

            assertThat(Utils.toJson(body)).isEqualTo(Utils.toJson(employee3));
        }


        {
            System.out.println("\n****** 51 patch update with map ******************************************************\n");

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("lastName", "Adam");

            ResponseEntity response = access.patch("/employees/"+empNo1, map);

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("200");

            Map body = (Map) response.getBody();
            Map content = (Map) access.getContent(body);
            //Map links = (Map) access.getLinks(body);
            //System.out.println("===> getContent: \n" + Utils.toPrettyJson(content));
            //System.out.println("===> getLinks: \n" + Utils.toPrettyJson(links));

            String lastName = content.get("lastName").toString();

            assertThat(lastName).isEqualTo("Adam");
            employee1.put("lastName", "Adam");
        }

        {
            System.out.println("\n****** 52 check the updated record with map ******************************************\n");

            ResponseEntity response = access.get("/employees/"+empNo1);

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("200");

            Map body = (Map) response.getBody();
            Map content = (Map) access.getContent(body);

            //System.out.println("===> getContent: \n" + Utils.toPrettyJson(content));
            assertThat(Utils.toJson(content)).isEqualTo(Utils.toJson(employee1));

            Map links = (Map) access.getLinks(body);
            //System.out.println("===> getLinks: \n" + Utils.toPrettyJson(links));
            assertThat(access.getLinkHref(links, "self")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo1);
            assertThat(access.getLinkHref(links, "employee")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo1);
            assertThat(access.getLinkHref(links, "departments")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo1+"/departments");
            assertThat(access.getLinkHref(links, "titles")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo1+"/titles");
            assertThat(access.getLinkHref(links, "deptEmps")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo1+"/deptEmps");
            assertThat(access.getLinkHref(links, "salaries")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo1+"/salaries");
        }

        {
            System.out.println("\n****** 53 patch update with typed ****************************************************\n");

            Employee obj = new Employee();
            obj.setFirstName("Samual");

            ResponseEntity<Resource<Employee>> response = access.patch("/employees/"+empNo2, obj,
                    new TypeReferences.ResourceType<Employee>() {});

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("200");

            Resource<Employee> body = (Resource<Employee>) response.getBody();
            Employee content = body.getContent();
            List<Link> links = body.getLinks();
            //System.out.println("===> getContent: \n" + Utils.toPrettyJson(content));
            //System.out.println("===> getLinks: \n" + Utils.toPrettyJson(links));

            String firstName = content.getFirstName();

            assertThat(firstName).isEqualTo("Samual");
            employee2.setFirstName("Samual");
        }

        {
            System.out.println("\n****** 54 check the updated record with typed ****************************************\n");

            ResponseEntity<Resource<Employee>> response = access.get("/employees/"+empNo2,
                    new TypeReferences.ResourceType<Employee>() {});

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("200");

            Resource<Employee> body = response.getBody();
            Employee content = body.getContent();
            List<Link> links = body.getLinks();

            //System.out.println("===> getContent: \n" + Utils.toPrettyJson(content));
            assertThat(Utils.toJson(content)).isEqualTo(Utils.toJson(employee2));

            //System.out.println("===> getLinks: \n" + Utils.toPrettyJson(links));
            assertThat(access.getLinkHref(links, "self")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo2);
            assertThat(access.getLinkHref(links, "employee")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo2);
            assertThat(access.getLinkHref(links, "departments")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo2+"/departments");
            assertThat(access.getLinkHref(links, "titles")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo2+"/titles");
            assertThat(access.getLinkHref(links, "deptEmps")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo2+"/deptEmps");
            assertThat(access.getLinkHref(links, "salaries")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo2+"/salaries");
        }

        {
            System.out.println("\n****** 55 patch update with typed return typed (no links) ****************************\n");

            Employee obj = new Employee();
            obj.setFirstName("Tom");

            ResponseEntity response = access.patch("/employees/"+empNo3, obj, Employee.class);

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("200");

            Employee body = (Employee) response.getBody();

            String firstName = body.getFirstName();

            assertThat(firstName).isEqualTo("Tom");
            employee3.setFirstName("Tom");
        }

        {
            System.out.println("\n****** 56 check the updated record with typed return typed (no links) ****************\n");

            ResponseEntity response = access.get("/employees/"+empNo2, Employee.class);

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("200");

            Employee body = (Employee) response.getBody();

            assertThat(Utils.toJson(body)).isEqualTo(Utils.toJson(employee2));
        }

        {
            System.out.println("\n****** 61 delete employee1 ***********************************************************\n");

            ResponseEntity response = access.delete("/employees/"+empNo1);

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("204");

            //System.out.println("===> delete with map: \n" + Utils.toPrettyJson(response));
        }

        {
            System.out.println("\n****** 62 check deletion *************************************************************\n");

            try {
                ResponseEntity response = access.get("/employees/"+empNo1, new TypeReferences.ResourceType<Employee>() {});

                System.out.println("response status code = "+response.getStatusCode().toString());
                assertThat(response.getStatusCode().toString()).isNotEqualTo("200");

            } catch (HttpClientErrorException e) {

                System.out.println("response status code = "+e.getStatusCode().toString());
                assertThat(e.getStatusCode().toString()).isEqualTo("404");
            }
        }

        {
            System.out.println("\n****** 63 delete employee2 ***********************************************************\n");

            ResponseEntity response = access.delete("/employees/"+empNo2);

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("204");

            //System.out.println("===> delete with employee: \n" + Utils.toPrettyJson(response));
        }

        {
            System.out.println("\n****** 64 check deletion *************************************************************\n");

            try {
                ResponseEntity response = access.get("/employees/"+empNo2, new TypeReferences.ResourceType<Employee>() {});

                System.out.println("response status code = "+response.getStatusCode().toString());
                assertThat(response.getStatusCode().toString()).isNotEqualTo("200");

            } catch (HttpClientErrorException e) {

                System.out.println("response status code = "+e.getStatusCode().toString());
                assertThat(e.getStatusCode().toString()).isEqualTo("404");
            }
        }

        {
            System.out.println("\n****** 65 delete employee3 ***********************************************************\n");

            ResponseEntity response = access.delete("/employees/"+empNo3);

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("204");

            //System.out.println("===> delete with employee: \n" + Utils.toPrettyJson(response));
        }

        {
            System.out.println("\n****** 66 check deletion *************************************************************\n");

            try {
                ResponseEntity response = access.get("/employees/"+empNo3, new TypeReferences.ResourceType<Employee>() {});

                System.out.println("response status code = "+response.getStatusCode().toString());
                assertThat(response.getStatusCode().toString()).isNotEqualTo("200");

            } catch (HttpClientErrorException e) {

                System.out.println("response status code = "+e.getStatusCode().toString());
                assertThat(e.getStatusCode().toString()).isEqualTo("404");
            }
        }

        System.out.println("\n<= "+this.getClass().getSimpleName());
        System.out.println("****** Done Single Item HalRest Tests ******************************************************\n");
    }

    @Test
    @Sql({"/data.sql"})
    public void MultiItemsTests() {

        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        HalRest access = new HalRest(String.format("http://localhost:%d/ds/v1", port));

        String[] employees_strings = {
                "{\"empNo\":10010,\"firstName\":\"Duangkaew\",\"lastName\":\"Piveteau\",\"gender\":\"F\",\"birthDate\":\"1963-06-01\",\"hireDate\":\"1989-08-24\"}",
                "{\"empNo\":10011,\"firstName\":\"Mary\",\"lastName\":\"Sluis\",\"gender\":\"F\",\"birthDate\":\"1953-11-07\",\"hireDate\":\"1990-01-22\"}",
                "{\"empNo\":10012,\"firstName\":\"Patricio\",\"lastName\":\"Bridgland\",\"gender\":\"M\",\"birthDate\":\"1960-10-04\",\"hireDate\":\"1992-12-18\"}"
        };

        Employee[] employees = new Employee[employees_strings.length];
        for (int i = 0; i < employees_strings.length; i++) {
            employees[i] = Utils.fromString(employees_strings[i], Employee.class);
        }

        /*
        for (int i = 0; i < 3; i++) {
            System.out.println(Utils.toPrettyJson(employees[i]));
        }
        */

        System.out.println("\n=> " + this.getClass().getSimpleName());
        System.out.println("****** Multi Items HalRest Tests ***********************************************************\n");

        {
            System.out.println("\n****** 11 get first page by map **************************************************\n");

            ResponseEntity response = access.get("/");

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("200");

            //System.out.println(Utils.toPrettyJson(response));
            Map body = (Map)response.getBody();
            Map links = (Map) access.getLinks(body);

            assertThat(access.getLinkHref(links, "employees")).isEqualTo(access.getBaseUrl()+"/employees{?page,size,sort}");
            assertThat(access.getLinkHref(links, "deptEmps")).isEqualTo(access.getBaseUrl()+"/deptEmps{?page,size,sort}");
            assertThat(access.getLinkHref(links, "deptManagers")).isEqualTo(access.getBaseUrl()+"/deptManagers{?page,size,sort}");
            assertThat(access.getLinkHref(links, "departments")).isEqualTo(access.getBaseUrl()+"/departments{?page,size,sort}");
            assertThat(access.getLinkHref(links, "salaries")).isEqualTo(access.getBaseUrl()+"/salaries{?page,size,sort}");
            assertThat(access.getLinkHref(links, "titles")).isEqualTo(access.getBaseUrl()+"/titles{?page,size,sort}");
            assertThat(access.getLinkHref(links, "profile")).isEqualTo(access.getBaseUrl()+"/profile");
        }

        {
            System.out.println("\n****** 12 get first page by resource *********************************************\n");

            ResponseEntity<Resource<Employee>> response = access.get("/", new TypeReferences.ResourceType<Employee>() {});

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("200");

            //System.out.println(Utils.toPrettyJson(response));
            Resource<Employee> body = response.getBody();
            List<Link> links = body.getLinks();

            assertThat(access.getLinkHref(links, "employees")).isEqualTo(access.getBaseUrl()+"/employees{?page,size,sort}");
            assertThat(access.getLinkHref(links, "deptEmps")).isEqualTo(access.getBaseUrl()+"/deptEmps{?page,size,sort}");
            assertThat(access.getLinkHref(links, "deptManagers")).isEqualTo(access.getBaseUrl()+"/deptManagers{?page,size,sort}");
            assertThat(access.getLinkHref(links, "departments")).isEqualTo(access.getBaseUrl()+"/departments{?page,size,sort}");
            assertThat(access.getLinkHref(links, "salaries")).isEqualTo(access.getBaseUrl()+"/salaries{?page,size,sort}");
            assertThat(access.getLinkHref(links, "titles")).isEqualTo(access.getBaseUrl()+"/titles{?page,size,sort}");
            assertThat(access.getLinkHref(links, "profile")).isEqualTo(access.getBaseUrl()+"/profile");
        }

        {
            System.out.println("\n****** 21 get multiple with map **************************************************\n");

            ResponseEntity response = access.get("/employees?page=3&size=3");

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("200");

            //System.out.println(Utils.toPrettyJson(response));
            Map body = (Map) response.getBody();
            //System.out.println("===> getContent: \n" + Utils.toPrettyJson(access.getContent(body)));
            Collection<Map> resources = (Collection<Map>) access.getContent(body);

            Iterator<Map> iterator = resources.iterator();

            for (int i = 0; i < employees.length; i++) {

                assertThat(iterator.hasNext()).isTrue();
                Map resource = iterator.next();
                Map content = (Map) access.getContent(resource);
                Map links = (Map) access.getLinks(resource);

                //System.out.println("===> getContent: \n" + Utils.toPrettyJson(content));
                assertThat(Utils.toJson(Utils.toObject(content, Employee.class))).isEqualTo(Utils.toJson(employees[i]));

                //System.out.println("===> getLinks: \n" + Utils.toPrettyJson(links));
                long empNo = employees[i].getEmpNo();
                assertThat(access.getLinkHref(links, "self")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo);
                assertThat(access.getLinkHref(links, "employee")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo);
                assertThat(access.getLinkHref(links, "departments")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo+"/departments");
                assertThat(access.getLinkHref(links, "titles")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo+"/titles");
                assertThat(access.getLinkHref(links, "deptEmps")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo+"/deptEmps");
                assertThat(access.getLinkHref(links, "salaries")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo+"/salaries");
            }

            Map links = (Map) access.getLinks(body);
            Map metaData = (Map) access.getMetadata(body);

            //System.out.println("===> getLinks: \n" + Utils.toPrettyJson(links));
            assertThat(access.getLinkHref(links, "first")).isEqualTo(access.getBaseUrl()+"/employees?page=0&size=3");
            assertThat(access.getLinkHref(links, "prev")).isEqualTo(access.getBaseUrl()+"/employees?page=2&size=3");
            assertThat(access.getLinkHref(links, "self")).isEqualTo(access.getBaseUrl()+"/employees{&sort}");
            assertThat(access.getLinkHref(links, "next")).isEqualTo(access.getBaseUrl()+"/employees?page=4&size=3");
            assertThat(access.getLinkHref(links, "last")).isEqualTo(access.getBaseUrl()+"/employees?page=35&size=3");
            assertThat(access.getLinkHref(links, "profile")).isEqualTo(access.getBaseUrl()+"/profile/employees");
            assertThat(access.getLinkHref(links, "search")).isEqualTo(access.getBaseUrl()+"/employees/search");

            //System.out.println("===> getMetadata: \n" + Utils.toPrettyJson(metaData));
            assertThat(metaData.get("size").toString()).isEqualTo("3");
            assertThat(metaData.get("number").toString()).isEqualTo("3");
            assertThat(metaData.get("totalElements").toString()).isEqualTo("107");
            assertThat(metaData.get("totalPages").toString()).isEqualTo("36");
        }

        {
            System.out.println("\n****** 31 get multiple with paged resources **************************************\n");

            ResponseEntity<PagedResources<Resource<Employee>>> response = access.get("/employees?page=3&size=3",
                    new TypeReferences.PagedResourcesType<Resource<Employee>>() {});

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("200");

            //System.out.println(Utils.toPrettyJson(response));
            PagedResources<Resource<Employee>> body = response.getBody();
            //System.out.println("===> getContent: \n" + Utils.toPrettyJson(body.getContent()));
            Collection<Resource<Employee>> resources = body.getContent();

            Iterator<Resource<Employee>> iterator = resources.iterator();

            for (int i = 0; i < employees.length; i++) {

                assertThat(iterator.hasNext()).isTrue();
                Resource<Employee> resource = iterator.next();
                Employee content = resource.getContent();
                List<Link> links = resource.getLinks();

                //System.out.println("===> getContent: \n" + Utils.toJson(content));
                assertThat(Utils.toJson(content)).isEqualTo(Utils.toJson(employees[i]));

                //System.out.println("===> getLinks: \n" + Utils.toPrettyJson(links));
                long empNo = employees[i].getEmpNo();
                assertThat(access.getLinkHref(links, "self")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo);
                assertThat(access.getLinkHref(links, "employee")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo);
                assertThat(access.getLinkHref(links, "departments")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo+"/departments");
                assertThat(access.getLinkHref(links, "titles")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo+"/titles");
                assertThat(access.getLinkHref(links, "deptEmps")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo+"/deptEmps");
                assertThat(access.getLinkHref(links, "salaries")).isEqualTo(access.getBaseUrl()+"/employees/"+empNo+"/salaries");
            }

            List<Link> links = body.getLinks();
            PagedResources.PageMetadata metaData = body.getMetadata();

            //System.out.println("===> getLinks: \n" + Utils.toPrettyJson(links));
            assertThat(access.getLinkHref(links, "first")).isEqualTo(access.getBaseUrl()+"/employees?page=0&size=3");
            assertThat(access.getLinkHref(links, "prev")).isEqualTo(access.getBaseUrl()+"/employees?page=2&size=3");
            assertThat(access.getLinkHref(links, "self")).isEqualTo(access.getBaseUrl()+"/employees{&sort}");
            assertThat(access.getLinkHref(links, "next")).isEqualTo(access.getBaseUrl()+"/employees?page=4&size=3");
            assertThat(access.getLinkHref(links, "last")).isEqualTo(access.getBaseUrl()+"/employees?page=35&size=3");
            assertThat(access.getLinkHref(links, "profile")).isEqualTo(access.getBaseUrl()+"/profile/employees");
            assertThat(access.getLinkHref(links, "search")).isEqualTo(access.getBaseUrl()+"/employees/search");

            //System.out.println("===> getMetadata: \n" + Utils.toPrettyJson(metaData));
            assertThat(metaData.getSize()).isEqualTo(3);
            assertThat(metaData.getNumber()).isEqualTo(3);
            assertThat(metaData.getTotalElements()).isEqualTo(107);
            assertThat(metaData.getTotalPages()).isEqualTo(36);
        }

        System.out.println("\n<= "+this.getClass().getSimpleName());
        System.out.println("****** Done Multi Items HalRest Tests ******************************************************\n");
    }
}

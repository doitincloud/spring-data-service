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

import com.doitincloud.library.employees.DeptEmpId;
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

import com.doitincloud.library.employees.DeptEmp;
import com.doitincloud.library.employees.Department;
import com.doitincloud.library.employees.Employee;

@RunWith(SpringRunner.class)
public class DeptManagerIntegrationTests extends TestBase {

    @Test
    @Sql({"/schema.sql", "/delete-all.sql"})
    public void CRUDTests() {

        setup();

        String apiUrl = apiBaseUrl + "/deptManagers";
        String apiUrlPer;

        System.out.println("\n=> "+this.getClass().getSimpleName());
        System.out.println("****** CRUD Operations Test ****************************************************************\n");

        VerifyApiUrl(apiUrl);

        DeptEmp deptEmp;

        Department department = new Department();
        department.setDeptName("Research in Cloud");

        System.out.println("department = "+ Utils.toJson(department));

        Employee  employee = new Employee();
        employee.setFirstName("Samual");
        employee.setLastName("Adam");
        employee.setBirthDate(java.sql.Date.valueOf("1990-02-12"));
        employee.setGender("M");
        employee.setHireDate(java.sql.Date.valueOf("2017-07-01"));

        System.out.println("employee = "+Utils.toJson(employee));

        {
            System.out.println("\n****** 11 create department **********************************************************\n");

            System.out.println("url = "+apiBaseUrl+"/departments");

            ResponseEntity<Department> response = post(apiBaseUrl+"/departments", department, Department.class);

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("201");

            Department body = response.getBody();

            assertThat(body.getDeptNo()).isNotNull();

            department.setDeptNo(body.getDeptNo());

            readToVerifyTheRecord(apiBaseUrl+"/departments/"+body.getDeptNo(), department);
        }

        {
            System.out.println("\n****** 12 create employee ************************************************************\n");

            System.out.println("url = "+apiBaseUrl+"/employees");

            ResponseEntity<Employee> response = post(apiBaseUrl+"/employees", employee, Employee.class);

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("201");

            Employee body = response.getBody();

            assertThat(body.getEmpNo()).isNotNull();

            employee.setEmpNo(body.getEmpNo());

            readToVerifyTheRecord(apiBaseUrl+"/employees/"+body.getEmpNo(), employee);
        }

        {
            System.out.println("\n****** 13 create a new record ********************************************************\n");

            DeptEmpId deptEmpId = new DeptEmpId(
                    employee.getEmpNo(),
                    department.getDeptNo(),
                    java.sql.Date.valueOf("2017-01-01"),
                    java.sql.Date.valueOf("9999-01-01"));

            System.out.println("url = "+apiUrl);

            ResponseEntity<DeptEmp> response = post(apiUrl, deptEmpId, DeptEmp.class);

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("201");

            DeptEmp body = response.getBody();

            assertThat(body.equals(deptEmpId)).isTrue();

            deptEmp = body;

            apiUrlPer = apiUrl+"/deptNo="+deptEmp.getDeptNo()+"&empNo="+deptEmp.getEmpNo();

            readToVerifyTheRecord(apiUrlPer, deptEmp);
        }

        {
            System.out.println("\n****** 21 edit use put - required all fields *****************************************\n");

            DeptEmpId deptEmpId = new DeptEmpId(deptEmp);
            deptEmpId.setToDate(java.sql.Date.valueOf("2017-09-01"));

            System.out.println("url = "+apiUrlPer);

            ResponseEntity<DeptEmp> response = put(apiUrlPer, deptEmpId, DeptEmp.class);

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("200");

            DeptEmp body = response.getBody();

            assertThat(deptEmpId.equals(body)).isTrue();

            deptEmp.setToDate(java.sql.Date.valueOf("2017-09-01"));

            readToVerifyTheRecord(apiUrlPer, deptEmp);
        }

        {
            System.out.println("\n****** 31 edit with patch - requires only the fields needed to change  ***************\n");

            Map<String, Object> patch = new HashMap<String, Object>();
            patch.put("toDate", java.sql.Date.valueOf("9999-01-01"));

            System.out.println("patch:"+Utils.toJson(patch));

            System.out.println("url = "+apiUrlPer);

            ResponseEntity<DeptEmp> response = patch(apiUrlPer, patch, DeptEmp.class);

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("200");

            DeptEmp body = response.getBody();

            deptEmp.setToDate(java.sql.Date.valueOf("9999-01-01"));

            assertThat(Utils.toJson(body)).isEqualTo(Utils.toJson(deptEmp));

            readToVerifyTheRecord(apiUrlPer, deptEmp);
        }

        {
            System.out.println("\n****** 41 delete *********************************************************************\n");

            System.out.println("url = "+apiUrlPer);

            ResponseEntity response = delete(apiUrlPer);

            System.out.println("response status code = " + response.getStatusCode());
            assertThat(response.getStatusCode().toString()).isEqualTo("204");

            readToVerifyTheDelete(apiUrlPer);
        }

        {
            System.out.println("\n****** 43 delete department **********************************************************\n");

            System.out.println("url = "+apiBaseUrl+"/departments/"+department.getDeptNo());

            ResponseEntity response = delete(apiBaseUrl+"/departments/"+department.getDeptNo());

            System.out.println("response status code = " + response.getStatusCode());
            assertThat(response.getStatusCode().toString()).isEqualTo("204");

            readToVerifyTheDelete(apiBaseUrl+"/departments/"+department.getDeptNo());
        }

        {
            System.out.println("\n****** 44 delete employee ************************************************************\n");

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

        String apiUrl = apiBaseUrl + "/deptManagers";
        int pageSize = 20;

        String apiUrlPer = apiUrl + "/deptNo=d001&empNo=110039";

        System.out.println("\n=> " + this.getClass().getSimpleName());
        System.out.println("****** Tests with Sample Data **************************************************************\n");

        System.out.println("api url = " + apiUrl);

        {
            System.out.println("\n****** 11 read one item **************************************************************\n");

            System.out.println("url = "+apiUrlPer);

            ResponseEntity<Resource<DeptEmp>> response = get(apiUrlPer, new TypeReferences.ResourceType<DeptEmp>() {});

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("200");

            Resource<DeptEmp> resource = response.getBody();
            DeptEmp deptManager = resource.getContent();

            assertThat(resource.hasLink(Link.REL_SELF)).isTrue();
            assertThat(resource.hasLink("deptManager")).isTrue();
            assertThat(resource.getLink("deptManager").getHref()).isEqualTo(resource.getLink(Link.REL_SELF).getHref());
            assertThat(resource.getLink("deptManager").getHref()).isEqualTo(apiUrlPer);

            assertThat(deptManager.getDeptNo()).isEqualTo("d001");
            assertThat(deptManager.getEmpNo()).isEqualTo(110039);
            assertThat(deptManager.getFromDate()).isEqualTo(java.sql.Date.valueOf("1991-10-01"));
            assertThat(deptManager.getToDate()).isEqualTo(java.sql.Date.valueOf("9999-01-01"));

            FollowLinks(resource.getLinks(), "deptManager");
        }

        {
            System.out.println("\n****** 21 first view of items list ***************************************************\n");

            System.out.println("url = "+apiUrl+"?size="+pageSize);

            ResponseEntity<PagedResources<Resource<DeptEmp>>> response = get(apiUrl+"?size="+pageSize,
                    new TypeReferences.PagedResourcesType<Resource<DeptEmp>>() {});

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("200");

            PagedResources<Resource<DeptEmp>> resources = response.getBody();

            Collection<Resource<DeptEmp>> content = resources.getContent();

            assertThat(content.size()).isLessThanOrEqualTo(pageSize);

            FollowLinks(resources.getLinks(), "deptManager");
        }

        System.out.println("\n<= "+this.getClass().getSimpleName());
        System.out.println("****** Done Tests With Sample Data *********************************************************\n");
    }
}

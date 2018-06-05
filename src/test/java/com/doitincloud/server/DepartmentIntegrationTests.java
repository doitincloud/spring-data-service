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
import org.springframework.hateoas.*;
import org.springframework.hateoas.mvc.TypeReferences;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

import com.doitincloud.library.employees.Department;

@RunWith(SpringRunner.class)
public class DepartmentIntegrationTests extends TestBase {

    @Test
    @Sql({"/schema.sql", "/delete-all.sql"})
    public void CRUDTests() {

        setup();

        String apiUrl = apiBaseUrl + "/departments";
        String apiUrlPer;
        Department department;

        System.out.println("\n=> "+this.getClass().getSimpleName());
        System.out.println("****** CRUD Operations Test ****************************************************************\n");

        VerifyApiUrl(apiUrl);

        department = new Department();
        department.setDeptName("Research in Cloud");

        System.out.println("department = "+ Utils.toJson(department));

        {
            System.out.println("\n****** 11 create a new record ********************************************************\n");

            System.out.println("url = "+apiUrl);

            ResponseEntity<Department> response = post(apiUrl, department, Department.class);

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("201");

            Department body = response.getBody();

            assertThat(body.getDeptNo()).isNotNull();

            department.setDeptNo(body.getDeptNo());
            apiUrlPer = apiUrl+"/"+department.getDeptNo();

            readToVerifyTheRecord(apiUrlPer, department);
        }

        {
            System.out.println("\n****** 21 update use put - required all fields ***************************************\n");

            department.setDeptName("Research In Deep Cloud");

            System.out.println("url = "+apiUrlPer);

            ResponseEntity<Department> response = put(apiUrlPer, department, Department.class);

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("200");

            Department body = response.getBody();

            assertThat(department.equals(body)).isTrue();

            readToVerifyTheRecord(apiUrlPer, department);
        }

        {
            System.out.println("\n****** 31 edit with patch - requires only the fields needed to change  ***************\n");

            Department department1 = new Department(null, "Research In Cloud");

            System.out.println("department1:"+Utils.toJson(department1));

            System.out.println("url = "+apiUrlPer);

            ResponseEntity<Department> response = patch(apiUrlPer, department1, Department.class);

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("200");

            Department body = response.getBody();

            assertThat(body.getDeptName()).isEqualTo("Research In Cloud");

            department.setDeptName("Research In Cloud");

            readToVerifyTheRecord(apiUrlPer, department);
        }

        {
            System.out.println("\n****** 41 delete *********************************************************************\n");

            System.out.println("url = "+apiUrlPer);

            ResponseEntity response = delete(apiUrlPer);

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("204");

            readToVerifyTheDelete(apiUrlPer);
        }

        System.out.println("\n<= "+this.getClass().getSimpleName());
        System.out.println("****** Done with CRUD Tests ****************************************************************\n");
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    public void TestsWithSampleData() {

        setup();

        String apiUrl = apiBaseUrl + "/departments";
        int pageSize = 20;

        String apiUrlPer = apiUrl + "/d001";

        System.out.println("\n=> "+this.getClass().getSimpleName());
        System.out.println("****** Tests With Sample Data **************************************************************\n");

        System.out.println("api url = " + apiUrl);

        {
            System.out.println("\n****** 11 read one item **************************************************************\n");

            System.out.println("url = "+apiUrlPer);

            ResponseEntity<Resource<Department>> response = get(apiUrlPer,
                    new TypeReferences.ResourceType<Department>() {});

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("200");

            Resource<Department> resource = response.getBody();
            Department department = resource.getContent();

            assertThat(resource.hasLink(Link.REL_SELF)).isTrue();
            assertThat(resource.hasLink("department")).isTrue();
            assertThat(resource.getLink("department").getHref())
                    .isEqualTo(resource.getLink(Link.REL_SELF).getHref());
            assertThat(resource.getLink("department").getHref()).isEqualTo(apiUrlPer);

            assertThat(department.getDeptNo()).isEqualTo("d001");
            assertThat(department.getDeptName()).isEqualTo("Marketing");

            FollowLinks(resource.getLinks(), "department");
        }

        {
            System.out.println("\n****** 21 first view of items list ***************************************************\n");

            System.out.println("url = "+apiUrl+"?size="+pageSize);

            ResponseEntity<PagedResources<Resource<Department>>> response = get(apiUrl+"?size="+pageSize,
                    new TypeReferences.PagedResourcesType<Resource<Department>>() {});

            System.out.println("response status code = "+response.getStatusCode().toString());
            assertThat(response.getStatusCode().toString()).isEqualTo("200");

            PagedResources<Resource<Department>> resources = response.getBody();

            Collection<Resource<Department>> content = resources.getContent();

            assertThat(content.size()).isLessThanOrEqualTo(pageSize);

            FollowLinks(resources.getLinks(), "department");
        }

        System.out.println("\n<= "+this.getClass().getSimpleName());
        System.out.println("****** Done Tests With Sample Data *********************************************************\n");
    }

}

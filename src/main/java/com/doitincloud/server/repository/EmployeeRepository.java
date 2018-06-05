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

package com.doitincloud.server.repository;

import com.doitincloud.server.entity.Employee;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import java.sql.Date;

// ref: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/

@RepositoryRestResource
public interface EmployeeRepository extends PagingAndSortingRepository<Employee, Long>, EmployeeExRepository {

    // http://localhost:8080/ds/v1/employees/search/maxId
    @Query(value = "SELECT MAX(e.id) FROM Employee e")
    public long maxId();

    // http://localhost:8080/ds/v1/employees/search/findByLastName?name=Leonhardt
    public Page<Employee> findByLastName(@Param("name") String name, Pageable pageable);

    // http://localhost:8080/ds/v1/employees/search/findByLastNameLike?name=ch
    @Query(value = "FROM Employee e WHERE e.lastName LIKE %:name%")
    public Page<Employee> findByLastNameLike(@Param("name") String name, Pageable pageable);

    // http://localhost:8080/ds/v1/employees/search/findByLastNameNotLike?name=ch
    @Query(value = "FROM Employee e WHERE e.lastName NOT LIKE %:name%")
    public Page<Employee> findByLastNameNotLike(@Param("name") String name, Pageable pageable);

    // http://localhost:8080/ds/v1/employees/search/birthdaysOfMonth?today=2017-07-01
    @Query(value = "FROM Employee e WHERE 0 = 1")  // fake SQL
    // method is implemented in EmployeeExRepositoryImp
    public Page<Employee> birthdaysOfMonth(@Param("today") Date today, Pageable pageable);

    // http://localhost:8080/ds/v1/employees/search/birthdaysOfTheMonth
    @Query(value = "FROM Employee e WHERE 0 = 1")  // fake SQL
    // method is implemented in EmployeeExRepositoryImp
    public Page<Employee> birthdaysOfTheMonth(Pageable pageable);
}

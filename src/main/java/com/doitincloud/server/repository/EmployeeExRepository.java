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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.sql.Date;

public interface EmployeeExRepository {

    Page<Employee> birthdaysOfMonth(@Param("today") Date today, Pageable pageable);

    Page<Employee> birthdaysOfTheMonth(Pageable pageable);

}
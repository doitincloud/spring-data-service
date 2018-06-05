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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.query.QueryUtils;

import java.math.BigInteger;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EmployeeRepositoryImpl implements EmployeeExRepository {

    @PersistenceContext
    private EntityManager entityManager;

    // use native mysql query
    // issues:
    // 1) entity class needs constructor from Object array
    // 2) have to use database field names instead of attribute names, not consisted with conventions
    //
    @Override
    public Page<Employee> birthdaysOfMonth(@Param("today") Date today, Pageable pageable)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        int month = cal.get(Calendar.MONTH)+1;

        String sql = QueryUtils.applySorting("From employees WHERE MONTH(birth_date) = :month", pageable.getSort());

        Query countQuery = entityManager.createNativeQuery("SELECT COUNT(*) "+sql)
            .setParameter("month", month);
        long total = ((BigInteger) countQuery.getSingleResult()).longValue();;

        Query query = entityManager.createNativeQuery("SELECT * "+sql)
                .setParameter("month", month)
                .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
                .setMaxResults(pageable.getPageSize());
        List<Object[]> results = query.getResultList();

        List<Employee> employees = new ArrayList<Employee>();
        results.stream().forEach((record) -> {
              Employee employee = new Employee(record);
              employees.add(employee);
        });

        Page<Employee> page = new PageImpl<Employee>(employees, pageable, total);

        return page;
    }

    // use jpql query saves code
    // issues
    // 1) not flexible enough, need to pickup the knowledge
    //
    @Override
    public Page<Employee> birthdaysOfTheMonth(Pageable pageable)
    {
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH)+1;

        String jpql = QueryUtils.applySorting("From Employee e WHERE MONTH(e.birthDate) = :month", pageable.getSort(), "e");

        TypedQuery<Long> countQuery = entityManager.createQuery("SELECT COUNT(e) "+jpql, Long.class)
                .setParameter("month", month);
        long total = countQuery.getSingleResult();

        TypedQuery<Employee> query = entityManager.createQuery(jpql, Employee.class)
                .setParameter("month", month)
                .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
                .setMaxResults(pageable.getPageSize());
        List<Employee> employees = query.getResultList();

        Page<Employee> page = new PageImpl<Employee>(employees, pageable, total);

        return page;
    }
}
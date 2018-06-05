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

package com.doitincloud.server.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Date;
import java.util.Set;

@Entity
@Table(name="employees")
public class Employee {

    @Id
    @Column(name = "emp_no", nullable = false)
    @GenericGenerator(name="empIdGenerator", strategy="com.doitincloud.server.entity.EmpIdGenerator")
    @GeneratedValue(generator="empIdGenerator")
    private long id;

    @Column(name = "birth_date")
    @JsonFormat(pattern="yyyy-MM-dd", timezone = "UTC")
    private Date birthDate;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "gender")
    private String gender;

    @Column(name = "hire_date")
    @JsonFormat(pattern="yyyy-MM-dd", timezone = "UTC")
    private Date hireDate;

    public Employee(long id, Date birthDate, String firstName, String lastName, String gender, Date hireDate) {
        this.id = id;
        this.birthDate = birthDate;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.hireDate = hireDate;
    }

    public Employee(long id) {
    this.id = id;
    }

    public Employee() { }

    // to convert a native mysql result row
    //
    public Employee(Object[] row) {
        id = Long.parseLong(row[0].toString());
        birthDate = (Date) row[1];
        firstName = (String) row[2];
        lastName = (String) row[3];
        gender = row[4].toString();
        hireDate = (Date) row[5];
    }

    public long getId() {
    return id;
    }

    public void setId(long id) {
    this.id = id;
    }

    public Date getBirthDate() {
    return birthDate;
    }

    public void setBirthDate(Date birthDate) {
    this.birthDate = birthDate;
    }

    public String getFirstName() {
    return firstName;
    }

    public void setFirstName(String firstName) {
    this.firstName = firstName;
    }

    public String getLastName() {
    return lastName;
    }

    public void setLastName(String lastName) {
    this.lastName = lastName;
    }

    public String getGender() {
    return gender;
    }

    public void setGender(String gender) {
    this.gender = gender;
    }

    public Date getHireDate() {
    return hireDate;
    }

    public void setHireDate(Date hireDate) {
    this.hireDate = hireDate;
    }

    // more derivatives, associations and annotations
    //

    @JsonInclude
    public long getEmpNo() {
        return id;
    }

    public void setEmpNo(long empNo) {
        this.id = empNo;
    }

    @OneToMany
    @JoinColumn(name = "emp_no")
    @OrderBy("to_date DESC")
    private Set<Title> titles;

    public Set<Title> getTitles() {
    return titles;
    }

    public void setTitles(Set<Title> titles) {
    this.titles = titles;
    }

    @OneToMany
    @JoinColumn(name = "emp_no")
    @OrderBy("to_date DESC")
    private Set<DeptEmp> deptEmps;

    public Set<DeptEmp> getDeptEmps() {
    return deptEmps;
    }

    public void setDeptEmps(Set<DeptEmp> deptEmps) {
    this.deptEmps = deptEmps;
    }

    @OneToMany
    @JoinColumn(name = "emp_no")
    @OrderBy("to_date DESC")
    private Set<Salary> salaries;

    public Set<Salary> getSalaries() {
    return salaries;
    }

    public void setSalaries(Set<Salary> salaries) {
    this.salaries = salaries;
    }

    //* works, but better covered by the one use deptEmps
    @ManyToMany
    @JoinTable(name = "dept_emp",
          joinColumns = @JoinColumn(name = "emp_no", referencedColumnName = "emp_no"),
          inverseJoinColumns = @JoinColumn(name = "dept_no", referencedColumnName = "dept_no"))
    private Set<Department> departments;

    public Set<Department> getDepartments() {
    return departments;
    }

    public void setDepartments(Set<Department> mmDepartments) {
    this.departments = departments;
    }
}

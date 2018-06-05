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

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name="departments")
public class Department {

    @Id
    @GenericGenerator(name="deptIdGenerator", strategy="com.doitincloud.server.entity.DeptIdGenerator")
    @GeneratedValue(generator="deptIdGenerator")
    @Column(name="dept_no", nullable = false)
    private String id;

    @Column(name="dept_name")
    private String deptName;

    public Department(String id, String deptName) {
        this.id = id;
        this.deptName = deptName;
    }

    public Department(String id) {
        this.id = id;
    }

    public Department() { }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    // more derivatives, associations and annotations
    //

    @JsonInclude
    public String getDeptNo() { return id; }

    public void setDeptNo(String deptNo) { id = deptNo; }

    @OneToMany
    @JoinColumn(name="dept_no")
    @OrderBy("to_date DESC")
    private Set<DeptManager> deptManagers;

    public Set<DeptManager> getDeptManagers() { return deptManagers; }

    public void setDeptManagers(Set<DeptManager> deptManagers) { this.deptManagers = deptManagers; }

    @OneToMany
    @JoinColumn(name="dept_no")
    @OrderBy("to_date DESC")
    private Set<DeptEmp> deptEmps;

    public Set<DeptEmp> getDeptEmps() { return deptEmps; }

    public void setDeptEmps(Set<DeptEmp> deptEmps) { this.deptEmps = deptEmps; }

    @ManyToMany
    @JoinTable(name = "dept_manager", joinColumns = @JoinColumn(name = "dept_no", referencedColumnName = "dept_no"),
          inverseJoinColumns = @JoinColumn(name = "emp_no", referencedColumnName = "emp_no"))
    private Set<Employee> managers;

    public Set<Employee> getManagers() {
        return managers;
    }

    public void setManagers(Set<Employee> managers) {
        this.managers = managers;
    }

    @ManyToMany
    @JoinTable(name = "dept_emp", joinColumns = @JoinColumn(name = "dept_no", referencedColumnName = "dept_no"),
          inverseJoinColumns = @JoinColumn(name = "emp_no", referencedColumnName = "emp_no"))
    private Set<Employee> employees;

    public Set<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(Set<Employee> employees) {
        this.employees = employees;
    }
}

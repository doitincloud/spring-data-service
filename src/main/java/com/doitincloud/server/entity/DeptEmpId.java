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

import com.doitincloud.library.helper.Utils;
import com.fasterxml.jackson.annotation.JsonRootName;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Map;

@Embeddable
@JsonRootName("id")
public class DeptEmpId implements Serializable {

    @ManyToOne
    @JoinColumn (name="dept_no", nullable = false)
    private Department department;

    @ManyToOne
    @JoinColumn (name="emp_no", nullable = false)
    private Employee employee;

    public DeptEmpId(Department department, Employee employee) {
        this.department = department;
        this.employee = employee;
    }

    public DeptEmpId(String query) {
        Map<String, String> params = Utils.getQueryParams(query);
        department = new Department(params.get("deptNo"));
        employee = new Employee(Long.parseLong(params.get("empNo")));
    }

    public DeptEmpId(String deptNo, long empNo) {
        department = new Department(deptNo);
        employee = new Employee(empNo);
    }

    public DeptEmpId() { }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getDeptNo() {
        if (null == department) return null;
        return department.getId();
    }

    public void setDeptNo(String deptNo) {
        if (null == department) department = new Department(deptNo);
        else department.setId(deptNo);
    }

    public long getEmpNo() {
        if (null == employee) return 0L;
        return employee.getId();
    }

    public void setEmpNo(long empNo) {
        if (null == employee) employee = new Employee(empNo);
        else employee.setId(empNo);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeptEmpId)) return false;

        DeptEmpId deptEmpId = (DeptEmpId) o;

        if (!getDepartment().equals(deptEmpId.getDepartment())) return false;
        return getEmployee().equals(deptEmpId.getEmployee());
    }

    @Override
    public int hashCode() {
        int result = getDepartment().hashCode();
        result = 31 * result + getEmployee().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "deptNo=" + department.getId() +
                "&empNo=" + employee.getId();
    }
}

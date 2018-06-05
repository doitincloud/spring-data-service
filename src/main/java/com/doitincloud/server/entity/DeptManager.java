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

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name="dept_manager")
public class DeptManager {

    @EmbeddedId
    private DeptEmpId id;

    @Column(name="from_date")
    @JsonFormat(pattern="yyyy-MM-dd", timezone = "UTC")
    private Date fromDate;

    @Column(name="to_date")
    @JsonFormat(pattern="yyyy-MM-dd", timezone = "UTC")
    private Date toDate;

    public DeptManager(DeptEmpId id, Date fromDate, Date toDate) {
        this.id = id;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public DeptManager(String deptNo, long empNo, Date fromDate, Date toDate) {
        this.id = new DeptEmpId(new Department(deptNo), new Employee(empNo));
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public DeptManager() { }

    public DeptEmpId getId() {
        return id;
    }

    public void setId(DeptEmpId id) {
        this.id = id;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    // additional derivative properties
    //

    public String getDeptNo() { return id.getDepartment().getId(); }

    public void setDeptNo(String deptNo) { id.getDepartment().setId(deptNo); }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getDeptName() { return id.getDepartment().getDeptName(); }

    public void setDeptName(String deptName) { id.getDepartment().setDeptName(deptName); }

    public long getEmpNo() { return id.getEmployee().getId(); }

    public void setEmpNo(long empNo) { id.getEmployee().setId(empNo); }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getEmpFirstName() { return id.getEmployee().getFirstName(); }

    public void setEmpFirstName(String firstName) { id.getEmployee().setFirstName(firstName); }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getEmpLastName() { return id.getEmployee().getLastName(); }

    public void setEmpLastName(String lastName) { id.getEmployee().setLastName(lastName); }
}

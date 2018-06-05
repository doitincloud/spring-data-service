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

package com.doitincloud.library.employees;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.sql.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "deptNo", "deptName", "empNo", "empFirstName", "empLastName", "fromDate", "toDate"})
public class DeptEmp {

    private long empNo;

    private String deptNo;

    private Date fromDate;

    private Date toDate;

    private String empFirstName;

    private String empLastName;

    private String deptName;

    public DeptEmp() {
    }

    public DeptEmp(long empNo, String deptNo, Date fromDate, Date toDate) {
        this.empNo = empNo;
        this.deptNo = deptNo;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public long getEmpNo() { return empNo; }

    public void setEmpNo(long empNo) {
        this.empNo = empNo;
    }

    public String getDeptNo() {
        return deptNo;
    }

    public void setDeptNo(String deptNo) {
        this.deptNo = deptNo;
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

    public String getEmpFirstName() {
        return empFirstName;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public void setEmpFirstName(String empFirstName) {
        this.empFirstName = empFirstName;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getEmpLastName() {
        return empLastName;
    }

    public void setEmpLastName(String empLastName) {
        this.empLastName = empLastName;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeptEmp)) return false;

        DeptEmp that = (DeptEmp) o;
        if (getEmpNo() != that.getEmpNo()) return false;
        if (!getDeptNo().equals(that.getDeptNo())) return false;
        if (!getFromDate().equals(that.getFromDate())) return false;
        return getToDate().equals(that.getToDate());
    }

    @Override
    public int hashCode() {
        int result = (int) (getEmpNo() ^ (getEmpNo() >>> 32));
        result = 31 * result + getDeptNo().hashCode();
        result = 31 * result + getFromDate().hashCode();
        result = 31 * result + getToDate().hashCode();
        return result;
    }
}

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
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.util.Map;

@Embeddable
public class SalaryId implements Serializable {

    @ManyToOne
    @JoinColumn(name="emp_no", nullable = false)
    private Employee employee;

    @Column(name="from_date", nullable = false)
    @JsonFormat(pattern="yyyy-MM-dd", timezone = "UTC")
    private Date fromDate;

    public SalaryId(Employee employee, Date fromDate) {
        this.employee = employee;
        this.fromDate = fromDate;
    }

    public SalaryId(String query) {
        Map<String, String> params = Utils.getQueryParams(query);
        this.employee = new Employee(Long.parseLong(params.get("empNo")));
        this.fromDate = Date.valueOf(params.get("fromDate"));
    }

    public SalaryId() {
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
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
        if (!(o instanceof SalaryId)) return false;

        SalaryId salaryId = (SalaryId) o;

        if (!getEmployee().equals(salaryId.getEmployee())) return false;
        return getFromDate().equals(salaryId.getFromDate());
    }

    @Override
    public int hashCode() {
        int result = getEmployee().hashCode();
        result = 31 * result + getFromDate().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "empNo=" + employee.getId() +
                "&fromDate=" + fromDate;
    }
}

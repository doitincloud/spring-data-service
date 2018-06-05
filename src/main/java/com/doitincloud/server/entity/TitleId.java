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
import java.util.*;

@Embeddable
public class TitleId implements Serializable {

    @ManyToOne
    @JoinColumn(name = "emp_no", nullable = false)
    private Employee employee;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "from_date", nullable = false)
    @JsonFormat(pattern="yyyy-MM-dd", timezone = "UTC")
    private Date fromDate;

    public TitleId(Employee employee, String title, Date fromDate) {
        this.employee = employee;
        this.title = title;
        this.fromDate = fromDate;
    }

    public TitleId(String query) {
        Map<String, String> params = Utils.getQueryParams(query);
        employee = new Employee(Long.parseLong(params.get("empNo")));
        title = params.get("title");
        fromDate = Date.valueOf(params.get("fromDate"));
    }

    public TitleId(long empNo, String title, Date fromDate) {
        employee = new Employee(empNo);
        this.title = title;
        this.fromDate = fromDate;
    }

    public TitleId() { }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
        if (!(o instanceof TitleId)) return false;

        TitleId titleId = (TitleId) o;

        if (getEmployee() != null ? !getEmployee().equals(titleId.getEmployee()) : titleId.getEmployee() != null)
            return false;
        if (getTitle() != null ? !getTitle().equals(titleId.getTitle()) : titleId.getTitle() != null) return false;
        return getFromDate() != null ? getFromDate().equals(titleId.getFromDate()) : titleId.getFromDate() == null;
    }

    @Override
    public int hashCode() {
        int result = getEmployee() != null ? getEmployee().hashCode() : 0;
        result = 31 * result + (getTitle() != null ? getTitle().hashCode() : 0);
        result = 31 * result + (getFromDate() != null ? getFromDate().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "empNo=" + getEmployee().getId() +
                "&title=" + Utils.encode(title) +
                "&fromDate=" + fromDate;
    }
}

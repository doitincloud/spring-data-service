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
@JsonPropertyOrder({ "empNo", "fromDate", "salary", "toDate"})
public class Salary {

    private long empNo;

    private Date fromDate;

    private long salary;

    private Date toDate;

    public Salary() {
    }

    public Salary(long empNo, Date fromDate, long salary, Date toDate) {
        this.empNo = empNo;
        this.fromDate = fromDate;
        this.salary = salary;
        this.toDate = toDate;
    }

    public long getEmpNo() {
        return empNo;
    }

    public void setEmpNo(long empNo) {
        this.empNo = empNo;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public long getSalary() {
        return salary;
    }

    public void setSalary(long salary) {
        this.salary = salary;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Salary)) return false;

        Salary that = (Salary) o;
        if (getEmpNo() != that.getEmpNo()) return false;
        if (getSalary() != that.getSalary()) return false;
        if (!getFromDate().equals(that.getFromDate())) return false;
        return getToDate().equals(that.getToDate());
    }

    @Override
    public int hashCode() {
        int result = (int) (getEmpNo() ^ (getEmpNo() >>> 32));
        result = 31 * result + getFromDate().hashCode();
        result = 31 * result + (int) (getSalary() ^ (getSalary() >>> 32));
        result = 31 * result + getToDate().hashCode();
        return result;
    }
}

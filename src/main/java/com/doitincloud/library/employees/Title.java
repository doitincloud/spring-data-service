
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
@JsonPropertyOrder({ "empNo", "title", "fromDate", "toDate"} )
public class Title {

    private long empNo;

    private String title;

    private Date fromDate;

    private Date toDate;

    public Title() {
    }

    public Title(long empNo, String title, Date fromDate, Date toDate) {
        this.empNo = empNo;
        this.title = title;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public long getEmpNo() {
        return empNo;
    }

    public void setEmpNo(long empNo) {
        this.empNo = empNo;
    }

    public String getTitle() {
        return this.title;
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

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Title)) return false;

        Title that = (Title) o;
        if (getEmpNo() != that.getEmpNo()) return false;
        if (!getTitle().equals(that.getTitle())) return false;
        if (!getFromDate().equals(that.getFromDate())) return false;
        return getToDate().equals(that.getToDate());
    }

    @Override
    public int hashCode() {
        int result = (int) (getEmpNo() ^ (getEmpNo() >>> 32));
        result = 31 * result + getTitle().hashCode();
        result = 31 * result + getFromDate().hashCode();
        result = 31 * result + getToDate().hashCode();
        return result;
    }
}

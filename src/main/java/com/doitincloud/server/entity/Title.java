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
@Table(name="titles")
public class Title {

    @EmbeddedId
    private TitleId id;

    @Column(name="to_date")
    @JsonFormat(pattern="yyyy-MM-dd", timezone = "UTC")
    private Date toDate;

    public Title(TitleId id, Date toDate) {
        this.id = id;
        this.toDate = toDate;
    }

    public Title(long empNo, String title, Date fromDate, Date toDate) {
        this.id = new TitleId(new Employee(empNo), title, fromDate);
        this.toDate = toDate;
    }

    public Title() {
    }

    public TitleId getId() {
        return id;
    }

    public void setId(TitleId id) {
        this.id = id;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    // additional derivative properties
    //

    public long getEmpNo() { return id.getEmployee().getId(); }

    public void setEmpNo(long empNo) { id.getEmployee().setId(empNo); }

    public String getTitle() {
        return id.getTitle();
    }

    public void setTitle(String title) { id.setTitle(title); }

    public Date getFromDate() { return id.getFromDate(); }

    public void setFromDate(Date fromDate) { id.setFromDate(fromDate); }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getEmpFirstName() { return id.getEmployee().getFirstName(); }

    public void setEmpFirstName(String firstName) { id.getEmployee().setFirstName(firstName); }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getEmpLastName() { return id.getEmployee().getLastName(); }

    public void setEmpLastName(String lastName) { id.getEmployee().setLastName(lastName); }
}
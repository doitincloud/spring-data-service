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

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

public class SalaryId extends Salary {

    private Map<String, Object> id;

    private SalaryId() { }

    public SalaryId(long empNo, Date fromDate, long salary, Date toDate) {
        super(empNo, fromDate, salary, toDate);
        id = new HashMap<String, Object>();
        id.put("empNo", empNo);
        id.put("fromDate", fromDate);
    }

    public SalaryId(Salary salary) {
        super(salary.getEmpNo(), salary.getFromDate(), salary.getSalary(), salary.getToDate());
        id = new HashMap<String, Object>();
        id.put("empNo", salary.getEmpNo());
        id.put("fromDate", salary.getFromDate());
    }

    public Map<String, Object> getId() { return id; };

    @JsonIgnore
    public long getEmpNo() {
        return super.getEmpNo();
    }

    @JsonIgnore
    public Date getFromDate() {
        return super.getFromDate();
    }
}
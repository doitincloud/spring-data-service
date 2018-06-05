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

public class DeptEmpId extends  DeptEmp {

    private Map<String, Object> id;

    private DeptEmpId() { }

    public DeptEmpId(long empNo, String deptNo, Date fromDate, Date toDate) {
        super(empNo, deptNo, fromDate, toDate);
        id = new HashMap<String, Object>();
        id.put("deptNo", deptNo);
        id.put("empNo", new Long(empNo));
    }

    public DeptEmpId(DeptEmp deptEmp) {
        super(deptEmp.getEmpNo(), deptEmp.getDeptNo(), deptEmp.getFromDate(), deptEmp.getToDate());
        id = new HashMap<String, Object>();
        id.put("deptNo", deptEmp.getDeptNo());
        id.put("empNo", new Long(deptEmp.getEmpNo()));
    }

    public Map<String, Object> getId() { return id; };

    @JsonIgnore
    public long getEmpNo() { return super.getEmpNo(); }

    @JsonIgnore
    public String getDeptNo() {
        return super.getDeptNo();
    }

}

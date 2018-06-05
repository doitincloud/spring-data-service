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

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.hateoas.Identifiable;

import java.io.Serializable;

public class DeptIdGenerator implements IdentifierGenerator {

    @Override
    public Serializable generate(SessionImplementor session, Object obj) throws HibernateException {

      if (obj instanceof Identifiable) {
          Identifiable identifiable = (Identifiable) obj;
          Serializable id = identifiable.getId();
          if (id != null) {
              return id;
            }
        }
        String s = (String) Session.class.cast(session)
                .createSQLQuery("SELECT MAX(dept_no) FROM departments")
                .uniqueResult();
        long max = 0L;
        if (null != s) {
            max = Long.valueOf(s.substring(1, s.length()));
        }
        String id = "d" + String.format("%03d", ++max);
        //System.out.println("*** max = "+s+ "   new max = "+id);
        return id;
    }

}

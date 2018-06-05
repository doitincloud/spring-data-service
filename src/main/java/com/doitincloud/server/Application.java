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

package com.doitincloud.server;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.TimeZone;

@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    private DataSource dataSource;

    @Value("${spring.data.rest.base-path}")
    private String basePath;

    @Autowired
    Environment environment;

    // to remove timezone side effect
    void started() { TimeZone.setDefault(TimeZone.getTimeZone("UTC")); }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        HikariDataSource ds = (HikariDataSource)dataSource;
        System.out.println("data source url = " + ds.getJdbcUrl());
        System.out.println("data source pool size = " + ds.getMaximumPoolSize());
        //System.out.println("data source user name = " + ds.getUsername());

        System.out.println("base path = " + basePath);
        String port = environment.getProperty("local.server.port");
        System.out.println("port = "+ port);

    }
}

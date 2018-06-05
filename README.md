# A Spring Boot Based Data Service

a preferred architecture is to have a data service to separate database from rest of a system. The data service provides RESTful API services to other services. It hides database related details such as schema change, connection pool, high availability, load balance, auto scale and data caching etc from the rest. The data service should provide the same functions as direct SQL queries can do. No direct database connections are allowed for rest of the system.

<img width="877" alt="dataservice" src="https://user-images.githubusercontent.com/24588196/29485771-b2a3e9ac-84a5-11e7-8207-f8f544e94164.png">

However, decoupling database of an existing system is an import decision. It involves substantial amount of implementation effort. Even for a newly started project, the initial main focus is to make it works, it can ends up with every components have its own way to access databases. 

The purpose of this project is to demonstrate how easy can a real data service be implemented with Spring Boot. It features with Hypermedia-Driven RESTful APIs or HATEOAS by default.

# Conclusion
By using Spring Boot, techniques demonstrated in this project and in <a href="https://github.com/samw2017/aws-data-service">AWS Data Service</a> project, a Hypermedia-Driven RESTful (HATEOAS) Data Service can be easily implemented within time frame of days.

## Getting Started

Here are environment variable names for database configuration:
<pre>
DB_HOST_NAME
DB_USER_NAME
DB_USER_PASS
</pre>

I use Docker as DevOps environment. Docker makes it possible for almost anyone to see the project alive in few minutes. My Docker version is 17.06.0-ce and I test on both Mac OS and Windows 10. To install Docker, please refer to www.docker.com.

If you have Docker installed correctly and follow the instructions step by step, it takes less than 10 minutes to see the project alive.

NOTICE: Lots of downloads will happen in the process. If you plan to do it many times, please refer to Development section in <a href="https://github.com/samw2017/java-spring-docker">Java Spring Docker</a> project.

<b>Step 1</b> Run mysql docker image

Ref: https://hub.docker.com/r/mysql/mysql-server

<pre>
# If you have a local mysql server is running, you will need to stop it to free the 3306 port.
docker run -d --rm --name=database-server -e MYSQL_ROOT_PASSWORD=newPassword -e MYSQL_ROOT_HOST="%" -e MYSQL_DATABASE=employees -p 3306:3306 mysql/mysql-server:latest
  
docker ps
# you see the Docker container is running:
CONTAINER ID        IMAGE                       COMMAND                  CREATED             STATUS                            PORTS                               NAMES
c4507b89f5ec        mysql/mysql-server:latest   "/entrypoint.sh my..."   10 seconds ago      Up 9 seconds (health: starting)   0.0.0.0:3306->3306/tcp, 33060/tcp   data
</pre>


<b>Step 2</b> Run java-spring docker image

Ref: https://github.com/samw2017/java-spring-docker

<pre>
# If you have a local server is running on port 8080, you will need to stop it.
docker run -it --rm --name=data-service --link database-server:database-server -e DB_HOST_NAME=database-server -e DB_USER_NAME=root -e DB_USER_PASS=newPassword -p 8080:8080 samwen2017/java-spring:latest sh

# you are in the docker virtual machine now, you see console prompt:
/workspace #
</pre>

<b>Step 3</b> Get source, test and play within the docker machine

<pre>
git clone https://github.com/samw2017/spring-data-service.git
cd spring-data-service

# run test
mvn clean test
</pre>
<details><summary>Results:</summary>
<pre>
......
Tests run: 14, Failures: 0, Errors: 0, Skipped: 0

[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 31.193 s
[INFO] Finished at: 2017-08-09T18:48:09-04:00
[INFO] Final Memory: 33M/285M
[INFO] ------------------------------------------------------------------------
</pre>
</details>
<br>
<pre>
# fill up database with data
mysql -h$DB_HOST_NAME -u$DB_USER_NAME -p$DB_USER_PASS -P3306 employees < src/test/resources/data.sql
</pre>
<pre>
# to run
mvn spring-boot:run
</pre>
<details><summary>Results:</summary>
<pre>
......
[INFO] <<< spring-boot-maven-plugin:1.5.6.RELEASE:run (default-cli) < test-compile @ server <<<
[INFO] 
[INFO] 
[INFO] --- spring-boot-maven-plugin:1.5.6.RELEASE:run (default-cli) @ server ---
[INFO] Attaching agents: []
data source url = jdbc:mysql://database-server/employees?useSSL=false
data source pool size = 8
base path = /ds/v1
port = 8080
</pre>
</details>
<br>
<pre>
# run in anonther terminal or from your browser
curl -i -H "Accept: application/json" "http://localhost:8080/ds/v1"
</pre>
<details><summary>Results:</summary>
<pre>
HTTP/1.1 200 
Content-Type: application/json;charset=UTF-8
Transfer-Encoding: chunked
Date: Wed, 09 Aug 2017 22:50:44 GMT

{
  "_links" : {
    "salaries" : {
      "href" : "http://localhost:8080/ds/v1/salaries{?page,size,sort}",
      "templated" : true
    },
    "deptManagers" : {
      "href" : "http://localhost:8080/ds/v1/deptManagers{?page,size,sort}",
      "templated" : true
    },
    "titles" : {
      "href" : "http://localhost:8080/ds/v1/titles{?page,size,sort}",
      "templated" : true
    },
    "employees" : {
      "href" : "http://localhost:8080/ds/v1/employees{?page,size,sort}",
      "templated" : true
    },
    "departments" : {
      "href" : "http://localhost:8080/ds/v1/departments{?page,size,sort}",
      "templated" : true
    },
    "deptEmps" : {
      "href" : "http://localhost:8080/ds/v1/deptEmps{?page,size,sort}",
      "templated" : true
    },
    "profile" : {
      "href" : "http://localhost:8080/ds/v1/profile"
    }
  }
}
</pre>
</details>
<br>
<pre>
# http://localhost:8080/ds/v1/employees?size=3
curl -i -H "Accept: application/json" "http://localhost:8080/ds/v1/employees?size=3"
</pre>
<details><summary>Results:</summary>
<pre>
HTTP/1.1 200 
Content-Type: application/json;charset=UTF-8
Transfer-Encoding: chunked
Date: Wed, 09 Aug 2017 22:51:33 GMT

{
  "_embedded" : {
    "employees" : [ {
      "birthDate" : "1953-09-02",
      "firstName" : "Georgi",
      "lastName" : "Facello",
      "gender" : "M",
      "hireDate" : "1986-06-26",
      "empNo" : 10001,
      "_links" : {
        "self" : {
          "href" : "http://localhost:8080/ds/v1/employees/10001"
        },
        "employee" : {
          "href" : "http://localhost:8080/ds/v1/employees/10001"
        },
        "salaries" : {
          "href" : "http://localhost:8080/ds/v1/employees/10001/salaries"
        },
        "deptEmps" : {
          "href" : "http://localhost:8080/ds/v1/employees/10001/deptEmps"
        },
        "titles" : {
          "href" : "http://localhost:8080/ds/v1/employees/10001/titles"
        },
        "departments" : {
          "href" : "http://localhost:8080/ds/v1/employees/10001/departments"
        }
      }
    }, {
......
  "_links" : {
    "first" : {
      "href" : "http://localhost:8080/ds/v1/employees?page=0&size=3"
    },
    "self" : {
      "href" : "http://localhost:8080/ds/v1/employees{&sort}",
      "templated" : true
    },
    "next" : {
      "href" : "http://localhost:8080/ds/v1/employees?page=1&size=3"
    },
    "last" : {
      "href" : "http://localhost:8080/ds/v1/employees?page=35&size=3"
    },
    "profile" : {
      "href" : "http://localhost:8080/ds/v1/profile/employees"
    },
    "search" : {
      "href" : "http://localhost:8080/ds/v1/employees/search"
    }
  },
  "page" : {
    "size" : 3,
    "totalElements" : 107,
    "totalPages" : 36,
    "number" : 0
  }
}
</pre>
</details>
<br>
<pre>
# to package
mvn clean package

target/server-1.0.0.jar is generated
</pre>

<b>Step 4</b> Clean up

<pre>
# back to the docker machine terminal

press CTRL C
#to stop the application

exit
# back to your host console

# stop running container
docker stop database-server

# remove listed containers
docker container rm -f data-service database-server

# list all images
docker image ls -a

# remove all images
docker image rm -f $(docker image ls -a -q)
</pre>

# Business and Coding Logic 

Now, we have seen the project alive. Let's look into some detail logic of the project.

## Employees Sample Database 

The mysql benchmark database is purposely selected for this project.

<details><summary>Entity relation diagram:</summary>
<img src="https://dev.mysql.com/doc/employee/en/images/employees-schema.png" />
(Ref: https://dev.mysql.com/doc/employee/en/sakila-structure.html)
</details>

## Key observations and solutions summaries

### 1) Generate primary key when insert new record:

    a) employees table has an integer primary key (emp_no), but without auto increment setup. It needs generate a unique integer.

    b) In department table, dept_no is a char(4) string primary key, it needs generate unique char(4) string.

    c) The above two issues are solved by implementing IdentifierGenerator interface.

### 2) Composite primary keys:

    a) Annotations: @EmbeddedId and @Embeddable are used to handle composite primary key.

    b) In tables: dept_emp, dept_manager, salaries, titles, composite primary key are used.
    
    c) CRUD opertions handling when composite primary key presents
    
    d) JPQL with composite primary key
    
### 3) Foreign keys and relations:

    a) One to many: employees -> dept_emp, employees -> titles, employees -> salaries, departments -> dept_emp, departments -> dept_manager

    b) Hints: employee changes department, gets promotion and raise etc.
    
    c) Many to one: dept_emp -> employees, titles -> employees, salaries -> employees, dept_emp -> departments, dept_manager -> departments
    
    d) Many to many: employees <-> (dept_emp) <-> departments, employees <-> (dept_manager) <-> departments
    
    e) The above relations are handled by corresponding annotations: @OneToMany, @ManyToOne and @ManyToMany.

## Project Outline

The Spring boot framework saves the most boilerplate codes. Data CRUD operations, pagination and HATEOAS are simple just work out of box. In the approach promoted by this demonstration, Tests (50%), Entity (30%) and Repository (20%) are the most of coding jobs. Less codes mean less bugs and less test. 

<details><summary>Project Tree:</summary>
<pre>
.
├── README.md
├── client
│   ├── pom.xml
│   ├── resources
│   │   └── application.properties
│   └── target
├── mvnw
├── mvnw.cmd
├── pom.xml
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── doitincloud
│   │   │           ├── client
│   │   │           │   └── Application.java
│   │   │           ├── library
│   │   │           │   ├── employees
│   │   │           │   │   ├── Department.java
│   │   │           │   │   ├── DeptEmp.java
│   │   │           │   │   ├── DeptEmpId.java
│   │   │           │   │   ├── Employee.java
│   │   │           │   │   ├── Salary.java
│   │   │           │   │   ├── SalaryId.java
│   │   │           │   │   ├── Title.java
│   │   │           │   │   └── TitleId.java
│   │   │           │   └── helper
│   │   │           │       ├── HalRest.java
│   │   │           │       └── Utils.java
│   │   │           └── server
│   │   │               ├── Application.java
│   │   │               ├── entity
│   │   │               │   ├── Department.java
│   │   │               │   ├── DeptEmp.java
│   │   │               │   ├── DeptEmpId.java
│   │   │               │   ├── DeptIdGenerator.java
│   │   │               │   ├── DeptManager.java
│   │   │               │   ├── EmpIdGenerator.java
│   │   │               │   ├── Employee.java
│   │   │               │   ├── Salary.java
│   │   │               │   ├── SalaryId.java
│   │   │               │   ├── Title.java
│   │   │               │   └── TitleId.java
│   │   │               └── repository
│   │   │                   ├── DepartmentRepository.java
│   │   │                   ├── DeptEmpRepository.java
│   │   │                   ├── DeptManagerRepository.java
│   │   │                   ├── EmployeeExRepository.java
│   │   │                   ├── EmployeeRepository.java
│   │   │                   ├── EmployeeRepositoryImpl.java
│   │   │                   ├── SalaryRepository.java
│   │   │                   └── TitleRepository.java
│   │   └── resources
│   │       └── application.properties
│   └── test
│       ├── java
│       │   └── com
│       │       └── doitincloud
│       │           └── server
│       │               ├── DepartmentIntegrationTests.java
│       │               ├── DeptEmpIntegrationTests.java
│       │               ├── DeptManagerIntegrationTests.java
│       │               ├── EmployeeIntegrationTests.java
│       │               ├── HalRestTests.java
│       │               ├── SalaryIntegrationTests.java
│       │               ├── TestBase.java
│       │               └── TitleIntegrationTests.java
│       └── resources
│           ├── application.properties
│           ├── data.sql
│           ├── delete-all.sql
│           └── schema.sql
└── target
</pre>
</details>

## Advanced features to cover potential data query needs

### 1) Query Methods:

<pre>
example:
public interface EmployeeRepository extends PagingAndSortingRepository<Employee, Long>, EmployeeExRepository {
...
	// http://localhost:8080/ds/v1/employees/search/findByLastName?name=Leonhardt
	public Page<Employee> findByLastName(@Param("name") String name, Pageable pageable);
</pre>
### 2) Query Methods with JPQL query:
<pre>
example:
public interface EmployeeRepository extends PagingAndSortingRepository<Employee, Long>, EmployeeExRepository {
...
	// http://localhost:8080/ds/v1/employees/search/findByLastNameLike?name=odru
	@Query(value = "FROM Employee e WHERE e.lastName LIKE %:name%")
	public Page<Employee> findByLastNameLike(@Param("name") String name, Pageable pageable);
</pre>

### 3) Custom implemented Query Method with JPQL query:
<pre>
example:
public class EmployeeRepositoryImpl implements EmployeeExRepository {
...
	@Override
	public Page<Employee> birthdaysOfTheMonth(Pageable pageable)
</pre>

### 4) Customer implemented Query Method with native SQL query:
<pre>
example:
public class EmployeeRepositoryImpl implements EmployeeExRepository {
...
	@Override
	public Page<Employee> birthdaysOfMonth(@Param("today") Date today, Pageable pageable)
</pre>

### 5) Support pagenation in Customer implemented Query Method
<pre>
example:
public class EmployeeRepositoryImpl implements EmployeeExRepository {
...
	@Override
	public Page<Employee> birthdaysOfTheMonth(Pageable pageable) {
	...
		Page<Employee> page = new PageImpl(employees, pageable, total);
		return page;

	}
</pre>
 
### 6) Integration Tests

Test codes are included for all CRUD operations APIs, additional search APIs and links. Tests are not use entity and repository classes. Instead, few library/employees classes are created. As demonstrated in client/Application.java - a simple text based HAL browser, a simple Map class is enough to use the data service.
In order to run the client sub-project, run the data-service first, then in a second terminal, cd to client folder, run mvn clean spring-boot:run 

<pre>
# run the data-service

/workspace/spring-data-service # mvn spring-boot:run

# in a second terminal

docker run -it --name=data-client --link data-service:data-service samwen2017/java-spring:latest sh

# get source
git clone https://github.com/samw2017/spring-data-service.git
cd spring-data-service/client

mvn clean spring-boot:run

# enter url as http://data-service:8080/ds/v1

Enter base url (http://localhost:8080/ds/v1): http://data-service:8080/ds/v1
......
****************************************************************

Url: http://localhost:8080/ds/v1
Type: index
Links: 7

----------------------------------------------------------------

0: EXIT
1: salaries => http://localhost:8080/ds/v1/salaries
2: employees => http://localhost:8080/ds/v1/employees
3: deptEmps => http://localhost:8080/ds/v1/deptEmps
4: deptManagers => http://localhost:8080/ds/v1/deptManagers
5: titles => http://localhost:8080/ds/v1/titles
6: departments => http://localhost:8080/ds/v1/departments
7: profile => http://localhost:8080/ds/v1/profile


Your selection (0): 
</pre> 

#
## Authors

* **Sam Wen** 

## License

This project is licensed under the Apache 2.0 License.

## References

* http://www.baeldung.com
* https://vladmihalcea.com
* http://www.mkyong.com

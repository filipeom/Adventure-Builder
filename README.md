# Adventure Builder [![Build Status](https://travis-ci.com/tecnico-softeng/prototype-2018.svg?token=fJ1UzWxWjpuNcHWPhqjT&branch=master)](https://travis-ci.com/tecnico-softeng/prototype-2018) [![codecov](https://codecov.io/gh/tecnico-softeng/prototype-2018/branch/master/graph/badge.svg?token=OPjXGqoNEm)](https://codecov.io/gh/tecnico-softeng/prototype-2018)


To run tests execute: mvn clean install

To see the coverage reports, go to <module name>/target/site/jacoco/index.html.


|   Number   |          Name           |            Email        |   GitHub Username  | Group |
| ---------- | ----------------------- | ----------------------- | -------------------| ----- |
| 79690     |Tomás Zaki                |tomaszaki@tecnico.ulisboa.pt|Zakovich |   1   |
| 86434     |Hussein Giva              |hussein.giva@tecnico.ulisboa.pt       | HusseinGiva |   1   |
|           |                          |                         |                    |   1   |
|86411      |Filipe Marques            |filipe.s.marques@tecnico.ulisboa.pt    | filipeom           |   2   |
|86456      |Jorge Martins                         |jorge.cardoso.martins@tecnico.ulisboa.pt                         |Jorgecmartins                    |   2   |
|86492       |Paulo Dias               |paulo.a.c.dias@tecnico.ulisboa.pt|PauloACDias |   2   |

### First Deliver:
- **Hussein Giva, 86434, HusseinGiva - Migrated Tests:**  
        - **Activity Module:**  
             - ActivityInterfaceGetActivityReservationDataMethodTest  - Issue #11  
        - **Bank Module:**  
             - AccountContructorMethodTest - Issue #12  
             - AccountDepositMethodTest - Issue #13  
             - AccountWithdrawMethodTest - Issue #14  
             - Migrate BankConstructorTest - Issue #15  
             - Migrate BankGetAccountMethodTest - Issue #16  
             - Migrate BankPersistenceTest - Issue #17  
             - Migrate ClientConstructorMethodTest - Issue #18  
             - Migrate OperationRevertMethodTest - Issue #20  

- **Group 1:**
- **Group 2:**

### Infrastructure

This project includes the persistent layer, as offered by the FénixFramework.
This part of the project requires to create databases in mysql as defined in `resources/fenix-framework.properties` of each module.

See the lab about the FénixFramework for further details.

#### Docker (Alternative to installing Mysql in your machine)

To use a containerized version of mysql, follow these stesp:

```
docker-compose -f local.dev.yml up -d
docker exec -it mysql sh
```

Once logged into the container, enter the mysql interactive console

```
mysql --password
```

And create the 6 databases for the project as specified in
the `resources/fenix-framework.properties`.

To launch a server execute in the module's top directory: mvn clean spring-boot:run

To launch all servers execute in bin directory: startservers

To stop all servers execute: bin/shutdownservers

To run jmeter (nogui) execute in project's top directory: mvn -Pjmeter verify. Results are in target/jmeter/results/, open the .jtl file in jmeter, by associating the appropriate listeners to WorkBench and opening the results file in listener context


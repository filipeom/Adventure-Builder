# Adventure Builder [![Build Status](https://travis-ci.com/tecnico-softeng/es19al_34-project.svg?token=eJvAd6DJajPUmobiJpdP&branch=develop)](https://travis-ci.com/tecnico-softeng/es19al_34-project) [![codecov](https://codecov.io/gh/tecnico-softeng/es19al_34-project/branch/develop/graph/badge.svg?token=0tC2pTXzH8)](https://codecov.io/gh/tecnico-softeng/es19al_34-project)

To run tests execute: mvn clean install

To see the coverage reports, go to <module name>/target/site/jacoco/index.html.

### Rastreabilidade do trabalho

Ordene a tabela por ordem crescente da data de término.

|   Issue id | Owner (ist number)      | Owner (github username) | PRs id (with link)  |            Date    |
| ---------- | ----------------------- | ----------------------- | ------------------- | ------------------ |
| #126       | ist186411               | filipeom                | [#135](https://github.com/tecnico-softeng/es19al_34-project/pull/135)            | **2019/04/07**     |
| #138       | ist186492               | PauloACDias             | [#140](https://github.com/tecnico-softeng/es19al_34-project/pull/140)            | **2019/04/07**     |
| #130       | ist186456               | PauloACDias             | [#146](https://github.com/tecnico-softeng/es19al_34-project/pull/146)            | **2019/04/13**     |
| #137       | ist186434               | HusseinGiva             | [#149](https://github.com/tecnico-softeng/es19al_34-project/pull/149)            | **2019/04/14**     |
| #136       | ist186434               | HusseinGiva             | [#156](https://github.com/tecnico-softeng/es19al_34-project/pull/156)            | **2019/04/15**     |
| #125       | ist186434               | HusseinGiva             | [#148](https://github.com/tecnico-softeng/es19al_34-project/pull/148) [#157](https://github.com/tecnico-softeng/es19al_34-project/pull/157)    | **2019/04/16**     |
| #133       | ist179690               | Zakovich                | [#159](https://github.com/tecnico-softeng/es19al_34-project/pull/159)            | **2019/04/16**     |
| #160       | ist186456               | Jorgecmartins           | [#161](https://github.com/tecnico-softeng/es19al_34-project/pull/161)            | **2019/04/17**     |
| #128       | ist186492               | PauloACDias             | [#162](https://github.com/tecnico-softeng/es19al_34-project/pull/162)            | **2019/04/17**     |
| #139       | ist179690               | Zakovich                | [#155](https://github.com/tecnico-softeng/es19al_34-project/pull/155)            | **2019/04/17**     |
| #129       | ist186411               | filipeom                | [#163](https://github.com/tecnico-softeng/es19al_34-project/pull/163)            | **2019/04/20**     |
| #153       | ist186434               | HusseinGiva             | [#166](https://github.com/tecnico-softeng/es19al_34-project/pull/166)            | **2019/04/20**     |
| #152       | ist186411               | filipeom                | [#164](https://github.com/tecnico-softeng/es19al_34-project/pull/164)            | **2019/04/20**     |
| #132       | ist186456               | Jorgecmartins           | [#147](https://github.com/tecnico-softeng/es19al_34-project/pull/147) [#165](https://github.com/tecnico-softeng/es19al_34-project/pull/165)    | **2019/04/20**     |
| #150       | ist186456               | Jorgecmartins           | [#169](https://github.com/tecnico-softeng/es19al_34-project/pull/169)            | **2019/04/21**     |
| #127       | ist186456               | Jorgecmartins           | [#143](https://github.com/tecnico-softeng/es19al_34-project/pull/143) [#167](https://github.com/tecnico-softeng/es19al_34-project/pull/167)    | **2019/04/21**     |
| #151       | ist179690               | Zakovich                | [#168](https://github.com/tecnico-softeng/es19al_34-project/pull/168)            | **2019/04/21**     |
| #131       | ist186411               | filipeom                | [#154](https://github.com/tecnico-softeng/es19al_34-project/pull/154) [#157](https://github.com/tecnico-softeng/es19al_34-project/pull/157) [#171](https://github.com/tecnico-softeng/es19al_34-project/pull/171)     | **2019/04/21**     |


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

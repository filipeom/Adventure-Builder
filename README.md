# Feedback SPRINT 4

## ----- General notes
Please delete unused branches


## ----- Refatorização Fix UIs [max=2.0]

## ----- Refatorização Extend UIs [max=5.5]

## ----- Refatorização Fix JMeter [max=4.8]

## ----- Funcionalidade New JMeter [max=1.3]

## ----- Penalties

## ----- Final considerations

# Adventure Builder [![Build Status](https://travis-ci.com/tecnico-softeng/es19al_34-project.svg?token=eJvAd6DJajPUmobiJpdP&branch=develop)](https://travis-ci.com/tecnico-softeng/es19al_34-project) [![codecov](https://codecov.io/gh/tecnico-softeng/es19al_34-project/branch/develop/graph/badge.svg?token=0tC2pTXzH8)](https://codecov.io/gh/tecnico-softeng/es19al_34-project)


To run tests execute: mvn clean install

To see the coverage reports, go to <module name>/target/site/jacoco/index.html.

### Rastreabilidade do trabalho

Ordene a tabela por ordem crescente da data de término.

|   Issue id | Owner (ist number)      | Owner (github username) | PRs id (with link)  |            Date    |  
| ---------- | ----------------------- | ----------------------- | ------------------- | ------------------ |
| #176 | 86411 | filipeom | [#197](https://github.com/tecnico-softeng/es19al_34-project/pull/197)           | 26/04/2019             |
|  #174      | 86456                   | Jorgecmartins           | [#198](https://github.com/tecnico-softeng/es19al_34-project/pull/198)           | 27/04/2019             |
|  #175      | 86434                   | HusseinGiva             | [#200](https://github.com/tecnico-softeng/es19al_34-project/pull/200)           | 01/05/2019             |
|  #177      | 86492                   | PauloACDias             | [#199](https://github.com/tecnico-softeng/es19al_34-project/pull/199)            | 01/05/2019         |
|  #181      | 86492                   | PauloACDias             | [#208](https://github.com/tecnico-softeng/es19al_34-project/pull/208)         | 03/05/2019       |
| #202 | 86456 | Jorgecmartins | [#209](https://github.com/tecnico-softeng/es19al_34-project/pull/209) | 05/05/2019 |
| #187 | 86456 | Jorgecmartins | [#210](https://github.com/tecnico-softeng/es19al_34-project/pull/210) | 05/05/2019 |
| #179 | 86411 | filipeom      | [#211](https://github.com/tecnico-softeng/es19al_34-project/pull/211) | 06/05/2019 |
| #188 | 86456 | Jorgecmartins | [#214](https://github.com/tecnico-softeng/es19al_34-project/pull/214) | 06/05/2019 |
| #189 | 86456 | Jorgecmartins | [#215](https://github.com/tecnico-softeng/es19al_34-project/pull/215) | 06/05/2019 |
| #193 | 86492 | PauloACDias   | [#216](https://github.com/tecnico-softeng/es19al_34-project/pull/216) | 07/05/2019 |
| #195 | 86492 | PauloACDias   | [#213](https://github.com/tecnico-softeng/es19al_34-project/pull/213) | 07/05/2019 |
| #184 | 86411 | filipeom      | [#212](https://github.com/tecnico-softeng/es19al_34-project/pull/212) | 07/05/2019 |
| #186 | 86411 | filipeom      | [#217](https://github.com/tecnico-softeng/es19al_34-project/pull/217) | 07/05/2019 |
| #182 | 86492 | PauloACDias   | [#218](https://github.com/tecnico-softeng/es19al_34-project/pull/218) | 07/05/2019 |
| #196 | 86411 | filipeom      | [#220](https://github.com/tecnico-softeng/es19al_34-project/pull/220) | 10/05/2019 |
| #178 | 86434 | HusseinGiva   | [#221](https://github.com/tecnico-softeng/es19al_34-project/pull/221) | 10/05/2019 |
| #180 | 86456 | Jorgecmartins | \*[#219](https://github.com/tecnico-softeng/es19al_34-project/pull/219) [#225](https://github.com/tecnico-softeng/es19al_34-project/pull/219) | 10/05/2019 |
| #183 | 79960 | Zakovich      | [#222](https://github.com/tecnico-softeng/es19al_34-project/pull/222) | 10/05/2019 |
| #191 | 86434 | HusseinGiva   | [#224](https://github.com/tecnico-softeng/es19al_34-project/pull/224) | 10/05/2019 |
| #185 | 86434 | HusseinGiva   | [#223](https://github.com/tecnico-softeng/es19al_34-project/pull/223) | 10/05/2019 |
| #190 | 79960 | Zakovich      | [#226](https://github.com/tecnico-softeng/es19al_34-project/pull/226) | 10/05/2019 |
| #192 | 79960 | Zakovich      | [#229](https://github.com/tecnico-softeng/es19al_34-project/pull/229) | 10/05/2019 |
| #194 | 79960 | Zakovich      | [#228](https://github.com/tecnico-softeng/es19al_34-project/pull/228) | 10/05/2019 |

### Notes
* ***Commit message errors in [#219](https://github.com/tecnico-softeng/es19al_34-project/pull/219)**
    - *FixJMeter: success-sequence-no-car* should instead be *FixJMeter: success-sequence-no-car, closes #188*
* *Clean BankDatabase* in load JMeter tests sometimes fails. As a consequence running back to back JMeter tests may fail.
  
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

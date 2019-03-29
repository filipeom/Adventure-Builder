# Adventure Builder [![Build Status](https://travis-ci.com/tecnico-softeng/es19al_34-project.svg?token=xDPBAaQ2epnFt9PRstYY&branch=develop)](https://travis-ci.com/tecnico-softeng/es19al_34-project)[![codecov](https://codecov.io/gh/tecnico-softeng/es19al_34-project/branch/develop/graph/badge.svg?token=0tC2pTXzH8)](https://codecov.io/gh/tecnico-softeng/es19al_34-project)

To run tests execute: mvn clean install

To see the coverage reports, go to <module name>/target/site/jacoco/index.html.


|   Number   |          Name           |            Email                       |   GitHub Username  | Group |
| ---------- | ----------------------- | -------------------------------------- | -------------------| ----- |
| 79690      | Tomás Zaki              |tomaszaki@tecnico.ulisboa.pt            | Zakovich           |   1   |
| 86434      | Hussein Giva            |hussein.giva@tecnico.ulisboa.pt         | HusseinGiva        |   1   |
|            |                         |                                        |                    |   1   |
| 86411      | Filipe Marques          |filipe.s.marques@tecnico.ulisboa.pt     | filipeom           |   2   |
| 86456      | Jorge Martins           |jorge.cardoso.martins@tecnico.ulisboa.pt| Jorgecmartins      |   2   |
| 86492      | Paulo Dias              |paulo.a.c.dias@tecnico.ulisboa.pt       | PauloACDias        |   2   |

- **Group 1:**
- **Group 2:**
### Sprint 2:
#### Migrated Tests:
* **Filipe Marques, 86411, [filipeom](https://github.com/filipeom):**
  + **Broker:**
    - Create tests for BookRoomState for the new functionality, #107
    - Broker checks bulkbooking before reserving a a new room, #105
    - Create TaxPaymentStateProcessMethodTest, #101
    - Add final interfaces to Broker, issue #76
    - Migrate UndoStateProcessMethodTest, and remove static variables from Broker, issue #69
    - Migrate ProcessPaymentStateProcessMethodTest, issue #66
    - Migrate CancelledStateProcessMethodTest, issue #64

* **Jorge Martins, 86456, Jorgecmartins:**
  + **Broker:**
    - Create tests for BookRoomState for the new functionality, issue #107
    - Broker checks bulkbooking before reserving a a new room, issue #105
    - Remove static method calls in BookRoomState and RentVeHicleState, issue #78
    - Migrate ReserveActivityStateProcessMethodTest, issue #68
    - Migrate ConfirmedStateProcessMethodTest, issue #65
    - Migrate BulkRoomBookingGetRoomBookingData4TypeMethodTest, issue #62

* **Paulo Dias, 86492, PauloACDias:**
  + **Broker:**
    - Remove static method calls in ConfirmedState and ReserveActivityState, issue #79
    - Migrate RentVehicleStateMethodTest, issue #67
    - Migrate BulkRoomBookingProcessBookingMethodTest, issue #63
    - Migrate BookRoomStateMethodTest, issue #61
    - Migrate AdventureSequenceTest, issue #60

* **Hussein Giva, 86434, HusseinGiva:**
  + **Hotel:**
    - Remove static method calls in Processor, issue #75
    - Migrate HotelInterfaceCancelBookingMethodTest, issue #73
    - Migrate HotelInterfaceBulkBookingMethodTest, issue #72
    - Migrate ProcessSubmitBookingMethodTest, issue #71

  + **Activity:**
    - Migrate ActivityInterfaceReserveActivityMethodtest, issue #59

* **Tomas Zaki, 79690, Zakovich:**
  + **Activity:**
    - Remove static method calls in Processor, issue #74
    - Migrate ActivityInterfaceGetActivityReservationDataMethodTest, issue #58
    - Migrate ActivityInterfaceCancelReservationMethodTest, issue #57
    - Migrate InvoiceProcessorSubmitBookingMethodTest, issue #56
    - Migrate ActivityOfferHasVacancyMethodTest, issue #55

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

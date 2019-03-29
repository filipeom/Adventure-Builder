package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.exception.BrokerException
import org.joda.time.LocalDate
import spock.lang.Unroll
import spock.lang.Shared


class AdventureConstructorSpockMethodTest extends SpockRollbackTestAbstractClass {
    //should use shared def.
    @Shared def MARGIN = 0.3
     @Shared def begin = new LocalDate(2016, 12, 19)
     @Shared def end = new LocalDate(2016, 12, 21)
     @Shared def broker

     def BROKER_IBAN = "BROKER_IBAN"
     def BROKER_NIF_AS_BUYER = "buyerNIF"
     def NIF_AS_BUYER = "buyerNIF"
     def BROKER_NIF_AS_SELLER = "sellerNIF"
     def OTHER_NIF = "987654321"
     def CLIENT_NIF = "123456789"
     def DRIVING_LICENSE = "IMT1234"
     def AGE = 20
     def CLIENT_IBAN = "BK011234567"
     def client

    @Override
     def populate4Test() {
        broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)    
        client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
     }

     def 'success'() {
        when:
        def adventure = new Adventure(broker, begin, end, client, MARGIN)
        //can use keyword "with" and join tests, using data tables
        then:
        adventure.getBroker() == broker
        adventure.getBegin() == begin
        adventure.getEnd() == end
        adventure.getClient() == client
        adventure.getMargin() == MARGIN
        broker.getAdventureSet().contains(adventure) == true

        adventure.getPaymentConfirmation() == null
        adventure.getActivityConfirmation() == null
        adventure.getRoomConfirmation() == null
     }

     def 'successEqual18'() {
        when:
        def adventure = new Adventure(broker, begin, end, new Client(broker, CLIENT_IBAN, OTHER_NIF, DRIVING_LICENSE + "1", 18), MARGIN)

        then:
        adventure.getBroker() == broker
        adventure.getBegin() == begin
        adventure.getEnd() == end
        adventure.getAge() == 18
        adventure.getIban() == CLIENT_IBAN
        adventure.getMargin() == MARGIN
        broker.getAdventureSet().contains(adventure) == true

        adventure.getPaymentConfirmation() == null
        adventure.getActivityConfirmation() == null
        adventure.getRoomConfirmation() == null
     }

     def 'successEqual100'() {
        when:
        def c = new Client(broker, CLIENT_IBAN, OTHER_NIF, DRIVING_LICENSE + "1", 100)
        def adventure = new Adventure(broker, begin, end, c, MARGIN)

        then:
        adventure.getBroker() == broker
        adventure.getBegin() == begin
        adventure.getEnd() == end
        adventure.getAge() == 100
        adventure.getIban() == CLIENT_IBAN
        adventure.getMargin() == MARGIN
        broker.getAdventureSet().contains(adventure) == true

        adventure.getPaymentConfirmation() == null
        adventure.getActivityConfirmation() == null
        adventure.getRoomConfirmation() == null
     }

     def 'success1Amount'() {
        when:
        def adventure = new Adventure(broker, begin, end, client, 1)

        then:
        adventure.getBroker() == broker
        adventure.getBegin() == begin
        adventure.getEnd() == end
        adventure.getAge() == 20
        adventure.getIban() == CLIENT_IBAN
        adventure.getMargin() == 1
        broker.getAdventureSet().contains(adventure) == true

        adventure.getPaymentConfirmation() == null
        adventure.getActivityConfirmation() == null
        adventure.getRoomConfirmation() == null
     }

     def 'successEqualDates'() {
        when:
        def adventure = new Adventure(broker, begin, begin, client, MARGIN)

        then:
        adventure.getBroker() == broker
        adventure.getBegin() == begin
        adventure.getEnd() == begin
        adventure.getAge() == 20
        adventure.getIban() == CLIENT_IBAN
        adventure.getMargin() == MARGIN
        broker.getAdventureSet().contains(adventure) == true

        adventure.getPaymentConfirmation() == null
        adventure.getActivityConfirmation() == null
        adventure.getRoomConfirmation() == null
    }

    @Unroll('Adventure: #_broker, #_begin, #_end, #_margin')
    def 'exceptions_Adventure'() {
        when:
        new Adventure(_broker, _begin, _end, client, _margin)

        then:
        thrown(BrokerException)

        where:
        _broker | _begin | _end               | _margin
        null    | begin  | end                | MARGIN
        broker  | null   | end                | MARGIN
        broker  | begin  | null               | MARGIN
        broker  | begin  | end                | -100
        broker  | begin  | end                | 0
        broker  | begin  | begin.minusDays(1) | MARGIN
    }

    @Unroll('Client_Adventure: #_margin')
    def 'exceptions_Client'() {
        when:
        def c = new Client(broker, CLIENT_IBAN, OTHER_NIF, DRIVING_LICENSE, _margin)
        new Adventure(broker, begin, end, c, MARGIN)

        then:
        thrown(BrokerException)

        where:
        _margin | _
        17      | _
        101     | _
    }
}

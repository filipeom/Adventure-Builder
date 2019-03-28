package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.exception.BrokerException

class BrokerGetBulkRoomBookingMethodSpockTest extends SpockRollbackTestAbstractClass {
  def broker

  @Override
  def populate4Test() {
    broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)
  }

  def 'success'() {
    given:
    def bulk = new BulkRoomBooking(broker, 2, BEGIN, END, NIF_AS_BUYER, IBAN_BUYER)

    expect:
    broker.getBulkRoomBooking() == bulk
  }

  def 'exception'() {
    when:
    broker.getBulkRoomBooking()

    then:
    thrown(BrokerException)
  }

  def 'reserve bulk and delete'() {
    given:
    new BulkRoomBooking(broker, 2, BEGIN, END, NIF_AS_BUYER, IBAN_BUYER)

    when:
    broker.getBulkRoomBooking().delete()
    broker.getBulkRoomBooking()

    then:
    thrown(BrokerException)
  }

  def 'reserve one more bulk than deleted ones'() {
    given:
    20.times {
      new BulkRoomBooking(broker, 2, BEGIN, END, NIF_AS_BUYER, IBAN_BUYER)
    }
    when:
    19.times {
      broker.getBulkRoomBooking().delete()
    }
    then:
    broker.getBulkRoomBooking() != null
  }
}

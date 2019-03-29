package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.services.remote.ActivityInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.CarInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.broker.exception.BrokerException

class BrokerGetBulkRoomBookingMethodSpockTest extends SpockRollbackTestAbstractClass {
  def broker

  def activityInterface
  def bankInterface
  def carInterface
  def hotelInterface
  def taxInterface

  @Override
  def populate4Test() {

    activityInterface = Mock(ActivityInterface)
    bankInterface     = Mock(BankInterface)
    carInterface      = Mock(CarInterface)
    hotelInterface    = Mock(HotelInterface)
    taxInterface      = Mock(TaxInterface)

    broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN,
    activityInterface, bankInterface, carInterface, hotelInterface, taxInterface)
  }

  def 'success'() {
    given:
    def bulk = new BulkRoomBooking(broker, 2, BEGIN, END, NIF_AS_BUYER, IBAN_BUYER)
    hotelInterface.bulkBooking(2, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >> [REF_ONE, REF_TWO]
    bulk.processBooking()

    expect:
    broker.getBulkRoomBooking(ARRIVAL, DEPARTURE) == bulk
  }

  def 'exception'() {
    when:
    broker.getBulkRoomBooking(ARRIVAL, DEPARTURE)

    then:
    thrown(BrokerException)
  }

  def 'reserve bulk and delete'() {
    given:
    def bulk = new BulkRoomBooking(broker, 2, BEGIN, END, NIF_AS_BUYER, IBAN_BUYER)
    hotelInterface.bulkBooking(2, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >> [REF_ONE, REF_TWO]
    bulk.processBooking()


    when:
    broker.getBulkRoomBooking(ARRIVAL, DEPARTURE).delete()
    broker.getBulkRoomBooking(ARRIVAL, DEPARTURE)

    then:
    thrown(BrokerException)
  }

  def 'reserve one more bulk than deleted ones'() {
    given:
    hotelInterface.bulkBooking(2, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, _) >> [REF_ONE, REF_TWO]
    20.times {
      def bulk = new BulkRoomBooking(broker, 2, BEGIN, END, NIF_AS_BUYER, IBAN_BUYER)
      bulk.processBooking()
    }

    when:
    19.times {
      broker.getBulkRoomBooking(ARRIVAL, DEPARTURE).delete()
    }
    then:
    broker.getBulkRoomBooking(ARRIVAL, DEPARTURE) != null
  }
}

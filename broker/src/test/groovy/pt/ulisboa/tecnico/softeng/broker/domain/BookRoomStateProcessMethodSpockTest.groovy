package pt.ulisboa.tecnico.softeng.broker.domain

import spock.lang.Unroll
import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State
import pt.ulisboa.tecnico.softeng.broker.services.remote.ActivityInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.CarInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface.Type
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.broker.exception.BrokerException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.ActivityException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.BankException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.CarException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.TaxException

import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRoomBookingData

class BookRoomStateProcessMethodSpockTest extends SpockRollbackTestAbstractClass {
  def adventure
  def bookingData
  def broker
  def bulk

  def activityInterface
  def bankInterface
  def carInterface
  def hotelInterface
  def taxInterface

  @Override
  def populate4Test() {
    activityInterface = Mock(ActivityInterface)
    bankInterface = Mock(BankInterface)
    carInterface = Mock(CarInterface)
    hotelInterface = Mock(HotelInterface)
    taxInterface = Mock(TaxInterface)

    broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN, activityInterface, bankInterface, carInterface, hotelInterface, taxInterface)

    bulk = new BulkRoomBooking(broker, NUMBER_OF_BULK, BEGIN, END, NIF_AS_BUYER, IBAN_BUYER)

    def client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
    adventure = new Adventure(broker, BEGIN, END, client, MARGIN)

    bookingData = new RestRoomBookingData(Type.SINGLE, ARRIVAL, DEPARTURE, NIF_AS_BUYER, BROKER_IBAN, bulk.getId())
    bookingData.setReference(ROOM_CONFIRMATION)
    bookingData.setPrice(80.0)

    adventure.setState(State.BOOK_ROOM)
  }

  def 'success'() {
    given:
    hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >> [REF_ONE, REF_TWO]
    bulk.processBooking()

    when:
    adventure.process()

    then:
    1 * hotelInterface.getRoomBookingData(_) >> bookingData
    1 * hotelInterface.reserveRoom(_) >> bookingData

    and:
    with(adventure) {
      getState().getValue() == State.PROCESS_PAYMENT
      getRoomConfirmation() == ROOM_CONFIRMATION
      getAmount() == (80.0 * (1 + MARGIN))
    }
  }

  @Unroll("exceptions: #exception")
  def 'exceptions'() {
  given:
  hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >> [REF_ONE, REF_TWO]
  bulk.processBooking()

  when:
  adventure.process()

  then:
  1 * hotelInterface.getRoomBookingData(_) >> { throw exception }
  1 * hotelInterface.reserveRoom(_) >> bookingData

  and:
  with(adventure) {
    getState().getValue() == State.PROCESS_PAYMENT
    getRoomConfirmation() == ROOM_CONFIRMATION
    getAmount() == (80.0 * (1 + MARGIN))
  }

  where:
  exception                   | _
  new HotelException()        | _
  new RemoteAccessException() | _
  new BrokerException()       | _
  }

}

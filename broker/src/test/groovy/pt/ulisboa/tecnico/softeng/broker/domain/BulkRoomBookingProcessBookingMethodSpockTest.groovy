package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.services.remote.ActivityInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.CarInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRoomBookingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException


class BulkRoomBookingProcessBookingMethodSpockTest extends SpockRollbackTestAbstractClass {
  def bulk

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

    def broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER,
		NIF_AS_BUYER, BROKER_IBAN, activityInterface, bankInterface, carInterface,
		hotelInterface, taxInterface)

    bulk = new BulkRoomBooking(broker, NUMBER_OF_BULK, BEGIN, END, NIF_AS_BUYER, IBAN_BUYER)
  }

  def 'success'() {
    when:
    bulk.processBooking()

    then:
    1 * hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >> new HashSet<>(Arrays.asList("ref1", "ref2"))
    and:
    bulk.getReferences().size() == 2
  }

  def 'success Twice'() {
    when:
    for (int i = 0; i < 2; i++)
      bulk.processBooking()

    then:
    1 * hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >> new HashSet<>(Arrays.asList("ref1", "ref2"))
    and:
    _ * hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >> new HashSet<>(Arrays.asList("ref3", "ref4"))
    and:
    bulk.getReferences().size() == 2
  }

  def 'one Hotel Exception'() {
    when:
    for (int i = 0; i < 2; i++)
      bulk.processBooking()

    then:
    1 * hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >> { throw new HotelException() }
    and:
    1 * hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >> new HashSet<>(Arrays.asList("ref1", "ref2"))
    and:
    bulk.getReferences().size() == 2
    and:
    bulk.getCancelled() == false
  }

  def 'max Hotel Exception'() {
    when:
    for (int i = 0; i < 3; i++)
      bulk.processBooking()

    then:
    3 * hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >> { throw new HotelException() }
    and:
    bulk.getReferences().size() == 0
    and:
    bulk.getCancelled() == true
  }

  def 'max Minus One Hotel Exception'() {
    when:
    for (int i = 0; i < 3; i++)
      bulk.processBooking()

    then:
    2 * hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >> { throw new HotelException() }
    and:
    1 * hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >> new HashSet<>(Arrays.asList("ref1", "ref2"))
    and:
    bulk.getReferences().size() == 2
    and:
    bulk.getCancelled() == false
  }

  def 'hotel Exception Value Is Reset By Remote Exception'() {
    when:
    for (int i = 0; i < 6; i++)
      bulk.processBooking()

    then:
    2 * hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >> { throw new HotelException() }
    and:
    1 * hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >> { throw new RemoteAccessException() }
    and:
    1 * hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >> new HashSet<>(Arrays.asList("ref1", "ref2"))
    and:
    bulk.getReferences().size() == 2
    and:
    bulk.getCancelled() == false
  }

  def 'one Remote Exception'() {
    when:
    for (int i = 0; i < 2; i++)
      bulk.processBooking()

    then:
    1 * hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >> { throw new RemoteAccessException() }
    and:
    1 * hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >> new HashSet<>(Arrays.asList("ref1", "ref2"))
    and:
    bulk.getReferences().size() == 2
    and:
    bulk.getCancelled() == false
  }

  def 'max Remote Exception'() {
    when:
    for (int i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS + 1; i++)
        bulk.processBooking()

    then:
    10 * hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >> { throw new RemoteAccessException() }
    and:
    bulk.getReferences().size() == 0
    and:
    bulk.getCancelled() == true
  }

  def 'max Minus One Remote Exception'() {
    when:
    for (int i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS; i++)
        bulk.processBooking()

    then:
    9 * hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >> { throw new RemoteAccessException() }
    and:
    1 * hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >> new HashSet<>(Arrays.asList("ref1", "ref2"))
    and:
    bulk.getReferences().size() == 2
    and:
    bulk.getCancelled() == false
  }

  def 'remote Exception Value Is Reset By Hotel Exception'() {
    when:
    for (int i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS * 2 ; i++)
        bulk.processBooking()

    then:
    9 * hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >> { throw new RemoteAccessException() }
    and:
    1 * hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >> { throw new HotelException() }
    and:
    1 * hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >> new HashSet<>(Arrays.asList("ref1", "ref2"))
    and:
    bulk.getReferences().size() == 2
    and:
    bulk.getCancelled() == false
  }

}

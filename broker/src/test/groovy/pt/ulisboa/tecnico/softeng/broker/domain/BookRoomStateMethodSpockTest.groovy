package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State
import pt.ulisboa.tecnico.softeng.broker.services.remote.ActivityInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.CarInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.ActivityException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.BankException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.CarException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.TaxException

import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRoomBookingData

class BookRoomStateMethodSpockTest extends SpockRollbackTestAbstractClass {
  def adventure
  def bookingData
  def broker
  def client

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

    client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
    adventure = new Adventure(broker, BEGIN, END, client, MARGIN)

    bookingData = new RestRoomBookingData()
    bookingData.setReference(ROOM_CONFIRMATION)
    bookingData.setPrice(80.0)

    adventure.setState(State.BOOK_ROOM)
    }

    def 'success Book Room'(){
      when:
      adventure.process()

      then:
      1 * hotelInterface.reserveRoom(_) >> bookingData
      and:
      adventure.getState().getValue() == State.PROCESS_PAYMENT
    }

    def 'success Book Room To Renting'() {
      given:
      def adv = new Adventure(broker, BEGIN, END, client, MARGIN, true)
      adv.setState(State.BOOK_ROOM)

      when:
      adv.process()

      then:
      1 * hotelInterface.reserveRoom(_) >> bookingData
      and:
      adv.getState().getValue() == State.RENT_VEHICLE
    }

    def 'hotel Exception'() {
      when:
      adventure.process()

      then:
      1 * hotelInterface.reserveRoom(_) >> { throw new HotelException() }
      and:
      adventure.getState().getValue() == State.UNDO
    }

    def 'singleRemoteAccessException'() {
      when:
      adventure.process()

      then:
      1 * hotelInterface.reserveRoom(_) >> { throw new RemoteAccessException() }
      and:
      adventure.getState().getValue() == State.BOOK_ROOM
    }

    def 'max Remote Access Exception'() {
      when:
      for (int i = 0; i < BookRoomState.MAX_REMOTE_ERRORS; i++) {
  			adventure.process()
  		}

      then:
      BookRoomState.MAX_REMOTE_ERRORS * hotelInterface.reserveRoom(_) >> { throw new RemoteAccessException() }
      and:
      adventure.getState().getValue() == State.UNDO
    }

    def 'maxMinusOneRemoteAccessException'() {
      when:
      for (int i = 0; i < BookRoomState.MAX_REMOTE_ERRORS - 1; i++) {
  			adventure.process()
  		}

      then:
      (BookRoomState.MAX_REMOTE_ERRORS - 1)  * hotelInterface.reserveRoom(_) >> { throw new RemoteAccessException() }
      and:
      adventure.getState().getValue() == State.BOOK_ROOM
    }

    def 'five Remote Access Exception One Success'() {
      when:
      for (int i = 0; i < 6; i++) {
  			adventure.process()
  		}

      then:
      5 * hotelInterface.reserveRoom(_) >> { throw new RemoteAccessException() }
      1 * hotelInterface.reserveRoom(_) >> bookingData
      and:
      adventure.getState().getValue() == State.PROCESS_PAYMENT
    }

    def 'one Remote Access Exception One Hotel Exception'() {
      when:
      adventure.process()
      adventure.process()

      then:
      1 * hotelInterface.reserveRoom(_) >> { throw new RemoteAccessException() }
      1 * hotelInterface.reserveRoom(_) >> { throw new HotelException() }
      and:
      adventure.getState().getValue() == State.UNDO
    }

}

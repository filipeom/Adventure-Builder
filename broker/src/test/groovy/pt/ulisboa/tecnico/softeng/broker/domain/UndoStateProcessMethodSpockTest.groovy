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
import spock.lang.Unroll

class UndoStateProcessMethodSpockTest extends SpockRollbackTestAbstractClass {
  def adventure

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

    def broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN, activityInterface, bankInterface, carInterface, hotelInterface, taxInterface)

    def client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
    adventure = new Adventure(broker, BEGIN, END, client, MARGIN)

    adventure.setState(State.UNDO)
  }

  def 'sucess revert payment'() {
    given:
    adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
    bankInterface.cancelPayment(PAYMENT_CONFIRMATION) >> PAYMENT_CANCELLATION

    when:
    adventure.process()

    then:
    adventure.getState().getValue() == State.CANCELLED
  }

  @Unroll('the #failure occured')
  def 'fail revert payment'() {
    given:
    adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)

    when:
    adventure.process()

    then:
    1 * bankInterface.cancelPayment(PAYMENT_CONFIRMATION) >> { throw exception }
    and:
    adventure.getState().getValue() == State.UNDO

    where:
    exception                   | failure
    new BankException()         | 'bank exception'
    new RemoteAccessException() | 'remote access exception'
  }

  def 'success revert activity'() {
    given:
    with(adventure) {
      setPaymentConfirmation(PAYMENT_CONFIRMATION)
      setPaymentCancellation(PAYMENT_CANCELLATION)
      setActivityConfirmation(ACTIVITY_CONFIRMATION)
    }
    activityInterface.cancelReservation(ACTIVITY_CONFIRMATION) >> ACTIVITY_CANCELLATION

    when:
    adventure.process()

    then:
    adventure.getState().getValue() == State.CANCELLED
  }

  @Unroll('the #failure occurred')
  def 'fail revert activity'() {
    given:
    with(adventure) {
      setPaymentConfirmation(PAYMENT_CONFIRMATION)
      setPaymentCancellation(PAYMENT_CANCELLATION)
      setActivityConfirmation(ACTIVITY_CONFIRMATION)
    }
 
    when:
    adventure.process()

    then:
    1 * activityInterface.cancelReservation(ACTIVITY_CONFIRMATION) >> {throw exception}
    and:
    adventure.getState().getValue() == State.UNDO

    where:
    exception                   | failure
    new ActivityException()     | 'activity exception'
    new RemoteAccessException() | 'remote access exception' 
  }
  
  def 'success revert room booking'() {
    given:
    with(adventure) {
      setPaymentConfirmation(PAYMENT_CONFIRMATION)
      setPaymentCancellation(PAYMENT_CANCELLATION)
      setActivityConfirmation(ACTIVITY_CONFIRMATION)
      setActivityCancellation(ACTIVITY_CANCELLATION)
      setRoomConfirmation(ROOM_CONFIRMATION)
    }
    hotelInterface.cancelBooking(ROOM_CONFIRMATION) >> ROOM_CANCELLATION

    when:
    adventure.process()

    then:
    adventure.getState().getValue() == State.CANCELLED
  }

  @Unroll('the #failure occurred')
  def 'fail revert room booking'() {
    given:
    with(adventure) {
      setPaymentConfirmation(PAYMENT_CONFIRMATION)
      setPaymentCancellation(PAYMENT_CANCELLATION)
      setActivityConfirmation(ACTIVITY_CONFIRMATION)
      setActivityCancellation(ACTIVITY_CANCELLATION)
      setRoomConfirmation(ROOM_CONFIRMATION)
    }

    when:
    adventure.process()

    then:
    1 * hotelInterface.cancelBooking(ROOM_CONFIRMATION) >> { throw exception }
    and:
    adventure.getState().getValue() == State.UNDO

    where:
    exception                   | failure
    new HotelException()        | 'hotel exception'
    new RemoteAccessException() | 'remote access exception'
  }

  def 'success revert rent a car'() {
    given:
    with(adventure) {
      setPaymentConfirmation(PAYMENT_CONFIRMATION)
      setPaymentCancellation(PAYMENT_CANCELLATION)
      setActivityConfirmation(ACTIVITY_CONFIRMATION)
      setActivityCancellation(ACTIVITY_CANCELLATION)
      setRoomConfirmation(ROOM_CONFIRMATION)
      setRoomCancellation(ROOM_CANCELLATION)
      setRentingConfirmation(RENTING_CONFIRMATION)
    }
    carInterface.cancelRenting(RENTING_CONFIRMATION) >> RENTING_CANCELLATION

    when:
    adventure.process()

    then:
    adventure.getState().getValue() == State.CANCELLED
  }

  @Unroll('the #failure occured')
  def 'fail revert rent a car'() {
    given:
    with(adventure) {
      setPaymentConfirmation(PAYMENT_CONFIRMATION)
      setPaymentCancellation(PAYMENT_CANCELLATION)
      setActivityConfirmation(ACTIVITY_CONFIRMATION)
      setActivityCancellation(ACTIVITY_CANCELLATION)
      setRoomConfirmation(ROOM_CONFIRMATION)
      setRoomCancellation(ROOM_CANCELLATION)
      setRentingConfirmation(RENTING_CONFIRMATION)
    }

    when:
    adventure.process()

    then:
    1 * carInterface.cancelRenting(RENTING_CONFIRMATION) >> { throw exception }
    and:
    adventure.getState().getValue() == State.UNDO
  
    where:
    exception                   | failure
    new CarException()          | 'car exception'
    new RemoteAccessException() | 'remote access exception'
  }

  def 'sucess cancel invoice'() {
    given:
    with(adventure) {
      setPaymentConfirmation(PAYMENT_CONFIRMATION)
      setPaymentCancellation(PAYMENT_CANCELLATION)
      setActivityConfirmation(ACTIVITY_CONFIRMATION)
      setActivityCancellation(ACTIVITY_CANCELLATION)
      setRoomConfirmation(ROOM_CONFIRMATION)
      setRoomCancellation(ROOM_CANCELLATION)
      setRentingConfirmation(RENTING_CONFIRMATION)
      setRentingCancellation(RENTING_CANCELLATION)
      setInvoiceReference(INVOICE_REFERENCE)
    }

    when:
    adventure.process()

    then:
    1 * taxInterface.cancelInvoice(INVOICE_REFERENCE)
    and:
    adventure.getState().getValue() == State.CANCELLED
  }

  @Unroll('the #failure occured')
  def 'fail cancel invoice'() {
    given:
    with(adventure) {
      setPaymentConfirmation(PAYMENT_CONFIRMATION)
      setPaymentCancellation(PAYMENT_CANCELLATION)
      setActivityConfirmation(ACTIVITY_CONFIRMATION)
      setActivityCancellation(ACTIVITY_CANCELLATION)
      setRoomConfirmation(ROOM_CONFIRMATION)
      setRoomCancellation(ROOM_CANCELLATION)
      setRentingConfirmation(RENTING_CONFIRMATION)
      setRentingCancellation(RENTING_CANCELLATION)
      setInvoiceReference(INVOICE_REFERENCE)
    }

    when:
    adventure.process()

    then:
    1 * taxInterface.cancelInvoice(INVOICE_REFERENCE) >> { throw exception }
    and:
    adventure.getState().getValue() == State.UNDO

    where:
    exception                   | failure
    new TaxException()          | 'tax exeception'
    new RemoteAccessException() | 'remote access exception'
  }
}

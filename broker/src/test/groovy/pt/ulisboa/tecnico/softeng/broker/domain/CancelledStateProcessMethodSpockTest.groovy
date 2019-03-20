package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State
import pt.ulisboa.tecnico.softeng.broker.services.remote.ActivityInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.CarInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestBankOperationData
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.BankException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException
import spock.lang.Unroll

class CancelledStateProcessMethodSpockTest extends SpockRollbackTestAbstractClass {
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

    def broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, 
    NIF_AS_BUYER, BROKER_IBAN, activityInterface, bankInterface, carInterface, 
    hotelInterface, taxInterface)

    def client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
    adventure = new Adventure(broker, BEGIN, END, client, MARGIN)

    adventure.setState(State.CANCELLED)
  }
  
  def 'did not pay'() {
    when:
    adventure.process()

    then:
    0 * bankInterface.getOperationData(_)
    and:
    0 * activityInterface.getActivityReservationData(_)
    and:
    0 * hotelInterface.getRoomBookingData(_)
    and:
    adventure.getState().getValue() == State.CANCELLED
  }

  @Unroll('the #failure occurred')
  def 'cancelled payment first'() {
    given:
    adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
    adventure.setPaymentCancellation(PAYMENT_CANCELLATION)

    when:
    adventure.process()

    then:
    1 * bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> { throw exception }
    and:
    adventure.getState().getValue() == State.CANCELLED

    where:
    exception                   | failure
    new BankException()         | 'bank exception'
    new RemoteAccessException() | 'remote access exception'
  }

  @Unroll('the #failure occurred')
  def 'cancelled payment second'() {
    given:
    adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
    adventure.setPaymentCancellation(PAYMENT_CANCELLATION)

    when:
    adventure.process()

    then:
    1 * bankInterface.getOperationData(_) >>  { new RestBankOperationData() }
    1 * bankInterface.getOperationData(_) >> { throw exception }
    and:
    adventure.getState().getValue() == State.CANCELLED

    where:
    exception                   | failure
    new BankException()         | 'bank exception'
    new RemoteAccessException() | 'remote access exception'
  }

  def 'cancelled payment'() {
    given:
    adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
    adventure.setPaymentCancellation(PAYMENT_CANCELLATION)

    when:
    adventure.process()

    then:
    1 * bankInterface.getOperationData(PAYMENT_CONFIRMATION)
    and:
    1 * bankInterface.getOperationData(PAYMENT_CANCELLATION)
    and:
    adventure.getState().getValue() == State.CANCELLED
  }

  def 'cancelled activity'() {
    given:
    adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
    adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
    adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
    adventure.setActivityCancellation(ACTIVITY_CANCELLATION)

    when:
    adventure.process()

    then:
    1 * bankInterface.getOperationData(PAYMENT_CONFIRMATION)
    and:
    1 * bankInterface.getOperationData(PAYMENT_CANCELLATION)
    and:
    1 * activityInterface.getActivityReservationData(ACTIVITY_CANCELLATION)
    and:
    adventure.getState().getValue() == State.CANCELLED
  }

  def 'cancelled room'() {
    given:
    adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
    adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
    adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
    adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
    adventure.setRoomConfirmation(ROOM_CONFIRMATION)
    adventure.setRoomCancellation(ROOM_CANCELLATION)

    when:
    adventure.process()

    then:
    1 * bankInterface.getOperationData(PAYMENT_CONFIRMATION)
    and:
    1 * bankInterface.getOperationData(PAYMENT_CANCELLATION)
    and:
    1 * activityInterface.getActivityReservationData(ACTIVITY_CANCELLATION)
    and:
    1 * hotelInterface.getRoomBookingData(ROOM_CANCELLATION)
    and:
    adventure.getState().getValue() == State.CANCELLED
  }

  def 'cancelled renting'() {
    given:
    adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
    adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
    adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
    adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
    adventure.setRentingConfirmation(RENTING_CONFIRMATION)
    adventure.setRentingCancellation(RENTING_CANCELLATION)

    when:
    adventure.process()

    then:
    1 * bankInterface.getOperationData(PAYMENT_CONFIRMATION)
    and:
    1 * bankInterface.getOperationData(PAYMENT_CANCELLATION)
    and:
    1 * activityInterface.getActivityReservationData(ACTIVITY_CANCELLATION)
    and:
    1 * carInterface.getRentingData(RENTING_CANCELLATION)
    and:
    adventure.getState().getValue() == State.CANCELLED
  }

  def 'cancelled booking and renting'() {
    given:
    adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
    adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
    adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
    adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
    adventure.setRoomConfirmation(ROOM_CONFIRMATION)
    adventure.setRoomCancellation(ROOM_CANCELLATION)
    adventure.setRentingConfirmation(RENTING_CONFIRMATION)
    adventure.setRentingCancellation(RENTING_CANCELLATION)

    when:
    adventure.process()

    then:
    1 * bankInterface.getOperationData(PAYMENT_CONFIRMATION)
    and:
    1 * bankInterface.getOperationData(PAYMENT_CANCELLATION)
    and:
    1 * activityInterface.getActivityReservationData(ACTIVITY_CANCELLATION)
    and:
    1 * hotelInterface.getRoomBookingData(ROOM_CANCELLATION)
    and:
    1 * carInterface.getRentingData(RENTING_CANCELLATION)
    and:
    adventure.getState().getValue() == State.CANCELLED
  }
}

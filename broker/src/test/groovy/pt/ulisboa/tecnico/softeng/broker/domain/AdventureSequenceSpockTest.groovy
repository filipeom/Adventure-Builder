package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State
import pt.ulisboa.tecnico.softeng.broker.services.remote.ActivityInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.CarInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestActivityBookingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRentingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRoomBookingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.ActivityException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.BankException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.CarException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.TaxException

class AdventureSequenceSpockTest extends SpockRollbackTestAbstractClass {
  def bookingActivityData
  def bookingRoomData
  def rentingData

  def activityInterface
  def bankInterface
  def carInterface
  def hotelInterface
  def taxInterface

  def broker
  def client

  @Override
  def populate4Test() {
    bookingActivityData = new RestActivityBookingData()
    bookingRoomData = new RestRoomBookingData()
    rentingData = new RestRentingData()

    activityInterface = Mock(ActivityInterface)
    bankInterface = Mock(BankInterface)
    carInterface = Mock(CarInterface)
    hotelInterface = Mock(HotelInterface)
    taxInterface = Mock(TaxInterface)

    broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN, activityInterface, bankInterface, carInterface, hotelInterface, taxInterface)
    client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)

    //minor corrections like this should be caught and handled in the PR phase.
    bookingActivityData.setReference(ACTIVITY_CONFIRMATION)
    bookingActivityData.setPrice(70.0)
    bookingActivityData.setPaymentReference(PAYMENT_CONFIRMATION)
    bookingActivityData.setInvoiceReference(INVOICE_REFERENCE)

    bookingRoomData.setReference(ROOM_CONFIRMATION)
    bookingRoomData.setPrice(80.0)
    bookingRoomData.setPaymentReference(PAYMENT_CONFIRMATION)
    bookingRoomData.setInvoiceReference(INVOICE_REFERENCE)

    rentingData.setReference(RENTING_CONFIRMATION)
    rentingData.setPrice(60.0)
    rentingData.setPaymentReference(PAYMENT_CONFIRMATION)
    rentingData.setInvoiceReference(INVOICE_REFERENCE)
  }

  //idem for other tests (see inside comments)
  def 'success Sequence'() {
    given:
    def adventure = new Adventure(broker, ARRIVAL, DEPARTURE, client, MARGIN, true)

    //we only have one thing in "then", the expected result. Why would we be expecting, for instance activityInterface.reserveActivity(_) >> bookingActivityData as a result?

    1 * activityInterface.reserveActivity(_) >> bookingActivityData
    and:
    1 * hotelInterface.reserveRoom(_) >> bookingRoomData
    and:
    1 * carInterface.rentCar(*_) >> rentingData
    and:
    1 * bankInterface.processPayment(_) >> PAYMENT_CONFIRMATION
    and:
    1 * taxInterface.submitInvoice(_) >> INVOICE_DATA
    and:
    1 * bankInterface.getOperationData(PAYMENT_CONFIRMATION)
    and:
    1 * activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> bookingActivityData
    and:
    1 * carInterface.getRentingData(RENTING_CONFIRMATION) >> rentingData
    and:
    1 * hotelInterface.getRoomBookingData(ROOM_CONFIRMATION) >> bookingRoomData

    when:
    for (int i = 0; i < 6; i++)
      adventure.process()
    //more spock-ish
    //        1.upto(6) { adventure.process() }


    then:
    adventure.getState().getValue() == State.CONFIRMED
  }

  def 'success Sequence One No Car'() {
    given:
    def adventure = new Adventure(broker, ARRIVAL, DEPARTURE, client, MARGIN)

    when:
    for (int i = 0; i < 5; i++)
      adventure.process()

    then:
    1 * activityInterface.reserveActivity(_) >> bookingActivityData
    and:
    1 * hotelInterface.reserveRoom(_) >> bookingRoomData
    and:
    1 * bankInterface.processPayment(_) >> PAYMENT_CONFIRMATION
    and:
    1 * taxInterface.submitInvoice(_) >> INVOICE_DATA
    and:
    1 * bankInterface.getOperationData(PAYMENT_CONFIRMATION)
    and:
    1 * activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> bookingActivityData
    and:
    1 * hotelInterface.getRoomBookingData(ROOM_CONFIRMATION) >> bookingRoomData
    and:
    adventure.getState().getValue() == State.CONFIRMED
  }

  def 'success Sequence No Hotel'() {
    given:
    def adventure = new Adventure(broker, ARRIVAL, ARRIVAL, client, MARGIN, true)

    when:
    for (int i = 0; i < 5; i++)
      adventure.process()

    then:
    1 * activityInterface.reserveActivity(_) >> bookingActivityData
    and:
    1 * carInterface.rentCar(*_) >> rentingData
    and:
    1 * bankInterface.processPayment(_) >> PAYMENT_CONFIRMATION
    and:
    1 * taxInterface.submitInvoice(_) >> INVOICE_DATA
    and:
    1 * bankInterface.getOperationData(PAYMENT_CONFIRMATION)
    and:
    1 * activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> bookingActivityData
    and:
    1 * carInterface.getRentingData(RENTING_CONFIRMATION) >> rentingData
    and:
    adventure.getState().getValue() == State.CONFIRMED
  }

  def 'success Sequence No Hotel No Car'() {
    given:
    def adventure = new Adventure(broker, ARRIVAL, ARRIVAL, client, MARGIN)

    when:
    for (int i = 0; i < 4; i++)
      adventure.process()

    then:
    1 * activityInterface.reserveActivity(_) >> bookingActivityData
    and:
    1 * bankInterface.processPayment(_) >> PAYMENT_CONFIRMATION
    and:
    1 * taxInterface.submitInvoice(_) >> INVOICE_DATA
    and:
    1 * bankInterface.getOperationData(PAYMENT_CONFIRMATION)
    and:
    1 * activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> bookingActivityData
    and:
    adventure.getState().getValue() == State.CONFIRMED
  }

  def 'unsuccess Sequence Fail Activity'() {
    given:
    def adventure = new Adventure(broker, ARRIVAL, DEPARTURE, client, MARGIN)

    when:
    for (int i = 0; i < 2; i++)
      adventure.process()

    then:
    1 * activityInterface.reserveActivity(_) >> { throw new ActivityException() }
    and:
    adventure.getState().getValue() == State.CANCELLED
  }

  def 'unsuccess Sequence Fail Hotel'() {
    given:
    def adventure = new Adventure(broker, ARRIVAL, DEPARTURE, client, MARGIN)

    when:
    for (int i = 0; i < 4; i++)
      adventure.process()

    then:
    1 * activityInterface.reserveActivity(_) >> bookingActivityData
    and:
    1 * hotelInterface.reserveRoom(_) >> { throw  new HotelException() }
    and:
    1 * activityInterface.cancelReservation(ACTIVITY_CONFIRMATION) >> ACTIVITY_CANCELLATION
    and:
    adventure.getState().getValue() == State.CANCELLED
  }

  def 'unsuccess Sequence Fail Car'() {
    given:
    def adventure = new Adventure(broker, ARRIVAL, ARRIVAL, client, MARGIN, true)

    when:
    for (int i = 0; i < 4; i++)
      adventure.process()

    then:
    1 * activityInterface.reserveActivity(_) >> bookingActivityData
    and:
    1 * carInterface.rentCar(*_) >> { throw new CarException() }
    and:
    1 * activityInterface.cancelReservation(ACTIVITY_CONFIRMATION) >> ACTIVITY_CANCELLATION
    and:
    adventure.getState().getValue() == State.CANCELLED
  }

  def 'unsuccess Sequence Fail Payment'() {
    given:
    def adventure = new Adventure(broker, ARRIVAL, DEPARTURE, client, MARGIN, true)

    when:
    for (int i = 0; i < 6; i++)
      adventure.process()

    then:
    1 * activityInterface.reserveActivity(_) >> bookingActivityData
    and:
    1 * hotelInterface.reserveRoom(_) >> bookingRoomData
    and:
    1 * carInterface.rentCar(*_) >> rentingData
    and:
    1 * bankInterface.processPayment(_) >> { throw new BankException() }
    and:
    1 * activityInterface.cancelReservation(_) >> ACTIVITY_CANCELLATION
    and:
    1 * hotelInterface.cancelBooking(ROOM_CONFIRMATION) >> ROOM_CANCELLATION
    and:
    1 * carInterface.cancelRenting(RENTING_CONFIRMATION) >> RENTING_CANCELLATION
    and:
    adventure.getState().getValue() == State.CANCELLED
  }

  def 'unsuccess Sequence Fail Tax'() {
    given:
    def adventure = new Adventure(broker, ARRIVAL, DEPARTURE, client, MARGIN, true)

    when:
    for (int i = 0; i < 6; i++)
      adventure.process()

    then:
    1 * activityInterface.reserveActivity(_) >> bookingActivityData
    and:
    1 * hotelInterface.reserveRoom(_) >> bookingRoomData
    and:
    1 * carInterface.rentCar(*_) >> rentingData
    and:
    1 * bankInterface.processPayment(_) >> PAYMENT_CONFIRMATION
    and:
    1 * taxInterface.submitInvoice(_) >> { throw  new TaxException() }
    and:
    1 * activityInterface.cancelReservation(ACTIVITY_CONFIRMATION) >> ACTIVITY_CANCELLATION
    and:
    1 * hotelInterface.cancelBooking(ROOM_CONFIRMATION) >> ROOM_CANCELLATION
    and:
    1 * carInterface.cancelRenting(RENTING_CONFIRMATION) >> RENTING_CANCELLATION
    and:
    1 * bankInterface.cancelPayment(PAYMENT_CONFIRMATION) >> PAYMENT_CANCELLATION
    and:
    adventure.getState().getValue() == State.CANCELLED
  }

}

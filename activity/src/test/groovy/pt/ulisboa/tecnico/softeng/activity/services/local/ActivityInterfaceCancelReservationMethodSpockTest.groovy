package pt.ulisboa.tecnico.softeng.activity.domain

import org.joda.time.LocalDate

import spock.lang.Unroll
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException
import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.activity.services.local.ActivityInterface

class ActivityInterfaceCancelReservationMethodSpockTest extends SpockRollbackTestAbstractClass {
  def CANCEL_PAYMENT_REFERENCE = "CancelPaymentReference"
  def INVOICE_REFERENCE = "InvoiceReference"
  def PAYMENT_REFERENCE = "PaymentReference"
  def IBAN = "IBAN"
  def NIF = "123456789"
  def begin = new LocalDate(2016, 12, 19)
  def end = new LocalDate(2016, 12, 21)

  def provider
  def offer
  def activityInterface
  def bankInterface
  def taxInterface
  def activity
  def processor
  def booking

  @Override
  def populate4Test() {
    bankInterface = Mock(BankInterface)
    taxInterface = Mock(TaxInterface)
    activityInterface = new ActivityInterface()
    processor = new Processor(bankInterface, taxInterface)
    provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", IBAN, processor)
    activity = new Activity(provider, "Bush Walking", 18, 80, 3)

    offer = new ActivityOffer(activity, begin, end, 30)
    booking = new Booking(provider, offer, NIF, IBAN)
  }

  def "success"(){
    given:
      booking = new Booking(provider, offer, NIF, IBAN)
      provider.getProcessor().submitBooking(booking)

    and:
      bankInterface.processPayment(_) >> PAYMENT_REFERENCE
      taxInterface.submitInvoice(_) >> INVOICE_REFERENCE

    when:
      def cancel = activityInterface.cancelReservation(booking.getReference())

    then:
      booking.isCancelled() == true
      booking.getCancel() == cancel

  }

  //see next test
  @Unroll('the #failure occurred')
  def "doesNotExist"(){
    when:
      provider.getProcessor().submitBooking(new Booking(provider, offer, NIF, IBAN))
    then:
      bankInterface.processPayment(_) >> PAYMENT_REFERENCE
      taxInterface.submitInvoice(_) >> INVOICE_REFERENCE

    and:
      activityInterface.cancelReservation("XPTO") >> { throw exception }


    where:
    exception                   | failure
    new ActivityException()     | 'Activity exception'

  }

  def 'booking does not exist'() {
    given:
    provider.getProcessor().submitBooking(new Booking(provider, offer, NIF, IBAN))

    when:
    activityInterface.cancelReservation("XPTO")

    then:
    thrown(ActivityException)
    and:
    and:
    0 * bankInterface.cancelPayment(_)
    0 * taxInterface.cancelInvoice(_)
  }

}

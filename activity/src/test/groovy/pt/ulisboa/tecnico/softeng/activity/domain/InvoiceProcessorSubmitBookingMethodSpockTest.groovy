package pt.ulisboa.tecnico.softeng.activity.domain

import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.exceptions.BankException
import pt.ulisboa.tecnico.softeng.activity.services.remote.exceptions.RemoteAccessException
import pt.ulisboa.tecnico.softeng.activity.services.remote.exceptions.TaxException

import spock.lang.Unroll


class InvoiceProcessorSubmitBookingMethodSpockTest extends SpockRollbackTestAbstractClass {
  def CANCEL_PAYMENT_REFERENCE = "CancelPaymentReference"
  def INVOICE_REFERENCE = "InvoiceReference"
  def PAYMENT_REFERENCE = "PaymentReference"
  def AMOUNT = 30
  def IBAN = "IBAN"
  def NIF = "123456789"
  def begin = new LocalDate(2016, 12, 19)
  def end = new LocalDate(2016, 12, 21)

  def provider
  def activity
  def offer
  def booking
  def taxInterface
  def bankInterface
  def bookingOne
  def bookingTwo

  @Override
  def populate4Test(){
    bankInterface = Mock(BankInterface)
    taxInterface = Mock(TaxInterface)
    def processor = new Processor(bankInterface, taxInterface)
    provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", IBAN, processor)
    activity = new Activity(provider, "Bush Walking", 18, 80, 10)
    offer = new ActivityOffer(activity, begin, end, AMOUNT)
    bookingOne = new Booking(provider, offer, NIF, IBAN)
    bookingTwo = new Booking(provider, offer, NIF, IBAN)

  }

  def 'success'(){
    given:
    bankInterface.processPayment(_) >> PAYMENT_REFERENCE
    taxInterface.submitInvoice(_) >> INVOICE_REFERENCE

    when:
    provider.getProcessor().submitBooking(bookingOne)

    then:
    bookingOne.paymentReference == PAYMENT_REFERENCE
    bookingOne.invoiceReference == INVOICE_REFERENCE
  }

  @Unroll('the #failure occurred')
  def "oneFailureOnSubmitInvoice"(){
    when:
      provider.getProcessor().submitBooking(bookingOne)
    then:
      1 * bankInterface.processPayment(_) >> PAYMENT_REFERENCE
    and:
      1 * taxInterface.submitInvoice(_) >> { throw exception}
    and:
      bookingOne.paymentReference == PAYMENT_REFERENCE
      bookingOne.invoiceReference == null

    when:
        provider.getProcessor().submitBooking(bookingTwo)

    then: 'only the second booking invokes the bank interface'
    1 * bankInterface.processPayment(_) >> PAYMENT_REFERENCE
    and: 'both invoke the tax interface'
    2 * taxInterface.submitInvoice(_) >> INVOICE_REFERENCE
    and: 'both bookings succeed'
    bookingOne.paymentReference == PAYMENT_REFERENCE
    bookingOne.invoiceReference == INVOICE_REFERENCE
    bookingTwo.paymentReference == PAYMENT_REFERENCE
    bookingTwo.invoiceReference == INVOICE_REFERENCE

    where:
    exception                   | failure
    new TaxException()          | 'tax exception'
    new RemoteAccessException() | 'remote access exception'

  }

  @Unroll('the #failure occurred')
  def "oneFailureOnProcessPayment"(){
    when:
      provider.getProcessor().submitBooking(bookingOne)
    then: 'the process payment throws a BankException'
      1 * bankInterface.processPayment(_) >> { throw exception }
    and: 'the tax interface is not invoked'
      0 * taxInterface.submitInvoice(_)
    and: 'both references are null'
      bookingOne.paymentReference == null
      bookingOne.invoiceReference == null

    when: 'doing another booking'
      provider.getProcessor().submitBooking(bookingTwo)

    then: 'both invoke the bank interface'
      2 * bankInterface.processPayment(_) >> PAYMENT_REFERENCE
    and: 'both invoke the tax interface'
      2 * taxInterface.submitInvoice(_) >> INVOICE_REFERENCE
    and: 'both bookings succeed'
      bookingOne.paymentReference == PAYMENT_REFERENCE
      bookingOne.invoiceReference == INVOICE_REFERENCE
      bookingTwo.paymentReference == PAYMENT_REFERENCE
      bookingTwo.invoiceReference == INVOICE_REFERENCE

    where:
    exception                   | failure
    new BankException()         | 'tax exception'
    new RemoteAccessException() | 'remote access exception'
  }

  def "successCancel"() {
      when:
      provider.getProcessor().submitBooking(bookingOne)

      then: 'the remote invocations succeed'
      1 * bankInterface.processPayment(_) >> PAYMENT_REFERENCE
      1 * taxInterface.submitInvoice(_) >> INVOICE_REFERENCE

      when: 'cancelling the booking'
      bookingOne.cancel()

      then: 'a cancel payment succeeds'
      1 * bankInterface.cancelPayment(PAYMENT_REFERENCE) >> CANCEL_PAYMENT_REFERENCE
      and: 'the invoice is cancelled'
      1 * taxInterface.cancelInvoice(INVOICE_REFERENCE)
      and: 'a cancel reference is stored'
      bookingOne.cancelledPaymentReference == CANCEL_PAYMENT_REFERENCE
      and: 'the booking state is cancelled'
      bookingOne.cancelledInvoice
      and: 'the original references are kept'
      bookingOne.paymentReference == PAYMENT_REFERENCE
      bookingOne.invoiceReference == INVOICE_REFERENCE
  }

  @Unroll('the #failure occurred')
  def "oneFailureOnCancelInvoice"() {
      when:
      provider.getProcessor().submitBooking(bookingOne)

      then: 'the remote invocations succeed'
      1 * bankInterface.processPayment(_) >> PAYMENT_REFERENCE
      1 * taxInterface.submitInvoice(_) >> INVOICE_REFERENCE

      when: 'cancelling the booking'
      bookingOne.cancel()

      then: 'the payment is cancelled'
      1 * bankInterface.cancelPayment(PAYMENT_REFERENCE) >> CANCEL_PAYMENT_REFERENCE
      and: 'the cancel of the invoice throws a TaxException'
      1 * taxInterface.cancelInvoice(INVOICE_REFERENCE) >> { throw exception }

      when: 'a new booking is done'
      provider.getProcessor().submitBooking(bookingTwo)

      then: 'booking one is completely cancelled'
      0 * bankInterface.cancelPayment(PAYMENT_REFERENCE)
      1 * taxInterface.cancelInvoice(INVOICE_REFERENCE)
      and: 'booking two is completed'
      1 * bankInterface.processPayment(_)
      1 * taxInterface.submitInvoice(_)

      where:
      exception                   | failure
      new TaxException()          | 'tax exception'
      new RemoteAccessException() | 'remote access exception'
  }

  @Unroll('the #failure occurred')
  def 'one failure on process payment'() {
      when:
      provider.getProcessor().submitBooking(bookingOne)

      then: 'the process payment throws a BankException'
      1 * bankInterface.processPayment(_) >> { throw exception }
      and: 'the tax interface is not invoked'
      0 * taxInterface.submitInvoice(_)
      and: 'both references are null'
      bookingOne.paymentReference == null
      bookingOne.invoiceReference == null

      when: 'doing another booking'
      provider.getProcessor().submitBooking(bookingTwo)

      then: 'both invoke the bank interface'
      2 * bankInterface.processPayment(_) >> PAYMENT_REFERENCE
      and: 'both invoke the tax interface'
      2 * taxInterface.submitInvoice(_) >> INVOICE_REFERENCE
      and: 'both bookings succeed'
      bookingOne.paymentReference == PAYMENT_REFERENCE
      bookingOne.invoiceReference == INVOICE_REFERENCE
      bookingTwo.paymentReference == PAYMENT_REFERENCE
      bookingTwo.invoiceReference == INVOICE_REFERENCE

      where:
      exception                   | failure
      new BankException()         | 'bank exception'
      new RemoteAccessException() | 'remote access exception'
  }

    def 'one remote exception on cancel invoice'() {
        when: 'a successful booking'
        provider.getProcessor().submitBooking(booking)

        then: 'the remote invocations succeed'
        1 * bankInterface.processPayment(_) >> PAYMENT_REFERENCE
        1 * taxInterface.submitInvoice(_) >> INVOICE_REFERENCE

        when: 'cancelling the booking'
        booking.cancel()

        then: 'the payment is cancelled'
        1 * bankInterface.cancelPayment(PAYMENT_REFERENCE) >> CANCEL_PAYMENT_REFERENCE
        and: 'the cancel of the invoice throws a RemoteAccessException'
        1 * taxInterface.cancelInvoice(INVOICE_REFERENCE) >> { throw new RemoteAccessException() }

        when: 'a new booking is done'
        provider.getProcessor().submitBooking(booking2)

        then: 'booking one is completely cancelled'
        0 * bankInterface.cancelPayment(PAYMENT_REFERENCE)
        1 * taxInterface.cancelInvoice(INVOICE_REFERENCE)
        and: 'booking two is completed'
        1 * bankInterface.processPayment(_)
        1 * taxInterface.submitInvoice(_)
    }
}

package pt.ulisboa.tecnico.softeng.activity.domain

import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface
import spock.lang.Unroll


class ActivityOfferHasVacancyMethodSpockTest extends SpockRollbackTestAbstractClass {
  def CANCEL_PAYMENT_REFERENCE = "CancelPaymentReference"
  def INVOICE_REFERENCE = "InvoiceReference"
  def PAYMENT_REFERENCE = "PaymentReference"
  def IBAN = "IBAN"
  def  NIF = "123456789"
  def provider
  def offer
  def processor
  def begin
  def end

  def bankInterface
  def taxInterface


	@Override
	def populate4Test() {
		bankInterface = Mock(BankInterface)
		taxInterface = Mock(TaxInterface)
		processor = new Processor(bankInterface, taxInterface)
    provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", IBAN, processor)
    def activity = new Activity(provider, "Bush Walking", 18, 80, 3)
    begin = new LocalDate(2016, 12, 19)
    end = new LocalDate(2016, 12, 21)
		offer = new ActivityOffer(activity, begin, end, 30)
	}

  @Unroll('success: #label')
  def 'success'() {
    when:
    1.upto(n) {
      new Booking(provider, offer, NIF, IBAN)
    }

    then:
    offer.hasVacancy() == res

    where:
    n | label                     || res
    1 | 'one booking'             || true
    2 | 'booking is full minus 1' || true
    3 | 'booking is full'         || false
  }


  def "hasCancelledBookings"(){
    given:
    provider.getProcessor().submitBooking(new Booking(provider, offer, NIF, IBAN))
    provider.getProcessor().submitBooking(new Booking(provider, offer, NIF, IBAN))
    def booking = new Booking(provider, offer, NIF, IBAN)
    provider.getProcessor().submitBooking(booking)

    when:
    booking.cancel()

    then:
    offer.hasVacancy()
    1 * taxInterface.cancelInvoice(_)
    1 * bankInterface.cancelPayment(_)
  }

  def "hasCancelledBookingsButFull"(){
    //most of lines present on "when" should be on "given"
    when:
      provider.getProcessor().submitBooking(new Booking(provider, offer, NIF, IBAN))
      provider.getProcessor().submitBooking(new Booking(provider, offer, NIF, IBAN))
      def booking1 = new Booking(provider, offer, NIF, IBAN)
      provider.getProcessor().submitBooking(booking1)
    then: 'bookings are complete'
      3 * bankInterface.processPayment(_) >> PAYMENT_REFERENCE
      3 * taxInterface.submitInvoice(_) >> INVOICE_REFERENCE


    when: 'cancelling a booking'
        booking1.cancel()
    then: 'payment and invoice are cancelled'
        1 * bankInterface.cancelPayment(PAYMENT_REFERENCE) >> CANCEL_PAYMENT_REFERENCE
        1 * taxInterface.cancelInvoice(INVOICE_REFERENCE)
    and: 'offer has a vacancy'
        offer.hasVacancy() == true

    when: "making another booking"
      def booking2 = new Booking(provider, offer, NIF, IBAN)
      provider.getProcessor().submitBooking(booking2)
    then: "no vacancies"
      offer.hasVacancy() == false
  }

}

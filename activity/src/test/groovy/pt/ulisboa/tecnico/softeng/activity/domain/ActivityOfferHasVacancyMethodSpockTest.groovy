package pt.ulisboa.tecnico.softeng.activity.domain

import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface


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

  def "success"(){
      when:
      def booking = new Booking(provider, offer, NIF, IBAN)

      then:
      offer.hasVacancy() == true
  }


  def "bookingIsFull"(){
    when:
      def booking1 = new Booking(provider, offer, NIF, IBAN)
      def booking2 = new Booking(provider, offer, NIF, IBAN)
      def booking3 = new Booking(provider, offer, NIF, IBAN)

    then:
      offer.hasVacancy() == false
  }

  def "bookingIsFullMinusOne"(){
    when:
      def booking1 = new Booking(provider, offer, NIF, IBAN)
      def booking2 = new Booking(provider, offer, NIF, IBAN)

    then:
      offer.hasVacancy() == true
  }

  def "hasCancelledBookings"(){
    when:
      provider.getProcessor().submitBooking(new Booking(provider, offer, NIF, IBAN))
      provider.getProcessor().submitBooking(new Booking(provider, offer, NIF, IBAN))
      def booking = new Booking(provider, offer, NIF, IBAN)
      provider.getProcessor().submitBooking(booking)
    then: 'bookings are complete'
      3 * bankInterface.processPayment(_) >> PAYMENT_REFERENCE
      3 * taxInterface.submitInvoice(_) >> INVOICE_REFERENCE


    when: 'cancelling a booking'
        booking.cancel()
    then: 'payment and invoice are cancelled'
        1 * bankInterface.cancelPayment(PAYMENT_REFERENCE) >> CANCEL_PAYMENT_REFERENCE
        1 * taxInterface.cancelInvoice(INVOICE_REFERENCE)
    and: 'offer has a vacancy'
        offer.hasVacancy() == true

  }

  def "hasCancelledBookingsButFull"(){
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

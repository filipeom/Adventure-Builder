package pt.ulisboa.tecnico.softeng.activity.services.local

import pt.ulisboa.tecnico.softeng.activity.domain.Processor

import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.activity.domain.Activity
import pt.ulisboa.tecnico.softeng.activity.domain.ActivityOffer
import pt.ulisboa.tecnico.softeng.activity.domain.ActivityProvider
import pt.ulisboa.tecnico.softeng.activity.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException
import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestActivityBookingData

class ActivityInterfaceReserveActivityMethodSpockTest extends SpockRollbackTestAbstractClass {
	def INVOICE_REFERENCE = 'InvoiceReference'
	def PAYMENT_REFERENCE = 'PaymentReference'
	def CANCEL_PAYMENT_REFERENCE = 'CancelPaymentReference'
	def IBAN = "IBAN"
	def NIF = "123456789"
	def MIN_AGE = 18
	def MAX_AGE = 50
	def CAPACITY = 30

	def provider1
	def provider2

	def taxInterface
	def bankInterface
	def activityInterface
	def processor

	@Override
	def populate4Test() {
		activityInterface = new ActivityInterface()

		taxInterface = Mock(TaxInterface)
		bankInterface = Mock(BankInterface)
		processor = new Processor(bankInterface, taxInterface)
		provider1 = new ActivityProvider("XtremX", "Adventure++", "NIF", IBAN, processor)

		taxInterface = Mock(TaxInterface)
		bankInterface = Mock(BankInterface)
		processor = new Processor(bankInterface, taxInterface)
		provider2 = new ActivityProvider("Walker", "Sky", "NIF2", IBAN, processor)

	}

	def "reserveActivity"() {
		given:
		bankInterface.processPayment(_) >> PAYMENT_REFERENCE
		taxInterface.submitInvoice(_) >> INVOICE_REFERENCE
		def activity = new Activity(provider1, "XtremX", MIN_AGE, MAX_AGE, CAPACITY)
		new ActivityOffer(activity, new LocalDate(2018, 02, 19), new LocalDate(2018, 12, 20), 30)
		def activityBookingData = new RestActivityBookingData()
		activityBookingData.setAge(20)
		activityBookingData.setBegin(new LocalDate(2018, 02, 19))
		activityBookingData.setEnd(new LocalDate(2018, 12, 20))
		activityBookingData.setIban(IBAN)
		activityBookingData.setNif(NIF)

		when:
		def bookingData = activityInterface.reserveActivity(activityBookingData)


		then:
		bookingData != null
		bookingData.getReference().startsWith("XtremX")
	}

	def "reserveAcitivityNoOption"() {
		given:
		def activityBookingData = new RestActivityBookingData()
		activityBookingData.setAge(20)
		activityBookingData.setBegin(new LocalDate(2018, 02, 19))
		activityBookingData.setEnd(new LocalDate(2018, 12, 20))
		activityBookingData.setIban(IBAN)
		activityBookingData.setNif(NIF)

		when:
		def bookingData = activityInterface.reserveActivity(activityBookingData)

		then:
		thrown(ActivityException)
	}

}
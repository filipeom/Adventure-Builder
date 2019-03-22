package pt.ulisboa.tecnico.softeng.activity.services.local

import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.activity.domain.Activity
import pt.ulisboa.tecnico.softeng.activity.domain.ActivityOffer
import pt.ulisboa.tecnico.softeng.activity.domain.ActivityProvider
import pt.ulisboa.tecnico.softeng.activity.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException
import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestActivityBookingData
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestBankOperationData
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestInvoiceData

class ActivityInterfaceReserveActivityMethodSpockTest extends SpockRollbackTestAbstractClass {
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

	@Override
	def populate4Test() {
		provider1 = new ActivityProvider("XtremX", "Adventure++", "NIF", IBAN)
		provider2 = new ActivityProvider("Walker", "Sky", "NIF2", IBAN)
		taxInterface = Mock(TaxInterface)
		bankInterface = Mock(BankInterface)
		activityInterface = Mock(ActivityInterface)
	}

	def "reserveActivity"() {
		given:
		0 * bankInterface.processPayment(_)
		0 * taxInterface.submitInvoice(_)
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
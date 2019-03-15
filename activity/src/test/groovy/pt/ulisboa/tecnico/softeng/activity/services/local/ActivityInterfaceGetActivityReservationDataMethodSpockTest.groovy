package pt.ulisboa.tecnico.softeng.activity.services.local

import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.activity.domain.Activity
import pt.ulisboa.tecnico.softeng.activity.domain.ActivityOffer
import pt.ulisboa.tecnico.softeng.activity.domain.ActivityProvider
import pt.ulisboa.tecnico.softeng.activity.domain.Booking
import pt.ulisboa.tecnico.softeng.activity.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException
import spock.lang.Unroll

class ActivityInterfaceGetActivityReservationDataMethodSpockTest extends SpockRollbackTestAbstractClass {

	def NAME = "ExtremeAdventure"
	def CODE = "XtremX"
	def begin = new LocalDate(2016, 12, 19)
	def end = new LocalDate(2016, 12, 21)
	def provider
	def offer
	def booking

	@Override
	def populate4Test() {
		provider = new ActivityProvider(CODE, NAME, "NIF", "IBAN")
		def activity = new Activity(provider, "Bush Walking", 18, 80, 3)
		offer = new ActivityOffer(activity, begin, end, 30)
	}

	def "success"() {
		given:
		booking = new Booking(provider, offer, "123456789", "IBAN")

		when:
		def data = ActivityInterface.getActivityReservationData(booking.getReference())

		then:
		with (data) {
			getReference() == booking.getReference()
			getCancellation() == null
			getName() == NAME
			getCode() == CODE
			getBegin() == begin
			getEnd() == end
			getCancellationDate() == null
		}
	}

	def "successCancelled"() {
		given:
		booking = new Booking(provider, offer, "123456789", "IBAN")

		when:
		provider.getProcessor().submitBooking(booking)
		booking.cancel()
		def data = ActivityInterface.getActivityReservationData(booking.getCancel())

		then:
		with (data) {
			getReference() == booking.getReference()
			getCancellation() == booking.getCancel()
			getName() == NAME
			getCode() == CODE
			getBegin() == begin
			getEnd() == end
			getCancellationDate() != null
		}
	}

	@Unroll("Get Activity Reservation Data: #ref")
	def "exceptions"(){
		when:
		ActivityInterface.getActivityReservationData(ref)

		then:
		thrown(ActivityException)

		where:
		ref    | _
		null   | _
		""     | _
		"XPTO" | _
	}

}

package pt.ulisboa.tecnico.softeng.activity.services.local;

import org.joda.time.LocalDate;

import pt.ulisboa.tecnico.softeng.activity.domain.Activity;
import pt.ulisboa.tecnico.softeng.activity.domain.ActivityOffer;
import pt.ulisboa.tecnico.softeng.activity.domain.ActivityProvider;
import pt.ulisboa.tecnico.softeng.activity.domain.Booking;
import pt.ulisboa.tecnico.softeng.activity.domain.SpockRollbackTestAbstractClass;
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException;
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestActivityBookingData;

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
		this.provider = new ActivityProvider(CODE, NAME, "NIF", "IBAN")
		def activity = new Activity(this.provider, "Bush Walking", 18, 80, 3)

		this.offer = new ActivityOffer(activity, this.begin, this.end, 30)
	}

	def "success"() {
		given:
		this.booking = new Booking(this.provider, this.offer, "123456789", "IBAN")

		when:
		def data = ActivityInterface.getActivityReservationData(this.booking.getReference())

		then:
		data.getReference() == this.booking.getReference()
		data.getCancellation() == null
		data.getName() == NAME
		data.getCode() == CODE
		data.getBegin() == this.begin
		data.getEnd() == this.end
		data.getCancellationDate() == null
	}

	def "successCancelled"() {
		given:
		this.booking = new Booking(this.provider, this.offer, "123456789", "IBAN")
		this.provider.getProcessor().submitBooking(this.booking)
		this.booking.cancel()
		def data = ActivityInterface.getActivityReservationData(this.booking.getCancel())

		expect:
		data.getReference() == this.booking.getReference()
		data.getCancellation() == this.booking.getCancel()
		data.getName() == NAME
		data.getCode() == CODE
		data.getBegin() == this.begin
		data.getEnd() == this.end
		data.getCancellationDate() != null
	}

	def "nullReference"() {
		when:
		ActivityInterface.getActivityReservationData(null)

		then:
		thrown(ActivityException)
	}

	def "emptyReference"() {
		when:
		ActivityInterface.getActivityReservationData("")

		then:
		thrown(ActivityException)
	}

	def "notExistsReference"() {
		when:
		ActivityInterface.getActivityReservationData("XPTO")

		then:
		thrown(ActivityException)
	}
}

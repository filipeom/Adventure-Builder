package pt.ulisboa.tecnico.softeng.hotel.services.local

import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.hotel.domain.Hotel
import pt.ulisboa.tecnico.softeng.hotel.domain.Processor
import pt.ulisboa.tecnico.softeng.hotel.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.hotel.domain.Room
import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException
import pt.ulisboa.tecnico.softeng.hotel.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.hotel.services.remote.TaxInterface


class HotelInterfaceCancelBookingMethodSpockTest2 extends SpockRollbackTestAbstractClass {
	def arrival = new LocalDate(2016, 12, 19)
	def departure = new LocalDate(2016, 12, 21)
	def NIF_BUYER = "123456789"
	def IBAN_BUYER = "IBAN_BUYER"

	def hotel
	def room
	def booking
	def taxInterface
	def bankInterface
	def hotelInterface

	@Override
	def populate4Test() {
		bankInterface = Mock(BankInterface)
		taxInterface = Mock(TaxInterface)
		hotelInterface = Mock(HotelInterface)
		def processor = new Processor(bankInterface, taxInterface)

		hotel = new Hotel("XPTO123", "Paris", "NIF", "IBAN", 20.0, 30.0, processor)
		room = new Room(hotel, "01", Type.DOUBLE)
		booking = room.reserve(Type.DOUBLE, arrival, departure, NIF_BUYER, IBAN_BUYER)
	}

	def "success"() {
		when:
		def cancel = hotelInterface.cancelBooking(booking.getReference())

		then:
		booking.isCancelled()
		booking.getCancellation() == cancel
	}

	def "doesNotExist"() {
		when:
		hotelInterface.cancelBooking("XPTO")

		then:
		thrown(HotelException)
	}

	def "nullReference"() {
		when:
		hotelInterface.cancelBooking(null)

		then:
		thrown(HotelException)
	}

	def "emptyReference"() {
		when:
		hotelInterface.cancelBooking("")

		then:
		thrown(HotelException)
	}

	def "successIntegration"() {
		given:
		TaxInterface.cancelInvoice(_)

		when:
		def cancel = hotelInterface.cancelBooking(booking.getReference())

		then:
		booking.isCancelled()
		booking.getCancellation() == cancel
	}

	def "doesNotExistIntegration"() {
		given:
		0 * TaxInterface.cancelInvoice(_)
		when:
		hotelInterface.cancelBooking("XPTO")
		then:
		thrown(HotelException)
	}
}
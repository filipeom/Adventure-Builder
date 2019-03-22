package pt.ulisboa.tecnico.softeng.hotel.services.local

import static org.junit.Assert.*

import java.util.stream.Collectors

import org.joda.time.LocalDate

import pt.ist.fenixframework.FenixFramework
import pt.ulisboa.tecnico.softeng.hotel.domain.Hotel
import pt.ulisboa.tecnico.softeng.hotel.domain.Room
import pt.ulisboa.tecnico.softeng.hotel.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException
import spock.lang.Shared
import spock.lang.Unroll

class HotelInterfaceBulkBookingMethodSpockTest extends SpockRollbackTestAbstractClass {
	@Shared def NIF_BUYER = '123456789'
	@Shared def IBAN_BUYER = 'IBAN_BUYER'
	@Shared def BULK_ID = 'BULK_ID'
	@Shared def ARRIVAL = new LocalDate(2016, 12, 19)
	@Shared def DEPARTURE = new LocalDate(2016, 12, 21)

	def hotel;
	def hotelInterface

	@Override
	def populate4Test() {
		hotel = new Hotel('XPTO123', 'Paris', 'NIF', 'IBAN', 20.0, 30.0)
		new Room(hotel, '01', Type.DOUBLE)
		new Room(hotel, '02', Type.SINGLE)
		new Room(hotel, '03', Type.DOUBLE)
		new Room(hotel, '04', Type.SINGLE)

		hotel = new Hotel('XPTO124', 'Paris', 'NIF2', 'IBAN2', 25.0, 35.0)
		new Room(hotel, '01', Type.DOUBLE)
		new Room(hotel, '02', Type.SINGLE)
		new Room(hotel, '03', Type.DOUBLE)
		new Room(hotel, '04', Type.SINGLE)

		hotelInterface = new HotelInterface()
	}

	@Unroll('bulkbooking #number rooms and the reference size is #refSize')
	def 'success'() {
		when: 'bulkbooking rooms'
		def references = hotelInterface.bulkBooking(number, ARRIVAL, DEPARTURE, NIF_BUYER,
				IBAN_BUYER, BULK_ID)

		then: 'references are returned'
		references.size() == refSize

		where:
		number | refSize
		2 || 2
		1 || 1
		8 || 8
	}

	def 'unsuccess'() {
		when: 'bulkbooking rooms'
		def references = hotelInterface.bulkBooking(9, ARRIVAL, DEPARTURE, NIF_BUYER,
				IBAN_BUYER, BULK_ID)

		then: 'references are returned'
		thrown(HotelException)
		and: 'no rooms are booked'
		hotelInterface.getAvailableRooms(8, ARRIVAL, DEPARTURE).size() == 8
	}

	def 'no rooms'() {
		given: 'there is a single hotel and has no rooms'
		for (def hotel : FenixFramework.getDomainRoot().getHotelSet()) {
			hotel.delete()
		}
		hotel = new Hotel('XPTO124', 'Paris', 'NIF', 'IBAN', 27.0, 37.0)

		when: 'a bulkbooking is done'
		hotelInterface.bulkBooking(3, ARRIVAL, DEPARTURE, NIF_BUYER, IBAN_BUYER, BULK_ID)

		then: 'a HotelException is thrown'
		thrown(HotelException)
	}

	@Unroll('invalid arguments: #number | #arrival | #departure | #nif | #iban')
	def 'invalid arguments'() {
		when: 'a bulkbooking is done with an invalid argument'
		hotelInterface.bulkBooking(number, arrival, departure, nif, iban, BULK_ID)

		then: 'a HotelException is thrown'
		thrown(HotelException)

		where:
		number | arrival | departure | nif | iban
		-1 | ARRIVAL | DEPARTURE | NIF_BUYER | IBAN_BUYER
		0 | ARRIVAL | DEPARTURE | NIF_BUYER | IBAN_BUYER
		2 | null | DEPARTURE | NIF_BUYER | IBAN_BUYER
		2 | ARRIVAL | null | NIF_BUYER  | IBAN_BUYER
		2 | ARRIVAL | DEPARTURE | null  | IBAN_BUYER
		2 | ARRIVAL | DEPARTURE | '  '  | IBAN_BUYER
		2 | ARRIVAL | DEPARTURE | NIF_BUYER | null
		2 | ARRIVAL | DEPARTURE | NIF_BUYER | '   '
	}

	def 'idempotent bulk booking'() {
		given: 'a bulkboooking of 4 rooms'
		def references = hotelInterface.bulkBooking(4, ARRIVAL, DEPARTURE, NIF_BUYER,
				IBAN_BUYER, BULK_ID)

		when: 'do a bulkboooking with the same id'
		def equalReferences = hotelInterface.bulkBooking(4, ARRIVAL, DEPARTURE, NIF_BUYER,
				IBAN_BUYER, BULK_ID)

		then: 'returns the same references'
		hotelInterface.getAvailableRooms(4, ARRIVAL, DEPARTURE).size() == 4
		references.stream().sorted().collect(Collectors.toList()).equals(
				equalReferences.stream().sorted().collect(Collectors.toList()))
	}
}

package pt.ulisboa.tecnico.softeng.hotel.services.local

import static org.junit.Assert.*

import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.hotel.domain.Hotel
import pt.ulisboa.tecnico.softeng.hotel.domain.Room
import pt.ulisboa.tecnico.softeng.hotel.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException
import pt.ulisboa.tecnico.softeng.hotel.services.remote.dataobjects.RestRoomBookingData
import spock.lang.Unroll

class HotelInterfaceGetRoomBookingDataMethodSpockTest extends SpockRollbackTestAbstractClass {
	def ARRIVAL = new LocalDate(2016, 12, 19)
	def DEPARTURE = new LocalDate(2016, 12, 24)
	def NIF_HOTEL = '123456700'
	def NIF_BUYER = '123456789'
	def IBAN_BUYER = 'IBAN_BUYER'
	def hotel
	def room
	def booking
	def hotelInterface

	@Override
	def populate4Test() {
		hotel = new Hotel('XPTO123', 'Lisboa', NIF_HOTEL, 'IBAN', 20.0, 30.0)
		room = new Room(hotel, '01', Type.SINGLE)
		booking = room.reserve(Type.SINGLE, ARRIVAL, DEPARTURE, NIF_BUYER, IBAN_BUYER)
		hotelInterface = new HotelInterface()
	}

	def 'success'() {
		when: 'get the booking data from a booking'
		RestRoomBookingData data = hotelInterface.getRoomBookingData(booking.getReference())

		then: 'it contains the correct information'
		booking.getReference().equals(data.getReference())
		data.getCancellation() == null
		data.getCancellationDate() == null
		data.getHotelName().equals(hotel.getName())
		data.getHotelCode().equals(hotel.getCode())
		data.getRoomNumber() == room.getNumber()
		data.getRoomType().equals(room.getType().name())
		data.getArrival() == booking.getArrival()
		data.getDeparture() == booking.getDeparture()
		data.getPrice() == booking.getPrice()
	}

	def 'success cancellation'() {
		given: 'a cancelled booking'
		booking.cancel();

		when: 'the get booking data'
		RestRoomBookingData data = hotelInterface.getRoomBookingData(booking.getCancellation());

		then: 'it contains the correct information'
		booking.getReference().equals(data.getReference())
		data.getCancellation() == booking.getCancellation()
		data.getCancellationDate() == booking.getCancellationDate()
		data.getHotelName().equals(hotel.getName())
		data.getHotelCode().equals(hotel.getCode())
		data.getRoomNumber() == room.getNumber()
		data.getRoomType().equals(room.getType().name())
		data.getArrival() == booking.getArrival()
		data.getDeparture() == booking.getDeparture()
		data.getPrice() == booking.getPrice()
	}

	@Unroll('invalid #reference')
	def 'invalid arguments'() {
		when:
		hotelInterface.getRoomBookingData(reference)

		then:
		thrown(HotelException)

		where:
		reference << [null, '', 'XPTO']
	}
}

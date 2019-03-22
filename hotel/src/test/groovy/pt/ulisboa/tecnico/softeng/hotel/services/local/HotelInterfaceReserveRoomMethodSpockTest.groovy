package pt.ulisboa.tecnico.softeng.hotel.services.local

import static org.junit.Assert.*

import org.joda.time.LocalDate

import pt.ist.fenixframework.FenixFramework
import pt.ulisboa.tecnico.softeng.hotel.domain.Hotel
import pt.ulisboa.tecnico.softeng.hotel.domain.Room
import pt.ulisboa.tecnico.softeng.hotel.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException
import pt.ulisboa.tecnico.softeng.hotel.services.remote.dataobjects.RestRoomBookingData

class HotelInterfaceReserveRoomMethodSpockTest extends SpockRollbackTestAbstractClass {
	def ARRIVAL = new LocalDate(2016, 12, 19)
	def DEPARTURE = new LocalDate(2016, 12, 24)
	def NIF_HOTEL = '123456789'
	def NIF_BUYER = '123456700'
	def IBAN_BUYER = 'IBAN_CUSTOMER'
	def IBAN_HOTEL = 'IBAN_HOTEL'
	def ADVENTURE_ID = 'AdventureId'

	def room
	def hotel
	def hotelInterface

	@Override
	def populate4Test() {
		hotel = new Hotel('XPTO123', 'Lisboa', NIF_HOTEL, IBAN_HOTEL, 20.0, 30.0)
		room = new Room(hotel, '01', Room.Type.SINGLE)
		hotelInterface = new HotelInterface()
	}

	def 'success'() {
		given: 'a booking data'
		def bookingData = new RestRoomBookingData("SINGLE", ARRIVAL, DEPARTURE, NIF_BUYER, IBAN_BUYER, ADVENTURE_ID)

		when: 'a reservation is done'
		bookingData = hotelInterface.reserveRoom(bookingData)

		then: 'a correct reference is returned'
		bookingData.getReference() != null
		bookingData.getReference().startsWith("XPTO123")
	}

	def 'no vancancy'() {
		given: 'the sigle room is booked'
		def bookingData = new RestRoomBookingData("SINGLE", ARRIVAL, new LocalDate(2016, 12, 25),
				NIF_BUYER, IBAN_BUYER, ADVENTURE_ID)
		hotelInterface.reserveRoom(bookingData)

		when: 'booking during the same period'
		bookingData = new RestRoomBookingData("SINGLE", ARRIVAL, new LocalDate(2016, 12, 25), NIF_BUYER,
				IBAN_BUYER, ADVENTURE_ID + "1")
		hotelInterface.reserveRoom(bookingData)

		then: 'throws an HotelException'
		def error = thrown(HotelException)
	}

	def 'no hotels'() {
		given: 'there is no hotels'
		for (def hotel: FenixFramework.getDomainRoot().getHotelSet()) {
			hotel.delete()
		}
		def bookingData = new RestRoomBookingData("SINGLE", ARRIVAL, DEPARTURE, NIF_BUYER,
				IBAN_BUYER, ADVENTURE_ID)

		when: 'reserve a room'
		hotelInterface.reserveRoom(bookingData)

		then: 'throws an HotelException'
		def error = thrown(HotelException)
	}

	def 'no rooms'() {
		given: 'there is no rooms'
		for (def room: hotel.getRoomSet()) {
			room.delete();
		}
		def bookingData = new RestRoomBookingData("SINGLE", ARRIVAL, new LocalDate(2016, 12, 25),
				NIF_BUYER, IBAN_BUYER, ADVENTURE_ID);

		when: 'reserve a room'
		hotelInterface.reserveRoom(bookingData);

		then: 'throws an HotelException'
		def error = thrown(HotelException)
	}
}

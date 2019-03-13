package pt.ulisboa.tecnico.softeng.hotel.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException

import spock.lang.Unroll
import spock.lang.Shared

class BookingConstructorSpockTest extends SpockRollbackTestAbstractClass {
  @Shared def ARRIVAL = new LocalDate(2016, 12, 19)
  @Shared def DEPARTURE = new LocalDate(2016, 12, 21)
  def ROOM_PRICE = 20.0
  def NIF_BUYER = "123456789"
  def IBAN_BUYER = "IBAN_BUYER"
  def room

  @Override
	def populate4Test() {
		def hotel = new Hotel("XPTO123", "Londres", "NIF", "IBAN", 20.0, 30.0)
		this.room = new Room(hotel, "01", Room.Type.SINGLE)
	}

  def 'success'(){
    given:
      def booking = new Booking(this.room, ARRIVAL, DEPARTURE, NIF_BUYER, IBAN_BUYER)

    expect:
      booking.getReference().startsWith(this.room.getHotel().getCode()) == true
      booking.getReference().length() > Hotel.CODE_SIZE
      booking.getArrival() == ARRIVAL
      booking.getDeparture() == DEPARTURE
      booking.getPrice() == ROOM_PRICE * 2
    }

  @Unroll
  def 'exceptions'(){
    when:
      new Booking(room, arrival, departure, NIF_BUYER, IBAN_BUYER)

    then:
      thrown(HotelException)

    where:
      room      | arrival | departure
      null      | ARRIVAL | DEPARTURE
      this.room | null    | DEPARTURE
      this.room | ARRIVAL | null
      this.room | ARRIVAL | ARRIVAL.minusDays(1)
  }

  def 'arrival Equal Departure'(){
    given:
      def booking = new Booking(this.room, ARRIVAL, ARRIVAL, NIF_BUYER, IBAN_BUYER)

    expect:
      booking.getArrival() == booking.getDeparture()
  }
  
}

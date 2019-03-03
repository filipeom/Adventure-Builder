package pt.ulisboa.tecnico.softeng.hotel.domain;

import org.joda.time.LocalDate;
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;

class BookingConstructorSpockTest extends SpockRollbackTestAbstractClass {
  def ARRIVAL = new LocalDate(2016, 12, 19)
  def DEPARTURE = new LocalDate(2016, 12, 21)
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

  def 'null Room'(){
    when:
    new Booking(null, ARRIVAL, DEPARTURE, NIF_BUYER, IBAN_BUYER)

    then:
    thrown(HotelException)
  }

  def 'null Arrival'(){
    when:
    new Booking(this.room, null, DEPARTURE, NIF_BUYER, IBAN_BUYER)

    then:
    thrown(HotelException)
  }

  def 'null Departure'(){
    when:
    new Booking(this.room, ARRIVAL, null, NIF_BUYER, IBAN_BUYER)

    then:
    thrown(HotelException)
  }

  def 'departure Before Arrival'(){
    when:
    new Booking(this.room, ARRIVAL, ARRIVAL.minusDays(1), NIF_BUYER, IBAN_BUYER)

    then:
    thrown(HotelException)
  }

  def 'arrival Equal Departure'(){
    when:
    ARRIVAL == DEPARTURE

    then:
    new Booking(this.room, ARRIVAL, ARRIVAL, NIF_BUYER, IBAN_BUYER)
  }
}

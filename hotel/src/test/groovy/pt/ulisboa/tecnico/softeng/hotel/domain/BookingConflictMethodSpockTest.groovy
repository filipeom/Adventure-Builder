package pt.ulisboa.tecnico.softeng.hotel.domain

import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException

class BookingConflictMethodSpockTest extends SpockRollbackTestAbstractClass {
  def arrival = LocalDate.parse('2016-12-19')
  def departure = LocalDate.parse('2016-12-24')
  def booking
  def NIF_HOTEL = "123456700"
  def NIF_BUYER = "123456789"
  def IBAN_BUYER = "IBAN_BUYER"

  @Override
	def populate4Test() {
		def hotel = new Hotel("XPTO123", "Londres", this.NIF_HOTEL, "IBAN", 20.0, 30.0)
		def room = new Room(hotel, "01", Room.Type.SINGLE)

		this.booking = new Booking(room, this.arrival, this.departure, this.NIF_BUYER, this.IBAN_BUYER)
	}
  //should use data tables to join test cases
  def 'arguments Are Consistent'() {
    when:
      def date1 = LocalDate.parse('2016-12-9')
      def date2 = LocalDate.parse('2016-12-15')

    then:
      this.booking.conflict(date1, date2) == false
  }

  def 'no Conflict Because It Is Cancelled'() {
    when:
      this.booking.cancel()

    then:
      this.booking.conflict(this.booking.getArrival(), this.booking.getDeparture()) == false
  }

  def 'arguments Are Inconsistent'() {
    when:
      this.booking.conflict(new LocalDate(2016, 12, 15), new LocalDate(2016, 12, 9)) == true

    then:
      thrown(HotelException)
  }

  def 'arguments Same Day'() {
    when:
      def date1 = LocalDate.parse('2016-12-9')
      def date2 = LocalDate.parse('2016-12-9')

    then:
      this.booking.conflict(date1, date2) == true
  }

  def 'arrival And Departure Are Before Booked'() {
    expect:
      this.booking.conflict(this.arrival.minusDays(10), this.arrival.minusDays(4)) == false
  }

  def 'arrival And Departure Are Before Booked But Departure Is Equal To Booked Arrival'() {
    expect:
      this.booking.conflict(this.arrival.minusDays(10), this.arrival) == false
  }

  def 'arrival And Departure Are After Booked'() {
    expect:
      this.booking.conflict(this.departure.plusDays(4), this.departure.plusDays(10)) == false
  }

  def 'arrival And Departure Are After Booked But Arrival Is Equal To Booked Departure'() {
    expect:
      this.booking.conflict(this.departure, this.departure.plusDays(10)) == false
  }

  def 'arrival Is Before Booked Arrival And Departure Is After Booked Departure'() {
    expect:
      this.booking.conflict(this.arrival.minusDays(4), this.departure.plusDays(4)) == true
  }

  def 'arrival Is Equal Booked Arrival And Departure Is After Booked Departure'() {
    expect:
      this.booking.conflict(this.arrival, this.departure.plusDays(4)) == true
  }

  def 'arrival Is Before Booked Arrival And Departure Is Equal Booked Departure'() {
    expect:
      this.booking.conflict(this.arrival.minusDays(4), this.departure) == true
  }

  def 'arrival Is Before Booked Arrival And Departure Is BetweenBooked'() {
    expect:
      this.booking.conflict(this.arrival.minusDays(4), this.departure.minusDays(3)) == true
  }

  def 'arrival Is Between Booked And Departure Is After Booked Departure'() {
    expect:
      this.booking.conflict(this.arrival.plusDays(3), this.departure.plusDays(6)) == true
  }

}

package pt.ulisboa.tecnico.softeng.hotel.domain;

import org.joda.time.LocalDate;

import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type;
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;

class HotelHasVacancyMethodSpockTest extends SpockRollbackTestAbstractClass {
  def arrival = LocalDate.parse('2016-12-19')
  def departure = LocalDate.parse('2016-12-21')
  def hotel
  def room
  def NIF_HOTEL = "123456700"
  def NIF_BUYER = "123456789"
  def IBAN_BUYER = "IBAN_BUYER"

  @Override
	def populate4Test() {
		this.hotel = new Hotel("XPTO123", "Paris", NIF_HOTEL, "IBAN", 20.0, 30.0)
		this.room = new Room(this.hotel, "01", Type.DOUBLE)
	}

  def 'has Vacancy'(){
    given:
    def room = this.hotel.hasVacancy(Type.DOUBLE, this.arrival, this.departure)

    expect:
    room != null
  }

  def 'no Vacancy'(){
    given:
    this.room.reserve(Type.DOUBLE, this.arrival, this.departure, this.NIF_BUYER, this.IBAN_BUYER)

    expect:
    this.hotel.hasVacancy(Type.DOUBLE, this.arrival, this.departure) == null
  }

  def 'no Vacancy Empty Room Set'(){
    given:
    def otherHotel = new Hotel("XPTO124", "Paris Germain", "NIF2", "IBAN", 25.0, 35.0)

    expect:
    otherHotel.hasVacancy(Type.DOUBLE, this.arrival, this.departure) == null
  }

  def 'null Type'(){
    when:
    this.hotel.hasVacancy(null, this.arrival, this.departure)

    then:
    thrown(HotelException)
  }

  def 'null Arrival'(){
    when:
    this.hotel.hasVacancy(Type.DOUBLE, null, this.departure)

    then:
    thrown(HotelException)
  }

  def 'null Departure'(){
    when:
    this.hotel.hasVacancy(Type.DOUBLE, this.arrival, null)

    then:
    thrown(HotelException)
  }

}

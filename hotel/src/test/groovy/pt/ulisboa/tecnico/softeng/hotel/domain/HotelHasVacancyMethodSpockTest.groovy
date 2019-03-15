package pt.ulisboa.tecnico.softeng.hotel.domain

import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException

import spock.lang.Unroll

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
    when:
      def room = this.hotel.hasVacancy(Type.DOUBLE, this.arrival, this.departure)

    then:
      room != null
      room.getNumber() == "01"
  }

  def 'no Vacancy'(){
    when:
      this.room.reserve(Type.DOUBLE, this.arrival, this.departure, this.NIF_BUYER, this.IBAN_BUYER)

    then:
      this.hotel.hasVacancy(Type.DOUBLE, this.arrival, this.departure) == null
  }

  def 'no Vacancy Empty Room Set'(){
    when:
      def otherHotel = new Hotel("XPTO124", "Paris Germain", "NIF2", "IBAN", 25.0, 35.0)

    then:
      otherHotel.hasVacancy(Type.DOUBLE, this.arrival, this.departure) == null
  }

  @Unroll
  def 'exceptions'(){
    when:
      this.hotel.hasVacancy(type, arrival, departure)

    then:
      thrown(HotelException)

    where:
      type        | arrival      | departure
      null        | this.arrival | this.departure
      Type.DOUBLE | null         | this.departure
      Type.DOUBLE | this.arrival | null
  }

}

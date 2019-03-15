package pt.ulisboa.tecnico.softeng.hotel.domain

import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException
import pt.ulisboa.tecnico.softeng.hotel.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.hotel.services.remote.TaxInterface
import spock.lang.Shared
import spock.lang.Unroll

class RoomReserveMethodSpockTest extends SpockRollbackTestAbstractClass {
  @Shared def ARRIVAL = LocalDate.parse("2016-12-19")
  @Shared def DEPARTURE = LocalDate.parse("2016-12-24")
  @Shared def HOTEL_NIF = "123456700"
  @Shared def BUYER_NIF = "123456789"
  @Shared def IBAN_BUYER = "IBAN_BUYER"

  def room

  @Override
  def populate4Test() {
    def hotel = new Hotel("XPTO123", "Lisboa", HOTEL_NIF, "IBAN", 20.0, 30.0)
    this.room = new Room(hotel, "01", Type.SINGLE)
  }

  def "success"() {
    when:
    def booking = this.room.reserve(Type.SINGLE, ARRIVAL, DEPARTURE, BUYER_NIF, IBAN_BUYER)

    then:
    this.room.getBookingSet().size() == 1
    booking.getReference().length() > 0
    booking.getArrival() == ARRIVAL
    booking.getDeparture() == DEPARTURE
  }

  @Unroll("Reserve: #type, #arr, #dep")
  def "exceptions"() {
    when: 
    this.room.reserve(type, arr, dep, BUYER_NIF, IBAN_BUYER)

    then: 
    thrown(HotelException)

    where:
    type        | arr     | dep
    Type.DOUBLE | ARRIVAL | DEPARTURE
    null        | ARRIVAL | DEPARTURE
    Type.SINGLE | null    | DEPARTURE
    Type.SINGLE | ARRIVAL | null
  }

  def "all conflict"() {
    given: 
    this.room.reserve(Type.SINGLE, ARRIVAL, DEPARTURE, BUYER_NIF, IBAN_BUYER)

    when: 
    this.room.reserve(Type.SINGLE, ARRIVAL, DEPARTURE, BUYER_NIF, IBAN_BUYER)

    then: 
    thrown(HotelException)

    and: 
    this.room.getBookingSet().size() == 1
  }
}

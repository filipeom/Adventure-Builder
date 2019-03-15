package pt.ulisboa.tecnico.softeng.hotel.domain

import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type
import pt.ulisboa.tecnico.softeng.hotel.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.hotel.services.remote.TaxInterface

class RoomGetBookingMethodSpockTest extends SpockRollbackTestAbstractClass {
  def arrival = LocalDate.parse("2016-12-19")
  def departure = LocalDate.parse("2016-12-24")

  def hotel
  def room
  def booking

  def BUYER_NIF = "123456789"
  def BUYER_IBAN = "IBAN_BUYER"

  @Override
  def populate4Test() {
    hotel = new Hotel("XPTO123", "Lisboa", "NIF", "IBAN", 20.0, 30.0)
    room = new Room(hotel, "01", Type.SINGLE)
    booking = room.reserve(Type.SINGLE, arrival, departure, BUYER_NIF, BUYER_IBAN)
  }


  def "success"() {
    expect:
    booking == room.getBooking(booking.getReference())
  }

  def "success cancelled"() {
    when:
    booking.cancel()

    then:
    booking == room.getBooking(booking.getCancellation())
  }

  def "does not exist"() {
    expect:
    room.getBooking("XPTO") == null
  }
}

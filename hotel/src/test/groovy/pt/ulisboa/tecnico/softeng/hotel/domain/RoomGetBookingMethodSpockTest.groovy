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
    this.hotel = new Hotel("XPTO123", "Lisboa", "NIF", "IBAN", 20.0, 30.0)
    this.room = new Room(this.hotel, "01", Type.SINGLE)
    this.booking = this.room.reserve(Type.SINGLE, this.arrival, this.departure, this.BUYER_NIF, this.BUYER_IBAN)
  }


  def "success"() {
    expect:
    this.booking == this.room.getBooking(this.booking.getReference())
  }

  def "success cancelled"() {
    when:
    this.booking.cancel()

    then:
    this.booking == this.room.getBooking(this.booking.getCancellation())
  }

  def "does not exist"() {
    expect:
    this.room.getBooking("XPTO") == null
  }
}

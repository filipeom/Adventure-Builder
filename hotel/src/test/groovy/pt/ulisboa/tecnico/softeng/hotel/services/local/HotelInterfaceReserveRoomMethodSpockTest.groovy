package pt.ulisboa.tecnico.softeng.hotel.services.local

import org.joda.time.LocalDate

import pt.ist.fenixframework.FenixFramework
import pt.ulisboa.tecnico.softeng.hotel.domain.Hotel
import pt.ulisboa.tecnico.softeng.hotel.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.hotel.domain.Room
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException
import pt.ulisboa.tecnico.softeng.hotel.services.remote.dataobjects.RestRoomBookingData
import spock.lang.Shared
import spock.lang.Unroll

class HotelInterfaceReserveRoomMethodSpockTest extends SpockRollbackTestAbstractClass {
  def ARRIVAL = LocalDate.parse("2016-12-19")
  def DEPARTURE = LocalDate.parse("2016-12-24")
  @Shared DATE = LocalDate.parse("2016-12-25")
  def NIF_HOTEL = "123456789"
  def NIF_BUYER = "123456700"
  def IBAN_BUYER = "IBAN_CUSTOMER"
  def IBAN_HOTEL = "IBAN_HOTEL"
  def ADVENTURE_ID = "ADVENTURE_ID"
  def hotel

  @Override
  def populate4Test() {
    this.hotel = new Hotel("XPTO123", "Lisboa", NIF_HOTEL, IBAN_BUYER, 20.0, 30.0)
    new Room(this.hotel, "01", Room.Type.SINGLE)
  }

  def "success"() {
    given: "a booking"
    def bookingData = new RestRoomBookingData("SINGLE", ARRIVAL, DEPARTURE, NIF_BUYER, IBAN_BUYER, ADVENTURE_ID)

    when: "reserving a room"
    bookingData = HotelInterface.reserveRoom(bookingData)

    then: "it should succeed: get a reference"
    bookingData.getReference() != null
    bookingData.getReference().startsWith("XPTO123") 
  }

  def "no hotels"() {
    given: "delete all hotels"
    FenixFramework.getDomainRoot().getHotelSet().stream().forEach({ h -> h.delete() })

    when: "creating a new booking"
    def bookingData = new RestRoomBookingData("SINGLE", ARRIVAL, DEPARTURE, NIF_BUYER, IBAN_BUYER, ADVENTURE_ID)
    HotelInterface.reserveRoom(bookingData)

    then: "throws an exception"
    thrown(HotelException)
  }

  def "no vacancy"() {
    given: "book a reservation"
    def bookingData = new RestRoomBookingData("SINGLE", ARRIVAL, DATE, NIF_BUYER, IBAN_BUYER, ADVENTURE_ID)
    HotelInterface.reserveRoom(bookingData)

    when: "booking a new reservation"
    bookingData = new RestRoomBookingData("SINGLE", ARRIVAL, DATE, NIF_BUYER, IBAN_BUYER, ADVENTURE_ID + "1")
    HotelInterface.reserveRoom(bookingData)

    then: "throws an exception"
    thrown(HotelException)
  }

  def "no rooms"() {
    given: "delete rooms"
    this.hotel.getRoomSet().stream().forEach({ r -> r.delete() })

    when: "making a reservation"
    def bookingData = new RestRoomBookingData("SINGLE", ARRIVAL, DATE, NIF_BUYER, IBAN_BUYER, ADVENTURE_ID)
    HotelInterface.reserveRoom(bookingData)

    then: "throws an exception"
    thrown(HotelException)
  }
}

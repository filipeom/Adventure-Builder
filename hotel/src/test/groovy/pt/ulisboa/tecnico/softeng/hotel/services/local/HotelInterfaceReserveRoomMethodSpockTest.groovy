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
//are you missing one test, HotelInterfaceGetRoomMethod?
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
    hotel = new Hotel("XPTO123", "Lisboa", NIF_HOTEL, IBAN_BUYER, 20.0, 30.0)
    new Room(hotel, "01", Room.Type.SINGLE)
  }

  def "success"() {
    given: 
    def bookingData = new RestRoomBookingData("SINGLE", ARRIVAL, DEPARTURE, NIF_BUYER, IBAN_BUYER, ADVENTURE_ID)

    when: 
    bookingData = HotelInterface.reserveRoom(bookingData)

    then:
    with(bookingData) {
      getReference() != null
      getReference().startsWith("XPTO123")
    }
  }

  def "no hotels"() {
    given: 
    FenixFramework.getDomainRoot().getHotelSet().stream().forEach({ h -> h.delete() })

    when: 
    def bookingData = new RestRoomBookingData("SINGLE", ARRIVAL, DEPARTURE, NIF_BUYER, IBAN_BUYER, ADVENTURE_ID)
    HotelInterface.reserveRoom(bookingData)

    then:
    thrown(HotelException)
  }

  def "no vacancy"() {
    given:
    def bookingData = new RestRoomBookingData("SINGLE", ARRIVAL, DATE, NIF_BUYER, IBAN_BUYER, ADVENTURE_ID)
    HotelInterface.reserveRoom(bookingData)

    when:
    bookingData = new RestRoomBookingData("SINGLE", ARRIVAL, DATE, NIF_BUYER, IBAN_BUYER, ADVENTURE_ID + "1")
    HotelInterface.reserveRoom(bookingData)

    then: 
    thrown(HotelException)
  }

  def "no rooms"() {
    given: 
    hotel.getRoomSet().stream().forEach({ r -> r.delete() })

    when: 
    def bookingData = new RestRoomBookingData("SINGLE", ARRIVAL, DATE, NIF_BUYER, IBAN_BUYER, ADVENTURE_ID)
    HotelInterface.reserveRoom(bookingData)

    then:
    thrown(HotelException)
  }
}

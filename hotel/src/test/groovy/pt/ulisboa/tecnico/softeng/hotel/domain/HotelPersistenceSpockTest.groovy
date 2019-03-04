package pt.ulisboa.tecnico.softeng.hotel.domain;

import org.joda.time.LocalDate;

import pt.ist.fenixframework.FenixFramework
import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type;

class HotelPersistenceSpockTest extends SpockPersistenceTestAbstractClass {
  def HOTEL_NIF = "123456789"
  def HOTEL_IBAN = "IBAN"
  def HOTEL_NAME = "Berlin Plaza"
  def HOTEL_CODE = "H123456"
  def ROOM_NUMBER = "01"
  def CLIENT_NIF = "123458789"
  def CLIENT_IBAN = "IBANC"

  def arrival = LocalDate.parse('2017-12-15')
  def departure = LocalDate.parse('2017-12-19')

  def 'set Up'(){
    for (def hotel : FenixFramework.getDomainRoot().getHotelSet()) {
      hotel.delete();
    }
  }

  @Override
  def whenCreateInDatabase() {
    def hotel = new Hotel(HOTEL_CODE, HOTEL_NAME, HOTEL_NIF, HOTEL_IBAN, 10.0, 20.0)
    new Room(hotel, ROOM_NUMBER, Type.DOUBLE)
    hotel.reserveRoom(Type.DOUBLE, this.arrival, this.departure, CLIENT_NIF, CLIENT_IBAN, "adventureId")
  }

  @Override
  def thenAssert() {
    FenixFramework.getDomainRoot().getHotelSet().size() == 1

    def hotels = new ArrayList<>(FenixFramework.getDomainRoot().getHotelSet());
    def hotel = hotels.get(0);

    assert HOTEL_NAME == hotel.getName()
    assert HOTEL_CODE == hotel.getCode()
    assert HOTEL_IBAN == hotel.getIban()
    assert HOTEL_NIF == hotel.getNif()
    assert 10.0 == hotel.getPriceSingle().round()
    assert 20.0 == hotel.getPriceDouble().round()
    assert 1 == hotel.getRoomSet().size()
    def processor = hotel.getProcessor()
    assert processor != null
    assert 1 == processor.getBookingSet().size()

    def rooms = new ArrayList<>(hotel.getRoomSet())
    def room = rooms.get(0);

    assert ROOM_NUMBER == room.getNumber()
    assert Type.DOUBLE == room.getType()
    assert 1 == room.getBookingSet().size()

    def bookings = new ArrayList<>(room.getBookingSet())
    def booking = bookings.get(0)

    assert booking.getReference() != null
    assert this.arrival == booking.getArrival()
    assert this.departure == booking.getDeparture()
    assert CLIENT_IBAN == booking.getBuyerIban()
    assert CLIENT_NIF == booking.getBuyerNif()
    assert HOTEL_NIF == booking.getProviderNif()
    assert 80.0 == booking.getPrice().round()
    assert room == booking.getRoom()
    assert booking.getTime() != null
    assert booking.getProcessor() != null
  }

  @Override
  def deleteFromDatabase() {
    for (def hotel : FenixFramework.getDomainRoot().getHotelSet()) {
      hotel.delete();
    }
  }

}

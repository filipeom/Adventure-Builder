package pt.ulisboa.tecnico.softeng.hotel.domain

import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException
import spock.lang.Shared
import spock.lang.Unroll

class RoomConstructorMethodSpockTest extends SpockRollbackTestAbstractClass {
  @Shared def hotel;

  @Override
  def populate4Test() {
    hotel = new Hotel("XPTO123", "Lisboa", "NIF", "IBAN", 20.0, 30.0)
  }

  def "success"() {
    when:
    def room = new Room(hotel, "01", Type.DOUBLE)

    then:
    with(room) {
      getHotel()  == hotel
      getNumber() == "01"
      getType()   == Type.DOUBLE
    }
    with(hotel) {
      getRoomSet().size() == 1
    }
  }

  @Unroll("Room: #hot, #room, #type")
  def "exceptions"() {
    when: 
    new Room(hot, room, type)

    then: 
    thrown(HotelException)

    where:
    hot   | room | type
    null  | "01" | Type.DOUBLE
    hotel | null | Type.DOUBLE
    hotel | ""   | Type.DOUBLE
    hotel |"    "| Type.DOUBLE
    hotel |"JOSE"| Type.DOUBLE
    hotel | "01" | null
  }

  def "non unique room number"() {
    given: 
    new Room(hotel, "01", Type.SINGLE)

    when: 
    new Room(hotel, "01", Type.DOUBLE)

    then: 
    thrown(HotelException)

    and:
    hotel.getRoomSet().size() == 1
  }
}

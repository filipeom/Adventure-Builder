package pt.ulisboa.tecnico.softeng.hotel.domain

import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException
import spock.lang.Unroll

class RoomConstructorMethodSpockTest extends SpockRollbackTestAbstractClass {
  def hotel;

  @Override
  def populate4Test() {
    this.hotel = new Hotel("XPTO123", "Lisboa", "NIF", "IBAN", 20.0, 30.0)
  }

  def "success"() {
    when:
    def room = new Room(this.hotel, "01", Type.DOUBLE)

    then:
    room.getHotel()  == this.hotel
    room.getNumber() == "01"
    room.getType()   == Type.DOUBLE
    this.hotel.getRoomSet().size() == 1
  }

  @Unroll("Room: #hotel, #room, #type")
  def "exceptions"() {
    when: 
    new Room(hotel, room, type)

    then: 
    thrown(HotelException)

    where:
    hotel      | room | type
    null       | "01" | Type.DOUBLE
    this.hotel | null | Type.DOUBLE
    this.hotel | ""   | Type.DOUBLE
    this.hotel |"    "| Type.DOUBLE
    this.hotel |"JOSE"| Type.DOUBLE
    this.hotel | "01" | null
  }

  def "non unique room number"() {
    given: 
    new Room(this.hotel, "01", Type.SINGLE)

    when: 
    new Room(this.hotel, "01", Type.DOUBLE)

    then: 
    thrown(HotelException)

    and:
    this.hotel.getRoomSet().size() == 1
  }
}

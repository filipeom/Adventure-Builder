package pt.ulisboa.tecnico.softeng.hotel.domain

import pt.ist.fenixframework.FenixFramework
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException

class HotelGetPriceMethodSpockTest extends SpockRollbackTestAbstractClass {

  def hotel
  def priceSingle = 20.0
  def priceDouble = 30.0

  @Override
  def populate4Test() {
    this.hotel = new Hotel("XPTO123", "Lisboa", "NIF", "IBAN", this.priceSingle, this.priceDouble)
  }

  def 'successSingle'() {
    expect:
    this.hotel.getPrice(Room.Type.SINGLE) == priceSingle
  }

  def 'successDouble'() {
    expect:
    this.hotel.getPrice(Room.Type.DOUBLE) == priceDouble
  }

  def 'null Type'() {
    when:
    this.hotel.getPrice(null)

    then:
    thrown(HotelException)
  }
}

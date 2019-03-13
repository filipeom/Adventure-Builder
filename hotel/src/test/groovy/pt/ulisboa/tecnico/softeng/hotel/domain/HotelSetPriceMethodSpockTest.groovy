package pt.ulisboa.tecnico.softeng.hotel.domain

import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException

import spock.lang.Unroll

class HotelSetPriceMethodSpockTest extends SpockRollbackTestAbstractClass {

  def hotel;
  def price = 25.0

  @Override
	def populate4Test() {
		this.hotel = new Hotel("XPTO123", "Lisboa", "NIF", "IBAN", this.price + 5.0, this.price + 10.0);
	}

  def 'success Single'(){
    given:
      this.hotel.setPrice(Room.Type.SINGLE, this.price)

    expect:
      this.price == this.hotel.getPrice(Room.Type.SINGLE)
  }

  def 'success Double'(){
    given:
      this.hotel.setPrice(Room.Type.DOUBLE, this.price)

    expect:
      this.price == this.hotel.getPrice(Room.Type.DOUBLE)
  }

  @Unroll
  def 'negative Price'(){
    when:
      this.hotel.setPrice(type, price)

    then:
      thrown(HotelException)

    where:
      type             | price
      Room.Type.SINGLE | -1.0
      Room.Type.DOUBLE | -1.0
  }

}

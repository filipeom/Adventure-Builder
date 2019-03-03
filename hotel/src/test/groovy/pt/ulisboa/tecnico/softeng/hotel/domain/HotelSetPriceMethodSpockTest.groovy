package pt.ulisboa.tecnico.softeng.hotel.domain;

import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;

class HotelSetPriceMethodSpockTest extends SpockRollbackTestAbstractClass {

  def hotel;
  def price = 25.0;

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

  def 'negative Price Single'(){
    when:
    this.hotel.setPrice(Room.Type.SINGLE, -1.0)

    then:
    thrown(HotelException)
  }

  def 'negative Price Double'(){
    when:
    this.hotel.setPrice(Room.Type.DOUBLE, -1.0)

    then:
    thrown(HotelException)
  }

}

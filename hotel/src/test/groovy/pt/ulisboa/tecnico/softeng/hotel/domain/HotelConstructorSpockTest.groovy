package pt.ulisboa.tecnico.softeng.hotel.domain;

import pt.ist.fenixframework.FenixFramework;
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;

class HotelConstructorSpockTest extends SpockRollbackTestAbstractClass {

  def IBAN = "IBAN";
  def NIF = "NIF";

  def HOTEL_NAME = "Londres";
  def HOTEL_CODE = "XPTO123";

  def PRICE_SINGLE = 20.0;
  def PRICE_DOUBLE = 30.0;

  @Override
	def populate4Test() {}

  def 'success'() {
    given:
    def hotel = new Hotel(HOTEL_CODE, HOTEL_NAME, NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE)

    expect:
    hotel.getName() == HOTEL_NAME
    hotel.getCode().length() == Hotel.CODE_SIZE
    hotel.getRoomSet().size() == 0
    FenixFramework.getDomainRoot().getHotelSet().size() == 1
    hotel.getPrice(Room.Type.SINGLE) == PRICE_SINGLE
    hotel.getPrice(Room.Type.DOUBLE) == PRICE_DOUBLE
  }

  def 'null Code'(){
    when:
    new Hotel(null, HOTEL_NAME, NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE)

    then:
    thrown(HotelException)
  }

  def 'blanck Code'(){
    when:
    new Hotel("      ", HOTEL_NAME, NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE)

    then:
    thrown(HotelException)
  }

  def 'empty Code'(){
    when:
    new Hotel("", HOTEL_NAME, NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE)

    then:
    thrown(HotelException)
  }

  def 'null Name'(){
    when:
    new Hotel(HOTEL_CODE, null, NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE)

    then:
    thrown(HotelException)
  }

  def 'blanck Name'(){
    when:
    new Hotel(HOTEL_CODE, "  ", NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE)

    then:
    thrown(HotelException)
  }

  def 'empty Name'(){
    when:
    new Hotel(HOTEL_CODE, "", NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE)

    then:
    thrown(HotelException)
  }

  def 'code Size Less'(){
    when:
    new Hotel("123456", HOTEL_NAME, NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE)

    then:
    thrown(HotelException)
  }

  def 'code Size More'(){
    when:
    new Hotel("12345678", HOTEL_NAME, NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE)

    then:
    thrown(HotelException)
  }

  def 'code Not Unique'(){
    when:
    new Hotel(HOTEL_CODE, HOTEL_NAME, NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE)
		new Hotel(HOTEL_CODE, HOTEL_NAME + " City", NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE)

    then:
    thrown(HotelException)
  }

  def 'nif Not Unique'(){
    when:
    new Hotel(HOTEL_CODE, HOTEL_NAME, NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE)
		new Hotel(HOTEL_CODE + "_new", HOTEL_NAME + "_New", NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE)

    then:
    thrown(HotelException)
  }

  def 'negative Price Single'(){
    when:
    new Hotel(HOTEL_CODE, HOTEL_NAME, NIF, IBAN, -1.0, PRICE_DOUBLE)

    then:
    thrown(HotelException)
  }

  def 'negative Price Double'(){
    when:
    new Hotel(HOTEL_CODE, HOTEL_NAME, NIF, IBAN, PRICE_SINGLE, -1.0)

    then:
    thrown(HotelException)
  }

}

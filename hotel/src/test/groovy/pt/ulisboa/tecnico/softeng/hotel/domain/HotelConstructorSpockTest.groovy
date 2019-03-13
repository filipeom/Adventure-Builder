package pt.ulisboa.tecnico.softeng.hotel.domain

import pt.ist.fenixframework.FenixFramework
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException

import spock.lang.Unroll
import spock.lang.Shared

class HotelConstructorSpockTest extends SpockRollbackTestAbstractClass {

  def IBAN = "IBAN"
  def NIF = "NIF"

  @Shared def HOTEL_NAME = "Londres"
  @Shared def HOTEL_CODE = "XPTO123"

  @Shared def PRICE_SINGLE = 20.0
  @Shared def PRICE_DOUBLE = 30.0

  @Override
	def populate4Test() {}

  def 'success'() {
    given:
      def hotel =  new Hotel(HOTEL_CODE, HOTEL_NAME, NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE)

    expect:
      hotel.getName() == HOTEL_NAME
      hotel.getCode().length() == Hotel.CODE_SIZE
      hotel.getRoomSet().size() == 0
      FenixFramework.getDomainRoot().getHotelSet().size() == 1
      hotel.getPrice(Room.Type.SINGLE) == PRICE_SINGLE
      hotel.getPrice(Room.Type.DOUBLE) == PRICE_DOUBLE
  }

  @Unroll("Hotel: #code, #name, #price_single, #price_double")
  def 'exceptions'(){
    when:
      new Hotel(code, name, NIF, IBAN, price_single, price_double)

    then:
      thrown(HotelException)

    where:
      code       | name       | price_single | price_double
      null       | HOTEL_NAME | PRICE_SINGLE | PRICE_DOUBLE
      "      "   | HOTEL_NAME | PRICE_SINGLE | PRICE_DOUBLE
      ""         | HOTEL_NAME | PRICE_SINGLE | PRICE_DOUBLE
      HOTEL_CODE | null       | PRICE_SINGLE | PRICE_DOUBLE
      HOTEL_CODE | "  "       | PRICE_SINGLE | PRICE_DOUBLE
      HOTEL_CODE | ""         | PRICE_SINGLE | PRICE_DOUBLE
      "123456"   | HOTEL_NAME | PRICE_SINGLE | PRICE_DOUBLE
      "12345678" | HOTEL_NAME | PRICE_SINGLE | PRICE_DOUBLE
      HOTEL_CODE | HOTEL_NAME | -1.0         | PRICE_DOUBLE
      HOTEL_CODE | HOTEL_NAME | PRICE_SINGLE | -1.0
  }


  def 'code Not Unique'(){
    given:
      new Hotel(HOTEL_CODE, HOTEL_NAME, NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE)

    when:
		  new Hotel(HOTEL_CODE, HOTEL_NAME + " City", NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE)

    then:
      thrown(HotelException)
  }

  def 'nif Not Unique'(){
    given:
      new Hotel(HOTEL_CODE, HOTEL_NAME, NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE)

    when:
		  new Hotel(HOTEL_CODE + "_new", HOTEL_NAME + "_New", NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE)

    then:
      thrown(HotelException)
  }


}

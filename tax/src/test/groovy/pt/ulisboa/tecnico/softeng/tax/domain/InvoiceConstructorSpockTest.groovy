package pt.ulisboa.tecnico.softeng.tax.domain

import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.tax.domain.Buyer
import pt.ulisboa.tecnico.softeng.tax.domain.IRS
import pt.ulisboa.tecnico.softeng.tax.domain.Invoice
import pt.ulisboa.tecnico.softeng.tax.domain.ItemType
import pt.ulisboa.tecnico.softeng.tax.domain.Seller
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException
import spock.lang.Shared
import spock.lang.Unroll

class InvoiceConstructorSpockTest extends SpockRollbackTestAbstractClass {
  def SELLER_NIF = "123456789"
  def BUYER_NIF = "987654321"
  def FOOD = "FOOD"
  def TAX = 23
  @Shared def VALUE = 16
  @Shared def date1 = LocalDate.parse('2018-02-13')
  @Shared def date2 = LocalDate.parse('1969-12-31')

  @Shared def seller
  @Shared def buyer
  @Shared def itemType

  @Override
  def populate4Test() {
    def irs = IRS.getIRSInstance()
    seller = new Seller(irs, SELLER_NIF, "José Vendido", "Somewhere")
    buyer = new Buyer(irs, BUYER_NIF, "Manuel Comprado", "Anywhere")
    itemType = new ItemType(irs, FOOD, TAX)
  }

  def "success"() {
    when: 
    def invoice = new Invoice(VALUE, date1, itemType, seller, buyer)

    then: 
    with(invoice) {
      getReference() != null
      getValue()     == VALUE
      getItemType()  == itemType
      getSeller()    == seller
      getBuyer()     == buyer
      getIva()       == (VALUE * TAX / 100.0)
      isCancelled()  == false
      getDate().isEqual(date1)
    }
    invoice == seller.getInvoiceByReference(invoice.getReference())
    invoice == buyer.getInvoiceByReference(invoice.getReference())

  }

  @Unroll("Invoice: #value, #date, #item, #sell, #buy")
  def "exceptions"() {
    when: 
    new Invoice(value, date, item, sell, buy)

    then:
    thrown(TaxException)

    where:
    value | date  | item     | sell   | buy
    VALUE | date1 | itemType | null   | buyer 
    VALUE | date1 | itemType | seller | null
    VALUE | date1 | null     | seller | buyer
    0     | date1 | itemType | seller | buyer
    -23.6f| date1 | itemType | seller | buyer
    VALUE | null  | itemType | seller | buyer
    VALUE | date2 | itemType | seller | buyer
  }

  def "date equals to 1970"() {
    when:
    new Invoice(VALUE, LocalDate.parse("1970-01-01"), itemType, seller, buyer)

    then: 
    notThrown(TaxException)
  }
}

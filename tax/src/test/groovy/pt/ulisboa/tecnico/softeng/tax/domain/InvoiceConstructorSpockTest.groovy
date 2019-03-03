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

  def seller
  def buyer
  def itemType

  @Override
  def populate4Test() {
    def irs = IRS.getIRSInstance()
    this.seller = new Seller(irs, SELLER_NIF, "José Vendido", "Somewhere")
    this.buyer = new Buyer(irs, BUYER_NIF, "Manuel Comprado", "Anywhere")
    this.itemType = new ItemType(irs, FOOD, TAX)
  }

  def "success"() {
    when: 
    def invoice = new Invoice(VALUE, this.date1, this.itemType, this.seller, this.buyer)

    then: 
    invoice.getReference() != null
    invoice.getValue()     == VALUE
    invoice.getItemType()  == this.itemType
    invoice.getSeller()    == this.seller
    invoice.getBuyer()     == this.buyer
    invoice.getIva()       == (VALUE * TAX / 100.0)
    invoice.isCancelled()  == false
    invoice.getDate().isEqual(this.date1)
    invoice == this.seller.getInvoiceByReference(invoice.getReference())
    invoice == this.buyer.getInvoiceByReference(invoice.getReference())

  }

  @Unroll("Invoice: #value, #date, #item, #seller, #buyer")
  def "exceptions"() {
    when: "Invoice with invalid args"
    new Invoice(value, date, item, seller, buyer)

    then: "throws an exception"
    thrown(TaxException)

    where:
    value | date       | item          | seller      | buyer
    VALUE | this.date1 | this.itemType | null        | this.buyer 
    VALUE | this.date1 | this.itemType | this.seller | null
    VALUE | this.date1 | null          | this.seller | this.buyer
    0     | this.date1 | this.itemType | this.seller | this.buyer
    -23.6f| this.date1 | this.itemType | this.seller | this.buyer
    VALUE | null       | this.itemType | this.seller | this.buyer
    VALUE | this.date2 | this.itemType | this.seller | this.buyer
  }

  def "date equals to 1970"() {
    when: "Invoice with a date in 1970"
    new Invoice(VALUE, LocalDate.parse("1970-01-01"), this.itemType, this.seller, this.buyer)

    then: "should not throw an exception"
    notThrown(TaxException)
  }
}

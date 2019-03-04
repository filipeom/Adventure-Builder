package pt.ulisboa.tecnico.softeng.tax.domain

import org.joda.time.LocalDate;

import pt.ulisboa.tecnico.softeng.tax.domain.Buyer
import pt.ulisboa.tecnico.softeng.tax.domain.IRS
import pt.ulisboa.tecnico.softeng.tax.domain.Invoice
import pt.ulisboa.tecnico.softeng.tax.domain.ItemType
import pt.ulisboa.tecnico.softeng.tax.domain.Seller
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException

class BuyerToReturnSpockTest extends SpockRollbackTestAbstractClass {
  def SELLER_NIF = "123456789"
  def BUYER_NIF = "987654321"
  def FOOD = "FOOD"
  def TAX = 10
  def date = LocalDate.parse("2018-02-13")

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
    given: 
    new Invoice(100, this.date, this.itemType, this.seller, this.buyer)
    new Invoice(100, this.date, this.itemType, this.seller, this.buyer)
    new Invoice(50, this.date, this.itemType, this.seller, this.buyer)

    when:
    def val = this.buyer.taxReturn(2018)

    then:
    val == 1.25f
  }

  def "year without invoices"() {
    given:
    new Invoice(100, this.date, this.itemType, this.seller, this.buyer)
    new Invoice(100, this.date, this.itemType, this.seller, this.buyer)
    new Invoice(50, this.date, this.itemType, this.seller, this.buyer)

    when:
    def val = this.buyer.taxReturn(2017)

    then:
    val == 0.0f
  }

  def "no invoices"() {
    when:
    def val = this.buyer.taxReturn(2018)

    then:
    val == 0.0f
  }

  def "before 1970"() {
    when:
    def val = this.buyer.taxReturn(1969)

    then:
    thrown(TaxException)
  }

  def "after 1970"() {
    given:
    new Invoice(100, LocalDate.parse("1970-02-13"), this.itemType, this.seller, this.buyer)

    when:
    def val = this.buyer.taxReturn(1970)

    then:
    val == 0.5f
  }

  def "ignore cancelled"() {
    given: 
    new Invoice(100, this.date, this.itemType, this.seller, this.buyer)
    def invoice = new Invoice(100, this.date, this.itemType, this.seller, this.buyer)
    new Invoice(50, this.date, this.itemType, this.seller, this.buyer)

    when:
    invoice.cancel()
    def val = this.buyer.taxReturn(2018)

    then:
    val == 0.75f
  }
}

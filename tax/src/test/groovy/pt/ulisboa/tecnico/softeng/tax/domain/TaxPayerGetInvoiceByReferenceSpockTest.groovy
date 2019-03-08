package pt.ulisboa.tecnico.softeng.tax.domain

import org.joda.time.LocalDate

import spock.lang.*

import pt.ulisboa.tecnico.softeng.tax.domain.Buyer
import pt.ulisboa.tecnico.softeng.tax.domain.IRS
import pt.ulisboa.tecnico.softeng.tax.domain.Invoice
import pt.ulisboa.tecnico.softeng.tax.domain.ItemType
import pt.ulisboa.tecnico.softeng.tax.domain.Seller
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException

class TaxPayerGetInvoiceByReferenceSpockTest extends SpockRollbackTestAbstractClass {
  @Shared def SELLER_NIF = "123456789"
  @Shared def BUYER_NIF = "987654321"
  @Shared def FOOD = "FOOD"
  @Shared def VALUE = 16
  @Shared def TAX = 23
  @Shared def date = LocalDate.parse("2018-02-13")

  def seller
  def buyer
  def itemType
  def invoice
  def irs

  @Override
  def populate4Test() {
    this.irs = IRS.getIRSInstance()
    seller = new Seller(this.irs, SELLER_NIF, "José Vendido", "Somewhere")
    buyer = new Buyer(this.irs, BUYER_NIF, "Manuel Comprado", "Anywhere")
    itemType = new ItemType(this.irs, FOOD, TAX)
    invoice = new Invoice(VALUE, date, itemType, seller, buyer)
  }

  def "success"(){
   when:
   def invoice1 = seller.getInvoiceByReference(invoice.getReference())
   then:
   invoice == invoice1
  }

  def "nullReference"(){
    when:
    seller.getInvoiceByReference(null)

    then:
    thrown(TaxException)
  }

  def "emptyReference"(){
    when:
    seller.getInvoiceByReference("")

    then:
    thrown(TaxException)
    }

  def "doesNotExist"(){
    when:
    def invoice1 = seller.getInvoiceByReference(BUYER_NIF)
    then:
    invoice1 == null
  }



}

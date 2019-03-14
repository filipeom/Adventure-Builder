package pt.ulisboa.tecnico.softeng.tax.domain

import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.tax.domain.Buyer
import pt.ulisboa.tecnico.softeng.tax.domain.IRS
import pt.ulisboa.tecnico.softeng.tax.domain.Invoice
import pt.ulisboa.tecnico.softeng.tax.domain.ItemType
import pt.ulisboa.tecnico.softeng.tax.domain.Seller
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException

import spock.lang.*

class SellerToPaySpockTest extends SpockRollbackTestAbstractClass{
  @Shared def SELLER_NIF = "123456789"
  @Shared def BUYER_NIF = "987654321"
  @Shared def FOOD = "FOOD"
  @Shared def TAX = 10
  @Shared def date2018 = LocalDate.parse("2018-02-13")
  def date1969 = LocalDate.parse("1969-02-13")
  def date1970 = LocalDate.parse("1970-02-13")

  @Shared def buyer
  @Shared def seller
  def itemType
  def irs

  @Override
  def populate4Test(){
    irs = IRS.getIRSInstance()
    seller = new Seller(irs, SELLER_NIF, "José Vendido", "Somewhere")
    buyer = new Buyer(irs, BUYER_NIF, "Manuel Comprado", "Anywhere")
    itemType = new ItemType(irs, FOOD, TAX)
  }

  def 'success'(){
    when:
    new Invoice(100, date2018, itemType, seller, buyer)
    new Invoice(100, date2018, itemType, seller, buyer)
    new Invoice(50, date2018, itemType, seller, buyer)
    def value = seller.toPay(2018)

    then:
    value == 25.0f
  }

  def 'yearWithoutInvoices'(){
    when:
    new Invoice(100, date2018, itemType, seller, buyer)
    new Invoice(100, date2018, itemType, seller, buyer)
    new Invoice(50, date2018, itemType, seller, buyer)

    then:
    seller.toPay(2015) == 0.0f
  }

  def 'noInvoices'(){
    when:
    def value = seller.toPay(2018)

    then:
    value == 0.0f
  }

  def 'equal1970'(){
    when:
    new Invoice(100, date1970, itemType, seller, buyer)
    new Invoice(50, date1970, itemType, seller, buyer)
    def value = seller.toPay(1970)

    then:
    value == 15.0f
  }

  def 'before1970'(){
    when:
    new Invoice(100, date1969, itemType, seller, buyer)
    new Invoice(50, date1969, itemType, seller, buyer)
    def value = seller.toPay(1969)

    then:
    thrown(TaxException)

    and:
    value == null //Ao contrario do teste java, o value nestas condicoes fica a null
  }

  def 'ignoreCancelled'(){
    given:
    new Invoice(100, date2018, itemType, seller, buyer)
    def invoice = new Invoice(100, date2018, itemType, seller, buyer)
    new Invoice(50, date2018, itemType, seller, buyer)

    when:
    invoice.cancel()
    def value = seller.toPay(2018)

    then:
    value == 15.0f
  }

}

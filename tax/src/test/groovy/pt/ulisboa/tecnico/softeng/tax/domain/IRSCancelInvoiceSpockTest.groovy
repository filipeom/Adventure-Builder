package pt.ulisboa.tecnico.softeng.tax.domain

import pt.ulisboa.tecnico.softeng.tax.domain.Buyer
import pt.ulisboa.tecnico.softeng.tax.domain.IRS
import pt.ulisboa.tecnico.softeng.tax.domain.Invoice
import pt.ulisboa.tecnico.softeng.tax.domain.ItemType
import pt.ulisboa.tecnico.softeng.tax.domain.Seller
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException
import pt.ulisboa.tecnico.softeng.tax.services.local.TaxInterface

import org.joda.time.LocalDate

import spock.lang.*

class IRSCancelInvoiceSpockTest extends SpockRollbackTestAbstractClass{
  def SELLER_NIF = "123456789"
  def BUYER_NIF = "987654321"
  def FOOD = "FOOD"
  def VALUE = 16
  def date = LocalDate.parse("2018-02-13")

  def irs
  def reference
  def seller
  def invoice
  def buyer
  def itemType


  @Override
  def populate4Test(){
    irs = IRS.getIRSInstance()
    seller = new Seller(irs, SELLER_NIF, "José Vendido", "Somewhere")
    buyer = new Buyer(irs, BUYER_NIF, "Manuel Comprado", "Anywhere")
    itemType = new ItemType(irs, FOOD, VALUE)
    invoice = new Invoice(30.0, date, itemType, seller, buyer)
    reference = invoice.getReference()
  }

  def "success"(){
    when:
    TaxInterface.cancelInvoice(reference)
    then:
    invoice.isCancelled() == true
  }

  def "nullReference"(){
    when:
    TaxInterface.cancelInvoice(null)
    then:
    thrown(TaxException)
  }

  def "emptyReference"(){
    when:
    TaxInterface.cancelInvoice("")
    then:
    thrown(TaxException)
  }

  def "referenceDoesNotExist"(){
    when:
    TaxInterface.cancelInvoice("XXXXXXXX")
    then:
    thrown(TaxException)
  }

}

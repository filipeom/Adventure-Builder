package pt.ulisboa.tecnico.softeng.tax.domain

import org.joda.time.LocalDate

import java.util.Map

import spock.lang.*

class TaxPayerGetTaxesPerYearSpockTest extends SpockRollbackTestAbstractClass {
  @Shared def SELLER_NIF = "123456789"
  @Shared def BUYER_NIF = "987654321"
  def FOOD = "FOOD"
  def TAX  = 10
  def date = LocalDate.parse("2018-02-13")


  def irs
  def buyer
  def seller
  def itemType

  @Override
  def populate4Test(){
    irs = IRS.getIRSInstance()
    seller = new Seller(irs, SELLER_NIF, "José Vendido", "Somewhere")
    buyer = new Buyer(irs, BUYER_NIF, "Manuel Comprado", "Anywhere")
    itemType = new ItemType(irs, FOOD, TAX)
  }

  def "success"(){
      given:
      new Invoice(100, LocalDate.parse("2017-12-12"), itemType, seller, buyer)
      new Invoice(100, date, itemType, seller, buyer)
      new Invoice(100, date, itemType, seller, buyer)
      new Invoice(50, date, itemType, seller, buyer)


      expect:
      Map<Integer, Double> toPay = seller.getToPayPerYear()
      Map<Integer, Double> taxReturn = buyer.getTaxReturnPerYear()
      toPay.keySet().size() == 2
      toPay.get(2017) == 10.0d
      toPay.get(2018) == 25.0d
      taxReturn.keySet().size() == 2
      taxReturn.get(2017) == 0.5d
      taxReturn.get(2018) == 1.25d

  }

  def "successEmpty"(){
    given:
    Map<Integer, Double> toPay = seller.getToPayPerYear()
    Map<Integer, Double> taxReturn = buyer.getTaxReturnPerYear()

    expect:
    toPay.keySet().size()     == 0
    taxReturn.keySet().size() == 0
  }
}

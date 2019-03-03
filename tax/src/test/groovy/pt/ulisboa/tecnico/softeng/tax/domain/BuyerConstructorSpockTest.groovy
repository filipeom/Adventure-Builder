package pt.ulisboa.tecnico.softeng.tax.domain

import pt.ulisboa.tecnico.softeng.tax.domain.Buyer
import pt.ulisboa.tecnico.softeng.tax.domain.IRS
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException
import spock.lang.Shared
import spock.lang.Unroll

class BuyerConstructorSpockTest extends SpockRollbackTestAbstractClass {
  @Shared def ADDRESS = "Somewhere"
  @Shared def NAME = "José Vendido"
  @Shared def NIF = "123456789"
  
  def irs

  @Override
  def populate4Test() {
    this.irs = IRS.getIRSInstance()
  }

  def "success"() {
    when: "Creating a valid buyer"
    def buyer = new Buyer(this.irs, NIF, NAME, ADDRESS)

    then: 
    buyer.getNif()     == NIF
    buyer.getName()    == NAME
    buyer.getAddress() == ADDRESS

    buyer == IRS.getIRSInstance().getTaxPayerByNIF(NIF)
  }

  def "unique nif"() {
    given: "a buyer"
    def seller = new Buyer(this.irs, NIF, NAME, ADDRESS)

    when: "inserert a buyer with the same nif"
    new Buyer(this.irs, NIF, NAME, ADDRESS)

    then: "throws an exception"
    thrown(TaxException)
    
    and:
    seller == IRS.getIRSInstance().getTaxPayerByNIF(NIF)
  }

  @Unroll("Buyer: #nif, #name, #address")
  def "exceptions"() {
    when: "creating a buyer with invalid args"
    new Buyer(this.irs, nif, name, address)

    then: "throws an exception"
    thrown(TaxException)

    where:
    nif        | name | address
    null       | NAME | ADDRESS
    ""         | NAME | ADDRESS
    "12345678" | NAME | ADDRESS
    NIF        | null | ADDRESS
    NIF        | ""   | ADDRESS
    NIF        | NAME | null
    NIF        | NAME | ""
  }
}

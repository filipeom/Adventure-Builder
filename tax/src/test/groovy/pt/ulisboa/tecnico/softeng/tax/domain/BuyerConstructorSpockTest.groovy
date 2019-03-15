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
    irs = IRS.getIRSInstance()
  }

  def "success"() {
    when: 
    def buyer = new Buyer(irs, NIF, NAME, ADDRESS)

    then: 
    with(buyer) {
      getNif() == NIF
      getName() == NAME
      getAddress() == ADDRESS
    }
    buyer == IRS.getIRSInstance().getTaxPayerByNIF(NIF)
  }

  def "unique nif"() {
    given: 
    def seller = new Buyer(irs, NIF, NAME, ADDRESS)

    when: 
    new Buyer(irs, NIF, NAME, ADDRESS)

    then:
    thrown(TaxException)
    
    and:
    seller == IRS.getIRSInstance().getTaxPayerByNIF(NIF)
  }

  @Unroll("Buyer: #nif, #name, #address")
  def "exceptions"() {
    when: 
    new Buyer(irs, nif, name, address)

    then:
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

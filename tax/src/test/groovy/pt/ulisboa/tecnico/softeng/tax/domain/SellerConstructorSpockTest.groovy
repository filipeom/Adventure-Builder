 package pt.ulisboa.tecnico.softeng.tax.domain

import pt.ulisboa.tecnico.softeng.tax.domain.IRS
import pt.ulisboa.tecnico.softeng.tax.domain.Seller
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException

import spock.lang.Shared
import spock.lang.Unroll

class SellerConstructorSpockTest extends SpockRollbackTestAbstractClass{
  @Shared def ADDRESS = "Somewhere"
  @Shared def NAME = "José Vendido"
  @Shared def NIF = "123456789"

  @Shared def irs

  @Override
  def populate4Test() {
    this.irs = IRS.getIRSInstance()
  }

  def "success"(){
    when:
    def seller = new Seller(this.irs, NIF, NAME, ADDRESS)

    then:
    seller.getNif() == NIF
    seller.getName() == NAME
    seller.getAddress() == ADDRESS
    seller == IRS.getIRSInstance().getTaxPayerByNIF(NIF)
  }

  def "uniqueNIF"(){
    given:
    def seller = new Seller(this.irs, NIF, NAME, ADDRESS)

    when:
    new Seller(this.irs, NIF, NAME, ADDRESS)

    then:
    thrown(TaxException)

    and:
    seller == IRS.getIRSInstance().getTaxPayerByNIF(NIF)
  }

  @Unroll("Seller: #nif, #name, #address")
  def "exceptions"(){
    when:
    new Seller(this.irs, nif, name, address)

    then:
    thrown(TaxException)

    where:
    nif   | name | address
    null  | NAME | ADDRESS
    ""    | NAME | ADDRESS
    NIF   | null | ADDRESS
    NIF   | ""   | ADDRESS
    NIF   | NAME | null
    NIF   | NAME | ""
  }



}

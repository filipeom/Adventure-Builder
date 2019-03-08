package pt.ulisboa.tecnico.softeng.tax.domain

import pt.ulisboa.tecnico.softeng.tax.domain.IRS
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException

import spock.lang.*

class ItemTypeConstructorSpockTest extends SpockRollbackTestAbstractClass {
  @Shared def CAR = "CAR"
  @Shared def TAX = 23
  def irs

  @Override
  def populate4Test() {
    this.irs = IRS.getIRSInstance()
  }

  def 'success'() {
    when:
    def itemType = new ItemType(this.irs, CAR, TAX)

    then:
    itemType.getName() == CAR
    itemType.getTax() == TAX
    IRS.getIRSInstance().getItemTypeByName(CAR) != null
    itemType == IRS.getIRSInstance().getItemTypeByName(CAR)
  }

  def 'uniqueName'(){
    given:
    def itemType = new ItemType(this.irs, CAR, TAX)

    when:
    def itemType2 = new ItemType(this.irs, CAR, TAX)

    then:
    thrown(TaxException)

    and:
    itemType == IRS.getIRSInstance().getItemTypeByName(CAR)
  }

  def "zeroTax"(){
    when:
    def itemType = new ItemType(this.irs, CAR, 0)

    then:
    itemType.getTax() == 0
  }

  @Unroll("ItemType: #name, #tax")
  def 'exceptions'(){
    when:
    new ItemType(this.irs, name, tax)

    then:
    thrown(TaxException)

    where:
    name | tax
    null | TAX
    ""   | TAX
    CAR  | -34


  }



}

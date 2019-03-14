package pt.ulisboa.tecnico.softeng.tax.domain

import org.joda.time.LocalDate
import org.joda.time.DateTime

import pt.ulisboa.tecnico.softeng.tax.exception.TaxException
import pt.ulisboa.tecnico.softeng.tax.services.local.TaxInterface
import pt.ulisboa.tecnico.softeng.tax.services.remote.dataobjects.RestInvoiceData

import spock.lang.*

class TaxInterfaceSubmitInvoiceSpockTest extends SpockRollbackTestAbstractClass {
  @Shared def REFERENCE = "123456789"
  @Shared def SELLER_NIF = "123456789"
  @Shared def BUYER_NIF = "987654321"
  @Shared def FOOD = "FOOD"
  @Shared def VALUE = 160
  def TAX = 16
  @Shared def date = new LocalDate(2018, 02, 13)
  @Shared def time = new DateTime(2018, 02, 13, 10, 10)

  def irs

  def populate4Test(){
    irs = IRS.getIRSInstance()
    new Seller(irs, SELLER_NIF, "José Vendido", "Somewhere")
    new Buyer(irs, BUYER_NIF, "Manuel Comprado", "Anywhere")
    new ItemType(irs, FOOD, TAX)
  }

  def "success"(){
    given:
    def invoiceData = new RestInvoiceData(REFERENCE, SELLER_NIF, BUYER_NIF, FOOD, VALUE, date,
				time)


    when:
    def invoiceReference = TaxInterface.submitInvoice(invoiceData)

    then:
    def invoice = irs.getTaxPayerByNIF(SELLER_NIF).getInvoiceByReference(invoiceReference)
    invoice.getReference() == invoiceReference
    invoice.getSeller().getNif() == SELLER_NIF
    invoice.getBuyer().getNif() == BUYER_NIF
    invoice.getItemType().getName() == FOOD
    invoice.getValue() == VALUE
    invoice.getDate() == date
  }

  def "submitTwice"(){
    when:
    def invoiceData = new RestInvoiceData(REFERENCE, SELLER_NIF, BUYER_NIF, FOOD, VALUE, date,
				time)
    def invoiceReference = TaxInterface.submitInvoice(invoiceData)
    def secondInvoiceReference = TaxInterface.submitInvoice(invoiceData)

    then:
    invoiceReference == secondInvoiceReference
  }


  @Unroll("InvoiceData: #reference, #seller_nif, #buyer_nif, #itemType, #value, #date_u, #time_u")
  def "exceptions"(){
    when:
    def invoiceData = new RestInvoiceData(reference, seller_nif, buyer_nif, itemType, value, date_u, time_u)
    TaxInterface.submitInvoice(invoiceData)

    then:
    thrown(TaxException)

    where:

    reference | seller_nif | buyer_nif | itemType | value | date_u                      | time_u
    REFERENCE | null       | BUYER_NIF | FOOD     | VALUE | date                        | time
    REFERENCE | ""         | BUYER_NIF | FOOD     | VALUE | date                        | time
    REFERENCE | SELLER_NIF | null      | FOOD     | VALUE | date                        | time
    REFERENCE | SELLER_NIF | ""        | FOOD     | VALUE | date                        | time
    REFERENCE | SELLER_NIF | BUYER_NIF | null     | VALUE | date                        | time
    REFERENCE | SELLER_NIF | BUYER_NIF | ""       | VALUE | date                        | time
    REFERENCE | SELLER_NIF | BUYER_NIF | FOOD     | 0.0d  | date                        | time
    REFERENCE | SELLER_NIF | BUYER_NIF | FOOD     |-23.7d | date                        | time
    REFERENCE | SELLER_NIF | BUYER_NIF | FOOD     | VALUE | null                        | time
    REFERENCE | SELLER_NIF | BUYER_NIF | FOOD     | VALUE | date                        | null
    REFERENCE | SELLER_NIF | BUYER_NIF | FOOD     | VALUE | new LocalDate(1969, 12, 31) | new DateTime(1969, 12, 31, 10, 10)
    null      | SELLER_NIF | BUYER_NIF | FOOD     | VALUE | new LocalDate(1970, 01, 01) | new DateTime(1970, 01, 01, 10, 10)


  }

  def "equal1970"(){
    when:
    def invoiceData = new RestInvoiceData(REFERENCE, SELLER_NIF, BUYER_NIF, FOOD, VALUE, date,
        time)
    then:
    def invoiceReference = TaxInterface.submitInvoice(invoiceData)
  }

}

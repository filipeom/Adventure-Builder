 package pt.ulisboa.tecnico.softeng.tax.domain

 import pt.ulisboa.tecnico.softeng.tax.domain.Buyer
 import pt.ulisboa.tecnico.softeng.tax.domain.IRS
 import pt.ulisboa.tecnico.softeng.tax.domain.Seller
 import pt.ulisboa.tecnico.softeng.tax.domain.TaxPayer
 import spock.lang.Unroll

 class IRSGetTaxPayerByNIFSpockTest extends SpockRollbackTestAbstractClass {
   def SELLER_NIF = "123456789"
   def BUYER_NIF = "987654321"

   def irs

   @Override
   def populate4Test() {
     this.irs = IRS.getIRSInstance()
     new Seller(this.irs, SELLER_NIF, "José Vendido", "Somewhere")
     new Buyer(this.irs, BUYER_NIF, "Manuel Comprado", "Anywhere")
   }

   def 'success buyer'() {
     when:
     def taxPayer = this.irs.getTaxPayerByNIF(BUYER_NIF)

     then:
     taxPayer != null
     taxPayer.getNif() == BUYER_NIF
   }

   def 'success seller'() {
     when:
     def taxPayer = this.irs.getTaxPayerByNIF(SELLER_NIF)

     then:
     taxPayer != null
     taxPayer.getNif() == SELLER_NIF
   }

   @Unroll('getTaxPayerByNif: #label')
   def 'exceptions'() {
     when:
     def taxPayer = this.irs.getTaxPayerByNIF(nif)

     then:
     taxPayer == null
     
     where:
     label          | nif
     'null NIF'     | null
     'empty NIF'    | ""
     'doesnt exist' | "122456789"

   }
 }

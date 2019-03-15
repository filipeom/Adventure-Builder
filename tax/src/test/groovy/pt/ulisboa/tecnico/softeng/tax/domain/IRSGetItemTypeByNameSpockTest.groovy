 package pt.ulisboa.tecnico.softeng.tax.domain

 import pt.ulisboa.tecnico.softeng.tax.domain.IRS
 import pt.ulisboa.tecnico.softeng.tax.domain.ItemType
 import spock.lang.Unroll

 class IRSGetItemByTypeNameSpockTest extends SpockRollbackTestAbstractClass {
   def FOOD = "FOOD"
   def VALUE = 16

   def irs

   @Override
   def populate4Test() {
     irs = IRS.getIRSInstance()
     new ItemType(irs, FOOD, VALUE)
   }

   def 'success'() {
     when:
     def itemType = irs.getItemTypeByName(FOOD)

     then:
     with(itemType) {
       getName() != null
       getName() == FOOD
     }
   }

   @Unroll('getItemTypeByName: #label')
   def 'exceptions'() {
     when:
     def itemType = irs.getItemTypeByName(arg)

     then:
     itemType == null

     where:
     label         | arg
     'null name'   | null
     'empty name'  | ""
     'doesnt exist'| "CAR"

   }
 }

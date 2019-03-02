 package pt.ulisboa.tecnico.softeng.tax.domain

 import pt.ulisboa.tecnico.softeng.tax.domain.IRS
 import pt.ulisboa.tecnico.softeng.tax.domain.ItemType

 class IRSGetItemByTypeNameSpockTest extends SpockRollbackTestAbstractClass {
   def FOOD = "FOOD"
   def VALUE = 16

   def irs

   @Override
   def populate4Test() {
     this.irs = IRS.getIRSInstance()
     new ItemType(this.irs, FOOD, VALUE)
   }

   def 'success'() {
     given:
     def itemType = this.irs.getItemTypeByName(FOOD)

     expect:
     itemType.getName() != null
     itemType.getName() == FOOD
   }

   def 'null name'() {
     given:
     def itemType = this.irs.getItemTypeByName(null)

     expect:
     itemType == null
   }
   def 'empty name'() {
     given:
     def itemType = this.irs.getItemTypeByName("")

     expect:
     itemType == null
   }
   def 'name does not exist'() {
     given:
     def itemType = this.irs.getItemTypeByName("CAR")

     expect:
     itemType == null
   }
 }

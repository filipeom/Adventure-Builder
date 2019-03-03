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
     when:
     def itemType = this.irs.getItemTypeByName(FOOD)

     then:
     itemType.getName() != null
     itemType.getName() == FOOD
   }

   def 'null name'() {
     when:
     def itemType = this.irs.getItemTypeByName(null)

     then:
     itemType == null
   }
   def 'empty name'() {
     when:
     def itemType = this.irs.getItemTypeByName("")

     then:
     itemType == null
   }
   def 'name does not exist'() {
     when:
     def itemType = this.irs.getItemTypeByName("CAR")

     then:
     itemType == null
   }
 }

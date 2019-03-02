package pt.ulisboa.tecnico.softeng.activity.domain


class ActivityMatchAgeMethodTest extends SpockRollbackTestAbstractClass {
    def MIN_AGE = 25
    def MAX_AGE = 80
    def CAPACITY = 30

    def activity
    
    @Override
    def populate4Test() {
       def provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", "IBAN")
       activity = new Activity(provider, "Bush Walking", MIN_AGE, MAX_AGE, CAPACITY)
    }

    def 'sucess'() {
        activity.matchAge((MAX_AGE - MIN_AGE) / 2) == true
    }

    def 'sucessEqualMinAge'() {
        activity.matchAge(MIN_AGE) == true
    }
    
    def 'lessThanMinAge'() {
        activity.matchAge(MIN_AGE - 1) == false
    } 

    def 'sucessEqualMaxAge'() {
        activity.matchAge(MAX_AGE) == false
    }

    def 'greaterThanMaxAge'() {
        activity.matchAge(MAX_AGE + 1) == false
    }

}

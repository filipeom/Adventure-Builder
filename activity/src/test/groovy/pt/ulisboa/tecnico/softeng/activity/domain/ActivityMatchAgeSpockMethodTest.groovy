package pt.ulisboa.tecnico.softeng.activity.domain


class ActivityMatchAgeSpockMethodTest extends SpockRollbackTestAbstractClass {
    def MIN_AGE = 25
    def MAX_AGE = 80
    def CAPACITY = 30
    def activity
    
    @Override
    def populate4Test() {
        def provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", "IBAN")
        activity = new Activity(provider, "Bush Walking", MIN_AGE, MAX_AGE, CAPACITY)
    }

    def 'success'() {
        expect:
        activity.matchAge((MAX_AGE - MIN_AGE).intdiv(2)) == true
    }

    def 'successEqualMinAge'() {
        expect:
        activity.matchAge(MIN_AGE) == true
    }

    def 'lessThanMinAge'() {
        expect:
        activity.matchAge(MIN_AGE - 1) == false
    }

    def 'successEqualMaxAge'() {
        expect:
        activity.matchAge(MAX_AGE) == true
    }

    def 'greaterThanMaxAge'() {
        expect:
        activity.matchAge(MAX_AGE + 1) == false
    }

}

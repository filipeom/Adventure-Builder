package pt.ulisboa.tecnico.softeng.activity.domain

import spock.lang.Shared
import spock.lang.Unroll

class ActivityMatchAgeSpockMethodTest extends SpockRollbackTestAbstractClass {
    @Shared def MIN_AGE = 25
    @Shared def MAX_AGE = 80
    def CAPACITY = 30
    def activity
    
    @Override
    def populate4Test() {
        def provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", "IBAN")
        activity = new Activity(provider, "Bush Walking", MIN_AGE, MAX_AGE, CAPACITY)
    }

    @Unroll('matchAge; #age || #res')
    def 'matchAge'() {
    	expect:
	activity.matchAge(age) == res

	where:
	age 			      || res
	(MAX_AGE - MIN_AGE).intdiv(2) || true
	MIN_AGE                       || true
	MIN_AGE - 1                   || false
	MAX_AGE                       || true
	MAX_AGE + 1                   || false
    }
}

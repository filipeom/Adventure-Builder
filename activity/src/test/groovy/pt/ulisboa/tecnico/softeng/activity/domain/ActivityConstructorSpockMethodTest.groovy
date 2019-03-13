package pt.ulisboa.tecnico.softeng.activity.domain

import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException

import spock.lang.Unroll
import spock.lang.Shared


class ActivityConstructorSpockMethodTest extends SpockRollbackTestAbstractClass {
    @Shared def IBAN = "IBAN"    
    @Shared def NIF = "NIF"
    @Shared def PROVIDER_NAME = "Bush Walking"
    @Shared def MIN_AGE = 25
    @Shared def MAX_AGE = 50
    @Shared def CAPACITY = 30
    @Shared def provider

    @Override
    def populate4Test() {
        provider = new ActivityProvider("XtremX", "ExtremeAdventure", NIF, IBAN)
    }


    @Unroll('success: #min_age, #max_age, #capacity')
    def 'sucess'() {
    	when:
        def activity = new Activity(provider, PROVIDER_NAME, min_age, max_age, capacity)    

	then:
        activity.getCode().startsWith(provider.getCode()) == true
        activity.getCode().length() > ActivityProvider.CODE_SIZE == true
        activity.getName() == "Bush Walking"
        activity.getMinAge() == min_age
        activity.getMaxAge() == max_age
        activity.getCapacity() == capacity
        activity.getActivityOfferSet().size() == 0
        provider.getActivitySet().size() == 1

	where:
	min_age | max_age | capacity
	MIN_AGE | MAX_AGE | CAPACITY
	18      | MAX_AGE | CAPACITY
	MIN_AGE | 99      | CAPACITY
	MIN_AGE | MIN_AGE | CAPACITY
	MIN_AGE | MAX_AGE | 1
    }

    @Unroll('Activity: #prov, #name, #min_age, #max_age, #capac')
    def 'exceptions'() {
        when:
        new Activity(prov, name, min_age, max_age, capac)
        
        then:
        thrown(ActivityException)

        where:
        prov        | name          | min_age      | max_age | capac
        null        | PROVIDER_NAME | MIN_AGE      | MAX_AGE | CAPACITY
        provider    | null          | MIN_AGE      | MAX_AGE | CAPACITY
        provider    | '    '        | MIN_AGE      | MAX_AGE | CAPACITY
        provider    | PROVIDER_NAME | 17           | MAX_AGE | CAPACITY
        provider    | PROVIDER_NAME | MIN_AGE      | 100     | CAPACITY
        provider    | PROVIDER_NAME | MAX_AGE + 10 | MAX_AGE | CAPACITY
        provider    | PROVIDER_NAME | MAX_AGE + 1  | MAX_AGE | CAPACITY
        provider    | PROVIDER_NAME | MIN_AGE      | MAX_AGE | 0
    }

}

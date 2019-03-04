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

    def 'success'() {
        when:
        def activity = new Activity(provider, PROVIDER_NAME, MIN_AGE, MAX_AGE, CAPACITY)    

        then:
        activity.getCode().startsWith(provider.getCode()) == true
        activity.getCode().length() > ActivityProvider.CODE_SIZE == true
        activity.getName() == "Bush Walking"
        activity.getMinAge() == MIN_AGE
        activity.getMaxAge() == MAX_AGE
        activity.getCapacity() == CAPACITY
        activity.getActivityOfferSet().size() == 0
        provider.getActivitySet().size() == 1
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

    def 'successMinAgeEqual18'() {
        when:
        def activity = new Activity(provider, PROVIDER_NAME, 18, MAX_AGE, CAPACITY)

        then:
        activity.getCode().startsWith(provider.getCode()) == true
        activity.getCode().length() > ActivityProvider.CODE_SIZE == true
        activity.getName() == "Bush Walking"
        activity.getMinAge() == 18
        activity.getMaxAge() == MAX_AGE
        activity.getCapacity() == CAPACITY
        activity.getActivityOfferSet().size() == 0
        provider.getActivitySet().size() == 1
    }

    def 'successMaxAge99'() {
        when:
        def activity = new Activity(provider, PROVIDER_NAME, MIN_AGE, 99, CAPACITY)

        then:
        activity.getCode().startsWith(provider.getCode()) == true
        activity.getCode().length() > ActivityProvider.CODE_SIZE == true
        activity.getName() == "Bush Walking"
        activity.getMinAge() == MIN_AGE
        activity.getMaxAge() == 99
        activity.getCapacity() == CAPACITY
        activity.getActivityOfferSet().size() == 0
        provider.getActivitySet().size() == 1
    }

    def 'successMinAgeEqualMaxAge'() {
        when:
        def activity = new Activity(provider, PROVIDER_NAME, MIN_AGE, MIN_AGE, CAPACITY)

        then:
        activity.getCode().startsWith(provider.getCode()) == true
        activity.getCode().length() > ActivityProvider.CODE_SIZE == true
        activity.getName() == "Bush Walking"
        activity.getMinAge() == MIN_AGE
        activity.getMaxAge() == MIN_AGE
        activity.getCapacity() == CAPACITY
        activity.getActivityOfferSet().size() == 0
        provider.getActivitySet().size() == 1
    }

    def 'successCapacityOne'() {
        when:
        def activity = new Activity(provider, PROVIDER_NAME, MIN_AGE, MAX_AGE, 1)

        then:
        activity.getCode().startsWith(provider.getCode()) == true
        activity.getCode().length() > ActivityProvider.CODE_SIZE == true
        activity.getName() == "Bush Walking"
        activity.getMinAge() == MIN_AGE
        activity.getMaxAge() == MAX_AGE
        activity.getCapacity() == 1
        activity.getActivityOfferSet().size() == 0
        provider.getActivitySet().size() == 1
    }
}

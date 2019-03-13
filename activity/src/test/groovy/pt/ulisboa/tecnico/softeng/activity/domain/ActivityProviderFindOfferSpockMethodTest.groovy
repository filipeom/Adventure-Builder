package pt.ulisboa.tecnico.softeng.activity.domain

import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException
import spock.lang.Unroll
import spock.lang.Shared


class ActivityProviderFindOfferSpockMethodTest extends SpockRollbackTestAbstractClass {
    def CAPACITY = 25
    @Shared def MAX_AGE = 80
    @Shared def MIN_AGE = 25
    @Shared def AGE = 40
    @Shared def begin = new LocalDate(2016, 12, 19)
    @Shared def end = new LocalDate(2016, 12, 21)

    def provider
    def activity
    def offer

    @Override
    def populate4Test() {
        provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", "IBAN")    
        activity = new Activity(provider, "Bush Walking", MIN_AGE, MAX_AGE, CAPACITY)

        offer = new ActivityOffer(activity, begin, end, 30)
    }

    @Unroll('findOffer: #_age')
    def 'checkOfferSize'() {
	when:
	def offers = provider.findOffer(begin, end, _age)

	then:
	offers.size() == 1
	offers.contains(offer) == true

	where:
	_age    | _
	AGE     | _
	MIN_AGE | _
	MAX_AGE | _
    }

    @Unroll('findOfferExceptions: #_begin, #_end')
    def 'findOfferExceptions'() {
	when:
	provider.findOffer(_begin, _end, AGE)

	then:
	thrown(ActivityException)

	where:
	_begin | _end
	null   | end
	begin  | null
    }

    @Unroll('offersEmpty: #_age')
    def 'offersEmpty'() {
    	when:
	def offers = provider.findOffer(begin, end, _age)

	then:
	offers.isEmpty() == true

	where:
	_age | _
	MIN_AGE - 1 | _
	MAX_AGE + 1 | _
    }

    def 'emptyActivitySet'() {
        when:
        def otherProvider = new ActivityProvider("Xtrems", "Adventure", "NIF2", "IBAN")
        def offers = otherProvider.findOffer(begin, end, AGE)

        then:
        offers.isEmpty() == true
    }

    def 'emptyActivityOfferSet'() {
        when:
        def otherProvider = new ActivityProvider("Xtrems", "Adventure", "NIF2", "IBAN")
        new Activity(otherProvider, "Bush Walking", 18, 80, 25)
        def offers = otherProvider.findOffer(begin, end, AGE)

        then:
        offers.isEmpty() == true
    }


    @Unroll('matchActivityOffers2: #_end, #_result')
    def 'matchActivityOffers1'() {
    	when:
	new ActivityOffer(activity, begin, _end, 30)
	def offers = provider.findOffer(begin, end, AGE)

	then:
	offers.size == _result

	where:
	_end 		| _result
	end 		| 2
	end.plusDays(1) | 1
    }

    def 'oneMatchActivityOfferAndOtherNoCapacity'() {
        when:
        def otherActivity = new Activity(provider, "Bush Walking", MIN_AGE, MAX_AGE, 1)
        def otherActivityOffer = new ActivityOffer(otherActivity, begin, end, 30)
        new Booking(provider, otherActivityOffer, "123456789", "IBAN")
        def offers = provider.findOffer(begin, end, AGE)

        then:
        offers.size() == 1
    }
}

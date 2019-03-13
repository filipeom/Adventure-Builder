package pt.ulisboa.tecnico.softeng.activity.domain

import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException
import spock.lang.Unroll
import spock.lang.Shared


class ActivityOfferMatchDateSpockMethodTest extends SpockRollbackTestAbstractClass {
    @Shared def begin = new LocalDate(2016, 12, 19)
    @Shared def end = new LocalDate(2016, 12, 23)

    def offer
    
    @Override
    def populate4Test() {
        def provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", "IBAN")
        def activity = new Activity(provider, "Bush Walking", 18, 80, 3)

        offer = new ActivityOffer(activity, begin, end, 30)
    }

    def 'sucess'() {
        expect:
        offer.matchDate(begin, end) == true    
    }

    @Unroll('matchDate: #_begin, #_end')
    def 'exception'() {
    	when:
	offer.matchDate(_begin, _end)

	then:
	thrown(ActivityException)

	where:
	_begin | _end
	null   | end
	begin  | null
    }
    
    @Unroll('AssertMatchDate: #_begin, #_end')
    def 'assertConditions'() {
    	expect:
	offer.matchDate(_begin, _end) == false

	where:
	_begin             | _end
	begin.plusDays(1)  | end
	begin.minusDays(1) | end
	begin              | end.plusDays(1)
	begin              | end.minusDays(1)
    }
}

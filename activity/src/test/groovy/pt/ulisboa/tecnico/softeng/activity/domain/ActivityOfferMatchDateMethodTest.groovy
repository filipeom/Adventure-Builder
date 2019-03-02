package pt.ulisboa.tecnico.softeng.activity.domain

import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException


class ActivityOfferMatchDateMethodTest extends SpockRollbackTestAbstractClass {
    def begin = new LocalDate(2016, 12, 19)
    def end = new LocalDate(2016, 12, 23)

    def offer
    
    @Override
    def populate4Test() {
        def provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", "IBAN")
        def activity = new Activity(provider, "Bush Walking", 18, 80, 3)

        offer = new ActivityOffer(activity, begin, end, 30)
    }

    def 'sucess'() {
        offer.matchDate(begin, end) == true    
    }

    def 'nullBeginDate'() {
        when:
        offer.matchDate(null, end)
        then:
        thrown(ActivityException)
    }
    
    def 'nullEndDate'() {
        when:
        offer.matchDate(begin, null)
        then:
        thrown(ActivityException)
    }
    
    def 'beginPlusOne'() {
        offer.matchDate(begin.plusDays(1), end) == false    
    }

    def 'beginMinusOne'() {
        offer.matchDate(begin.minusDays(1), end) == false
    }

    def 'endPlusOne'() {
        offer.matchDate(begin, end.plusDays(1)) == false    
    }

    def 'endMinusOne'() {
        offer.matchDate(begin, end.minusDays(1)) == false
    }

}

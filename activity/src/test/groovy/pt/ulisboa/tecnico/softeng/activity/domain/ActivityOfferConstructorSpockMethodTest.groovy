package pt.ulisboa.tecnico.softeng.activity.domain

import org.joda.time.LocalDate
import spock.lang.Shared
import spock.lang.Unroll

import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException

class ActivityOfferConstructorSpockMethodTest extends SpockRollbackTestAbstractClass {
    def CAPACITY = 25
    def MAX_AGE = 50
    def MIN_AGE = 25
    @Shared def begin = new LocalDate(2016, 12, 19)
    @Shared def end = new LocalDate(2016, 12, 21)
    @Shared def activity

    @Override
    def populate4Test() {
        def provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", "IBAN")
        activity = new Activity(provider, "Bush Walking", MIN_AGE, MAX_AGE, CAPACITY)
    }

    def 'success'() {
        when:
        def offer = new ActivityOffer(activity, begin, end, 30)

        then:
        offer.getBegin() == begin
        offer.getEnd() == end
        activity.getActivityOfferSet().size() == 1
        offer.getNumberActiveOfBookings() == 0
        offer.getPrice() == 30 //triple comparation
    }
    
    @Unroll('ActivityOffer: #_activity, #_begin, #_end, #_capc')
    def 'exceptions'() {
        when:
        new ActivityOffer(_activity, _begin, _end, _capac)

        then:
        thrown(ActivityException)

        where:
        _activity | _begin | _end               | _capac
        null      | begin  | end                | 30
        activity  | null   | end                | 30
        activity  | begin  | null               | 30
        activity  | begin  | begin.minusDays(1) | 30
        activity  | begin  | end                | 0
    }

    def 'successBeginDateEqualEndDate'() {
        when:
        def offer = new ActivityOffer(activity, begin, begin , 30)

        then:
        offer.getBegin() == begin
        offer.getEnd() == begin
        activity.getActivityOfferSet().size() == 1
        offer.getNumberActiveOfBookings() == 0
    }
    
} 

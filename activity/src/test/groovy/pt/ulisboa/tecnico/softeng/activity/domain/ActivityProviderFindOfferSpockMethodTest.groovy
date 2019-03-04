package pt.ulisboa.tecnico.softeng.activity.domain

import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException


class ActivityProviderFindOfferSpockMethodTest extends SpockRollbackTestAbstractClass {
    def MIN_AGE = 25
    def MAX_AGE = 80
    def CAPACITY = 25
    def AGE = 40
    def begin = new LocalDate(2016, 12, 19)
    def end = new LocalDate(2016, 12, 21)

    def provider
    def activity
    def offer

    @Override
    def populate4Test() {
        provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", "IBAN")    
        activity = new Activity(provider, "Bush Walking", MIN_AGE, MAX_AGE, CAPACITY)

        offer = new ActivityOffer(activity, begin, end, 30)
    }

    def 'success'() {
        when:
        def offers = provider.findOffer(begin, end, AGE)

        then:
        offers.size() == 1
        offers.contains(offer) == true
    }

    def 'nullBeginDate'() {
       when:
       provider.findOffer(null, end, AGE)

       then:
       thrown(ActivityException)
    }

    def 'nullEndDate'() {
        when:
        provider.findOffer(begin, null, AGE)

        then:
        thrown(ActivityException)
    }

    def 'successAgeEqualMin'() {
        when:
        def offers = provider.findOffer(begin, end, MIN_AGE)

        then:
        offers.size() == 1
        offers.contains(offer) == true
    }

    def 'AgeMinusOneThanMinimal'() {
        when:
        def offers = provider.findOffer(begin, end, MIN_AGE - 1)

        then:
        offers.isEmpty() == true
    }

    def 'successAgeEqualMax'() {
        when:
        def offers = provider.findOffer(begin, end, MAX_AGE)

        then:
        offers.size() == 1
        offers.contains(offer) == true
    }

    def 'AgePlusOneThanMinimal'() {
        when:
        def offers = provider.findOffer(begin, end, MAX_AGE + 1)

        then:
        offers.isEmpty() == true
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


    def 'twoMatchActivityOffers'() {
        when:
        new ActivityOffer(activity, begin, end, 30)
        def offers = provider.findOffer(begin, end, AGE)

        then:
        offers.size() == 2
    }

    def 'oneMatchActivityOfferAndOneNotMatch'() {
        when:
        new ActivityOffer(activity, begin, end.plusDays(1), 30)
        def offers = provider.findOffer(begin, end, AGE)

        then:
        offers.size() == 1
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

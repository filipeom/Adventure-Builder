package pt.ulisboa.tecnico.softeng.activity.domain

import org.joda.time.LocalDate
import spock.lang.Unroll
import spock.lang.Shared

import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException

class BookingConstructorSpockMethodTest extends SpockRollbackTestAbstractClass {
    @Shared def provider
    @Shared def offer
    @Shared def IBAN = "IBAN"
    @Shared def NIF = "123456789"
    def AMOUNT = 30

    def populate4Test() {
        provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", IBAN)
        def activity = new Activity(provider, "Bush Walking", 18, 80, 3)

        def begin = new LocalDate(2016, 12, 19)
        def end = new LocalDate(2016, 12, 21)
        offer = new ActivityOffer(activity, begin, end, AMOUNT)
    }

    def 'success'() {
        when:
        def booking = new Booking(provider, offer, NIF, IBAN)

        then:
        booking.getReference().startsWith(provider.getCode()) == true
        booking.getReference().length() > ActivityProvider.CODE_SIZE
        offer.getNumberActiveOfBookings() == 1
        booking.getBuyerNif() == NIF
        booking.getIban() == IBAN
        booking.getAmount() == AMOUNT
    }

    @Unroll('Booking: #_provider, #_offer, #_nif, #_iban')
    def 'exceptions'() {
        when:
        new Booking(_provider, _offer, _nif, _iban)

        then:
        thrown(ActivityException)

        where:
        _provider | _offer | _nif | _iban
        null      | offer  | NIF  | IBAN
        provider  | null   | NIF  | IBAN
        null      | offer  | null | IBAN
        null      | offer  | NIF  | null
    }

    def 'emptyIBAN'() {
        when:
        new Booking(provider, null, NIF, "     ")

        then:
        thrown(ActivityException)
    }

    def 'nullNIF'() {
        when:
        new Booking(provider, offer, "     ", IBAN)

        then:
        thrown(ActivityException)
    }

    def 'bookingEqualCapacity'() {
        given:
        new Booking(provider, offer, NIF, IBAN)
        new Booking(provider, offer, NIF, IBAN)
        new Booking(provider, offer, NIF, IBAN)

        when:
        new Booking(provider, offer, NIF, IBAN)

        then:
        thrown(ActivityException)

        and:
        offer.getNumberActiveOfBookings() == 3
    }

    def 'bookingEqualCapacityButHasCancelled'() {
        given:
        new Booking(provider, offer, NIF, IBAN)
        new Booking(provider, offer, NIF, IBAN)
        def booking = new Booking(provider, offer, NIF, IBAN)

        when:
        booking.cancel()
        new Booking(provider, offer, NIF, IBAN)

        then:
        offer.getNumberActiveOfBookings() == 3
    }
} 

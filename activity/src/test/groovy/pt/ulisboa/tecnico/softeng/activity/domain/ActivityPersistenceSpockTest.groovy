package pt.ulisboa.tecnico.softeng.activity.domain

import org.joda.time.LocalDate

import pt.ist.fenixframework.FenixFramework

class ActivityPersistenceSpockTest extends SpockPersistenceTestAbstractClass {
    def ADVENTURE_ID = "AdventureId"
    def ACTIVITY_NAME = "Activity_Name"
    def PROVIDER_NAME = "Wicket"
    def PROVIDER_CODE = "A12345"
    def IBAN = "IBAN"
    def NIF = "NIF"
    def BUYER_IBAN = "IBAN2"
    def BUYER_NIF = "NIF2"
    def CAPACITY = 25
    def AMOUNT = 30.0

    def begin = new LocalDate(2017, 04, 01)
    def end = new LocalDate(2017, 04, 15)

    @Override
    def whenCreateInDatabase() {
        def activityProvider = new ActivityProvider(PROVIDER_CODE, PROVIDER_NAME, NIF, IBAN)
        def activity = new Activity(activityProvider, ACTIVITY_NAME, 18, 65, CAPACITY)
        def offer = new ActivityOffer(activity, begin, end, AMOUNT)
        offer.book(activityProvider, offer, 54, BUYER_NIF, BUYER_IBAN, ADVENTURE_ID)
    }

    @Override
    def thenAssert() {
        assert FenixFramework.getDomainRoot().getActivityProviderSet().size() == 1

        def providers = new ArrayList<>(FenixFramework.getDomainRoot().getActivityProviderSet())
        def provider = providers.get(0)

        assert provider.getCode() == PROVIDER_CODE
        assert provider.getName() == PROVIDER_NAME
        assert provider.getActivitySet().size() == 1
        assert provider.getNif() == NIF
        assert provider.getIban() == IBAN
        
        def processor = provider.getProcessor()
        
        assert processor != null
        assert processor.getBookingSet().size() == 1

        def activities = new ArrayList<>(provider.getActivitySet())
        def activity = activities.get(0)

        assert activity.getName() == ACTIVITY_NAME
        assert activity.getCode().startsWith(PROVIDER_CODE) == true
        assert activity.getMinAge() == 18
        assert activity.getMaxAge() == 65
        assert activity.getCapacity() == CAPACITY
        assert activity.getActivityOfferSet().size() == 1

        def offers = new ArrayList<>(activity.getActivityOfferSet())
        def offer = offers.get(0)

        assert offer.getBegin() == begin
        assert offer.getEnd() == end
        assert offer.getCapacity() == CAPACITY
        assert offer.getBookingSet().size() == 1
        assert offer.getPrice() == AMOUNT

        def bookings = new ArrayList<>(offer.getBookingSet())
        def booking = bookings.get(0)

        assert booking.getReference() != null
        assert booking.getCancel() == null
        assert booking.getCancellationDate() == null
        assert booking.getPaymentReference() == null
        assert booking.getInvoiceReference() == null
        assert booking.getCancelledInvoice() == false
        assert booking.getCancelledPaymentReference() == null
        assert booking.getType() == "SPORT"
        assert booking.getBuyerNif() == BUYER_NIF
        assert booking.getIban() == BUYER_IBAN
        assert booking.getProviderNif() == NIF
        assert booking.getAmount() == AMOUNT
        assert booking.getAdventureId() == ADVENTURE_ID
        assert booking.getDate() == begin
        assert booking.getTime() != null
        assert booking.getProcessor() != null
    }

    @Override
    def deleteFromDatabase() {
        for(def activityProvider: FenixFramework.getDomainRoot().getActivityProviderSet()) {
            activityProvider.delete()    
        }    
    }

    
}

package pt.ulisboa.tecnico.softeng.broker.domain

import org.joda.time.LocalDate

import pt.ist.fenixframework.FenixFramework

class BrokerPersistenceSpockTest extends SpockPersistenceTestAbstractClass {
  def BROKER_NAME = "WeExplore"
  def BROKER_CODE = "BR01"
  def BROKER_NIF_AS_SELLER = "sellerNIF"
  def BROKER_IBAN = "BROKER_IBAN"
  def CLIENT_NIF = "123456789"
  def CLIENT_IBAN = "BK011234567"
  def NIF_AS_BUYER = "buyerNIF"

  def AGE = 20
  def MARGIN = 0.3
  def REF_ONE = "ref1"
  def NUMBER_OF_BULK = 20
  def DRIVING_LICENSE = "IMT1234"

  def begin = LocalDate.parse("2016-12-19")
  def end = LocalDate.parse("2016-12-21")

  @Override
  def whenCreateInDatabase() {
    def broker = new Broker(BROKER_CODE, BROKER_NAME, BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)
    def client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
    
    new Adventure(broker, this.begin, this.end, client, MARGIN, true)
    def bulk = new BulkRoomBooking(broker, NUMBER_OF_BULK, this.begin, this.end, NIF_AS_BUYER, CLIENT_IBAN)

    new Reference(bulk, REF_ONE)
  }

  @Override
  def thenAssert() {
    assert FenixFramework.getDomainRoot().getBrokerSet().size() == 1
    
    def brokers = new ArrayList<>(FenixFramework.getDomainRoot().getBrokerSet())
    def broker = brokers.get(0)

    assert broker.getCode() == BROKER_CODE
    assert broker.getName() == BROKER_NAME
    assert broker.getAdventureSet().size() == 1
    assert broker.getRoomBulkBookingSet().size() == 1
    assert broker.getNifAsBuyer() == NIF_AS_BUYER
    assert broker.getNifAsSeller() == BROKER_NIF_AS_SELLER
    assert broker.getIban() == BROKER_IBAN

    def adventures = new ArrayList<>(broker.getAdventureSet())
    def adventure = adventures.get(0)

    assert adventure.getID() != null
    assert adventure.getBroker() == broker
    assert adventure.getBegin() == this.begin
    assert adventure.getEnd() == this.end
    assert adventure.getAge() == AGE
    assert adventure.getIban() == CLIENT_IBAN
    assert adventure.getPaymentConfirmation() == null
    assert adventure.getPaymentCancellation() == null
    assert adventure.getRentingConfirmation() == null
    assert adventure.getRentingCancellation() == null
    assert adventure.getActivityConfirmation() == null
    assert adventure.getActivityCancellation() == null
    assert adventure.getRentingConfirmation() == null
    assert adventure.getRentingCancellation() == null
    assert adventure.getInvoiceReference() == null
    assert adventure.getInvoiceCancelled() == false
    assert adventure.getRentVehicle() == true
    assert adventure.getTime() != null
    assert adventure.getMargin() == MARGIN
    assert adventure.getCurrentAmount() == 0.0
    assert adventure.getClient().getAdventureSet().size() == 1
    assert adventure.getState().getValue() == Adventure.State.RESERVE_ACTIVITY
    assert adventure.getState().getNumOfRemoteErrors() == 0

    def bulks = new ArrayList<>(broker.getRoomBulkBookingSet())
    def bulk = bulks.get(0)

    assert bulk != null
    assert bulk.getArrival() == this.begin
    assert bulk.getDeparture() == this.end
    assert bulk.getNumber() == NUMBER_OF_BULK
    assert bulk.getCancelled() == false
    assert bulk.getNumberOfHotelExceptions() == 0
    assert bulk.getNumberOfRemoteErrors() == 0
    assert bulk.getReferenceSet().size() == 1
    assert bulk.getBuyerIban() == CLIENT_IBAN
    assert bulk.getBuyerNif() == NIF_AS_BUYER

    def references = new ArrayList<>(bulk.getReferenceSet())
    def reference = references.get(0)
    assert reference.getValue() == REF_ONE

    def client = adventure.getClient()

    assert client.getIban() == CLIENT_IBAN
    assert client.getNif() == CLIENT_NIF
    assert client.getAge() == AGE
    assert client.getDrivingLicense() == DRIVING_LICENSE
  }

  @Override
  def deleteFromDatabase() {
    for (def broker : FenixFramework.getDomainRoot().getBrokerSet()) {
      broker.delete()
    }
  }
}

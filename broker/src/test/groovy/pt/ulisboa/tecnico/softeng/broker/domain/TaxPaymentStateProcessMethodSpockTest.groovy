package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State
import pt.ulisboa.tecnico.softeng.broker.services.remote.ActivityInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.CarInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.TaxException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException
import spock.lang.Shared
import spock.lang.Unroll

class TaxPaymentStateProcessMethodSpockTest extends SpockRollbackTestAbstractClass {
  @Shared def MAX_REMOTE_ERROS = 3

  def adventure

  def activityInterface
  def bankInterface
  def carInterface
  def hotelInterface
  def taxInterface

  @Override
  def populate4Test() {
    activityInterface = Mock(ActivityInterface)
    bankInterface = Mock(BankInterface)
    carInterface = Mock(CarInterface)
    hotelInterface = Mock(HotelInterface)
    taxInterface = Mock(TaxInterface)

    def broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER,
    NIF_AS_BUYER, BROKER_IBAN, activityInterface, bankInterface, carInterface,
    hotelInterface, taxInterface)

    def client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE,
    AGE)

    adventure = new Adventure(broker, BEGIN, END, client, MARGIN)

    adventure.setState(State.TAX_PAYMENT)
  }

  def 'success'() {
    given:
    taxInterface.submitInvoice(_) >> INVOICE_REFERENCE

    when:
    adventure.process()

    then:
    adventure.getState().getValue() == State.CONFIRMED
  }

  
  @Unroll("the #failure occured")
  def 'exceptions'() {
    when:
    adventure.process()

    then:
    1 * taxInterface.submitInvoice(_) >> {throw exception}
    and:
    adventure.getState().getValue() == state 

    where:
    exception                   | state             | failure
    new TaxException()          | State.UNDO        | 'tax exception'
    new RemoteAccessException() | State.TAX_PAYMENT | 'remote access excepiton'
  }

  @Unroll("testing #numberOf remote exceptions")
  def 'remote exception'() {
    given:
    taxInterface.submitInvoice(_) >> { throw new RemoteAccessException() }

    when:
    numberOf.times { adventure.process() }

    then:
    adventure.getState().getValue() == state

    where:
    numberOf             | state
    MAX_REMOTE_ERROS     | State.UNDO
    MAX_REMOTE_ERROS - 1 | State.TAX_PAYMENT  
  }

  def 'two remote exceptions one success'() {
    when:
    3.times { adventure.process() }

    then:
    2 * taxInterface.submitInvoice(_) >> { throw new RemoteAccessException() }
    and:
    1 * taxInterface.submitInvoice(_) >> INVOICE_REFERENCE
    and:
    adventure.getState().getValue() == State.CONFIRMED
  }

  def 'one remote access exception one tax exeception'() {
    when:
    2.times { adventure.process() }

    then:
    1 * taxInterface.submitInvoice(_) >> { throw new RemoteAccessException() }
    and:
    1 * taxInterface.submitInvoice(_) >> { throw new TaxException() }
    and:
    adventure.getState().getValue() == State.UNDO
  }
}

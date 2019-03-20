package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State
import pt.ulisboa.tecnico.softeng.broker.services.remote.ActivityInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.CarInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestBankOperationData
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.BankException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException

class ProcessPaymentStateProcessMethodSpockTest extends SpockRollbackTestAbstractClass {
  def TRANSACTION_SOURCE = "ADVENTURE"

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

    def broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN, activityInterface, bankInterface, carInterface, hotelInterface, taxInterface)

    def client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
    adventure = new Adventure(broker, BEGIN, END, client, MARGIN)

    adventure.setState(State.PROCESS_PAYMENT)
  }

  def 'success'() {
    given:
    bankInterface.processPayment(_) >> PAYMENT_CONFIRMATION

    when:
    adventure.process()

    then:
    adventure.getState().getValue() == State.TAX_PAYMENT
  }

  def 'bank exception'() {
    given:
    bankInterface.processPayment(_) >> { throw new BankException() }

    when:
    adventure.process()
    adventure.process()
    
    then:
    adventure.getState().getValue() == State.CANCELLED
  }

  def 'single remote exception'() {
    given:
    bankInterface.processPayment(_) >> { throw new RemoteAccessException() }

    when:
    adventure.process()

    then:
    adventure.getState().getValue() == State.PROCESS_PAYMENT
  }

  def 'max remote exception'() {
    given:
    bankInterface.processPayment(_) >> { throw new RemoteAccessException() }

    when:
    adventure.process()
    adventure.process()
    adventure.process()
    adventure.process()

    then:
    adventure.getState().getValue() == State.CANCELLED
  }

  def 'max minus one remote exception'() {
    given:
    bankInterface.processPayment(_) >> { throw new RemoteAccessException() }

    when:
    adventure.process()
    adventure.process()

    then:
    adventure.getState().getValue() == State.PROCESS_PAYMENT
  }

  def 'two remote exceptions one success'() {
    when:
    adventure.process()
    adventure.process()
    adventure.process()

    then:
    2 * bankInterface.processPayment(_) >> { throw new RemoteAccessException() }
    1 * bankInterface.processPayment(_) >> PAYMENT_CONFIRMATION
    and:
    adventure.getState().getValue() == State.TAX_PAYMENT
  }

  def 'one remote access exception one bank exception'() {
    when:
    adventure.process()
    adventure.process()
    adventure.process()

    then:
    1 * bankInterface.processPayment(_) >> { throw new RemoteAccessException() }
    1 * bankInterface.processPayment(_) >> { throw new BankException() }
    and:
    adventure.getState().getValue() == State.CANCELLED
  }
}

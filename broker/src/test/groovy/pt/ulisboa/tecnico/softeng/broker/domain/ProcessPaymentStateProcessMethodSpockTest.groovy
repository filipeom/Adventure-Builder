package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State
import pt.ulisboa.tecnico.softeng.broker.services.remote.ActivityInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.CarInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.BankException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException
import spock.lang.Unroll

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

  @Unroll('Exceptions: #exception, #numberOf, #state')
  def 'exceptions'() {
    given:
    bankInterface.processPayment(_) >> { throw exception }

    when:
    numberOf.times { adventure.process() }

    then:
    adventure.getState().getValue() == state

    where:
    exception                   | numberOf | state
    new BankException()         | 2        | State.CANCELLED
    new RemoteAccessException() | 1        | State.PROCESS_PAYMENT
    new RemoteAccessException() | 4        | State.CANCELLED
    new RemoteAccessException() | 2        | State.PROCESS_PAYMENT
  }
  
  def 'two remote exceptions one success'() {
    when:
    3.times { adventure.process() }

    then:
    2 * bankInterface.processPayment(_) >> { throw new RemoteAccessException() }
    1 * bankInterface.processPayment(_) >> PAYMENT_CONFIRMATION
    and:
    adventure.getState().getValue() == State.TAX_PAYMENT
  }

  def 'one remote access exception one bank exception'() {
    when:
    3.times { adventure.process() }

    then:
    1 * bankInterface.processPayment(_) >> { throw new RemoteAccessException() }
    1 * bankInterface.processPayment(_) >> { throw new BankException() }
    and:
    adventure.getState().getValue() == State.CANCELLED
  }
}

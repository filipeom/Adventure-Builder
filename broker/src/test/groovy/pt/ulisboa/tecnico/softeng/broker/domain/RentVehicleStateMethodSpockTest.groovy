package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRentingData

import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State
import pt.ulisboa.tecnico.softeng.broker.services.remote.ActivityInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.CarInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.CarException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException

class RentVehicleStateMethodSpockTest extends SpockRollbackTestAbstractClass {
  def adventure

  def activityInterface
  def bankInterface
  def carInterface
  def hotelInterface
  def taxInterface
  def rentingData

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

    rentingData = new RestRentingData()
		rentingData.setReference(RENTING_CONFIRMATION)
		rentingData.setPrice(76.78)

    adventure.setState(State.RENT_VEHICLE)
  }

  //could use data table to join test cases
  def 'success Rent Vehicle'() {
    when:
    adventure.process()

    then:
    1 * carInterface.rentCar(CarInterface.Type.CAR, DRIVING_LICENSE, BROKER_NIF_AS_BUYER, BROKER_IBAN, BEGIN, END, _) >> rentingData
    and:
    adventure.getState().getValue() == State.PROCESS_PAYMENT
  }

  def 'car Exception'() {
    when:
    adventure.process()

    then:
    1 * carInterface.rentCar(CarInterface.Type.CAR, DRIVING_LICENSE, BROKER_NIF_AS_BUYER, BROKER_IBAN, BEGIN, END, _) >> { throw new CarException() }
    and:
    adventure.getState().getValue() == State.UNDO
  }

  def 'single Remote Access Exception'() {
    when:
    adventure.process()

    then:
    1 * carInterface.rentCar(CarInterface.Type.CAR, DRIVING_LICENSE, BROKER_NIF_AS_BUYER, BROKER_IBAN, BEGIN, END, _) >> { throw new RemoteAccessException() }
    and:
    adventure.getState().getValue() == State.RENT_VEHICLE
  }

  def 'max Remote Access Exception'() {
    when:
    for (int i = 0; i < RentVehicleState.MAX_REMOTE_ERRORS; i++)
      adventure.process()

    then:
    RentVehicleState.MAX_REMOTE_ERRORS *  carInterface.rentCar(CarInterface.Type.CAR, DRIVING_LICENSE, BROKER_NIF_AS_BUYER, BROKER_IBAN, BEGIN, END, _) >> { throw new RemoteAccessException() }
    and:
    adventure.getState().getValue() == State.UNDO
  }

  def 'max Minus One Remote Access Exception'() {
    when:
    for (int i = 0; i < RentVehicleState.MAX_REMOTE_ERRORS - 1; i++)
			adventure.process()

    then:
    ( RentVehicleState.MAX_REMOTE_ERRORS - 1 ) * carInterface.rentCar(CarInterface.Type.CAR, DRIVING_LICENSE, BROKER_NIF_AS_BUYER, BROKER_IBAN, BEGIN, END, _) >> { throw new RemoteAccessException() }
    and:
    adventure.getState().getValue() == State.RENT_VEHICLE
  }

  def 'two Remote Access Exception One Success'() {
    when:
    for (int i = 0; i < 3; i++)
      adventure.process()

    then:
    2 * carInterface.rentCar(CarInterface.Type.CAR, DRIVING_LICENSE, BROKER_NIF_AS_BUYER, BROKER_IBAN, BEGIN, END, _) >> { throw new RemoteAccessException() }
    1 * carInterface.rentCar(CarInterface.Type.CAR, DRIVING_LICENSE, BROKER_NIF_AS_BUYER, BROKER_IBAN, BEGIN, END, _) >> rentingData
    and:
    adventure.getState().getValue() == State.PROCESS_PAYMENT
  }

  def 'one Remote Access Exception One Car Exception'() {
    when:
    for (int i = 0; i < 2; i++)
      adventure.process()

    then:
    1 * carInterface.rentCar(CarInterface.Type.CAR, DRIVING_LICENSE, BROKER_NIF_AS_BUYER, BROKER_IBAN, BEGIN, END, _) >> { throw new RemoteAccessException() }
    1 * carInterface.rentCar(CarInterface.Type.CAR, DRIVING_LICENSE, BROKER_NIF_AS_BUYER, BROKER_IBAN, BEGIN, END, _) >> { throw new CarException() }
    and:
    adventure.getState().getValue() == State.UNDO
  }
  
}

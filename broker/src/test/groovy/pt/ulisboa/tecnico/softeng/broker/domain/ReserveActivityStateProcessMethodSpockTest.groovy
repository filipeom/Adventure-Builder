package pt.ulisboa.tecnico.softeng.broker.domain

import spock.lang.Unroll
import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State
import pt.ulisboa.tecnico.softeng.broker.services.remote.ActivityInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.CarInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestActivityBookingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.ActivityException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException


class ReserveActivityStateProcessMethodSpockTest extends SpockRollbackTestAbstractClass {
	def adventure
	
	def activityInterface
	def bankInterface
	def carInterface
	def hotelInterface
	def taxInterface

	def bookingData
	def broker
	def client

	@Override
	def populate4Test() {

		activityInterface = Mock(ActivityInterface)
		bankInterface = Mock(BankInterface)
		carInterface = Mock(CarInterface)
		hotelInterface = Mock(HotelInterface)
		taxInterface = Mock(TaxInterface)

		broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER,
		NIF_AS_BUYER, BROKER_IBAN, activityInterface, bankInterface, carInterface,
		hotelInterface, taxInterface)

		client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
		adventure = new Adventure(broker, BEGIN, END, client, MARGIN)
		bookingData = new RestActivityBookingData()
		bookingData.setReference(ACTIVITY_CONFIRMATION)
		bookingData.setPrice(76.78)
		
		adventure.setState(State.RESERVE_ACTIVITY)
	}

	//idem for other tests
	def 'successNoBookRoom'() {
		given:
		def sameDayAdventure = new Adventure(broker, BEGIN, BEGIN, client, MARGIN)
		sameDayAdventure.setState(State.RESERVE_ACTIVITY)
		activityInterface.reserveActivity(_) >> bookingData


		when:
		sameDayAdventure.process()

		then:
		sameDayAdventure.getState().getValue() == State.PROCESS_PAYMENT
	}


	def 'successToRentVehicle'() {
		def adv = new Adventure(broker, BEGIN, BEGIN, client, MARGIN, true)
		adv.setState(State.RESERVE_ACTIVITY)

		when:
		adv.process()

		then:
		activityInterface.reserveActivity(_) >> bookingData

		and:
		adv.getState().getValue() == State.RENT_VEHICLE
	}	

	def 'successBookRoom'() {
		when:
		adventure.process()

		then:
		activityInterface.reserveActivity(_) >> bookingData
		
		and:
		adventure.getState().getValue() == State.BOOK_ROOM
	}

	@Unroll('exceptions: #exception, #state')
	def 'exception'() {
		when:
		adventure.process()

		then:
		activityInterface.reserveActivity(_) >> { throw exception }
		
		and:
		adventure.getState().getValue() == state

		where:
		exception | state
		new ActivityException() | State.UNDO
		new RemoteAccessException() | State.RESERVE_ACTIVITY
	}

	@Unroll('RemoteException: #state, #n')
	def 'RemoteException'() {
		when:
		for (int i = 0; i < n; i++) 
			adventure.process()
		
		then:
		activityInterface.reserveActivity(_) >> { throw new RemoteAccessException() }

		and:
		adventure.getState().getValue() == state

		where:
		state                  | n
		State.UNDO             | 5
		State.RESERVE_ACTIVITY | 4 
	}
	
	def 'twoRemoteAccessExceptionOneSuccess'() {
		when:
		adventure.process()
		adventure.process()
		adventure.process()

		then:
		2 * activityInterface.reserveActivity(_) >> { throw new RemoteAccessException() }
		1 * activityInterface.reserveActivity(_) >> { bookingData}

		and:
		adventure.getState().getValue() == State.BOOK_ROOM 
	}

	def 'oneRemoteAccessExceptionOneActivityException'() {
		when:
		adventure.process()
		adventure.process()
		
		then:
		1 * activityInterface.reserveActivity(_) >> { throw new RemoteAccessException() }
		1 * activityInterface.reserveActivity(_) >> { throw new ActivityException() }
		
		and:
		adventure.getState().getValue() == State.UNDO

	}
}



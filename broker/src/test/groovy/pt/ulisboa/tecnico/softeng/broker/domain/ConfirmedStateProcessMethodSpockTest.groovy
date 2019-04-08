package pt.ulisboa.tecnico.softeng.broker.domain

import spock.lang.Unroll
import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State
import pt.ulisboa.tecnico.softeng.broker.services.remote.ActivityInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.CarInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestActivityBookingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRentingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRoomBookingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.ActivityException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.BankException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.CarException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException

class ConfirmedStateProcessMethodSpockTest extends SpockRollbackTestAbstractClass {
	def adventure

	def activityReservationData
	def rentingData
	def roomBookingData

	def activityInterface
	def bankInterface
	def carInterface
	def hotelInterface
	def taxInterface

	@Override
	def populate4Test() {
	activityReservationData = Mock(RestActivityBookingData)
	rentingData 		= Mock(RestRentingData)
	roomBookingData 	= Mock(RestRoomBookingData)

	activityInterface 	= Mock(ActivityInterface)
	bankInterface 		= Mock(BankInterface)
	carInterface 		= Mock(CarInterface)
	hotelInterface 		= Mock(HotelInterface)
	taxInterface 		= Mock(TaxInterface)

	def broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, 
	NIF_AS_BUYER, BROKER_IBAN, activityInterface, bankInterface, carInterface,
	hotelInterface, taxInterface)
	def client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)

	adventure = new Adventure(broker, BEGIN, END, client, MARGIN)

	adventure.setState(State.CONFIRMED)
	}

	//careful with the spock flow conventions. idem for other tests
	def 'success all'() {
		given:
		adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
		adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
		adventure.setRentingConfirmation(RENTING_CONFIRMATION)
		adventure.setRoomConfirmation(ROOM_CONFIRMATION)
		1 * bankInterface.getOperationData(PAYMENT_CONFIRMATION)
		1 * activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> activityReservationData
		1 * carInterface.getRentingData(RENTING_CONFIRMATION)                   >> rentingData
		1 * hotelInterface.getRoomBookingData(ROOM_CONFIRMATION)                >> roomBookingData

		activityReservationData.getPaymentReference() >> REFERENCE
		activityReservationData.getInvoiceReference() >> REFERENCE
		rentingData.getPaymentReference() 	      >> REFERENCE
		rentingData.getInvoiceReference()             >> REFERENCE
		roomBookingData.getPaymentReference()         >> REFERENCE
		roomBookingData.getInvoiceReference()	      >> REFERENCE

		when:
		adventure.process()

		then:
		adventure.getState().getValue() == State.CONFIRMED
	}


	def 'successActivityAndHotel'() {
		given:
		adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
		adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
		adventure.setRoomConfirmation(ROOM_CONFIRMATION)

		when:
		adventure.process()

		then:
		1 * bankInterface.getOperationData(PAYMENT_CONFIRMATION)
		1 * activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> activityReservationData
		1 * hotelInterface.getRoomBookingData(ROOM_CONFIRMATION)                >> roomBookingData
		
		activityReservationData.getPaymentReference()     >> REFERENCE
		activityReservationData.getInvoiceReference() 	  >> REFERENCE
		roomBookingData.getPaymentReference()             >> REFERENCE
		roomBookingData.getInvoiceReference() 	          >> REFERENCE

		and:
		adventure.getState().getValue() == State.CONFIRMED
	}

	def 'successActivityAndCar'() {
		given:
		adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
		adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
		adventure.setRentingConfirmation(RENTING_CONFIRMATION)

		when:
		adventure.process()

		then:
		1 * bankInterface.getOperationData(PAYMENT_CONFIRMATION)
		1 * activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> activityReservationData
		1 * carInterface.getRentingData(RENTING_CONFIRMATION)                   >> rentingData

		activityReservationData.getPaymentReference()     >> REFERENCE
		activityReservationData.getInvoiceReference()     >> REFERENCE
		rentingData.getPaymentReference() 	      	  >> REFERENCE
		rentingData.getInvoiceReference() 	          >> REFERENCE

		and:
		adventure.getState().getValue() == State.CONFIRMED
	}
	
	def 'successActivity'() {
		given:
		adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
		adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)

		when:
		adventure.process()

		then:
		1 * bankInterface.getOperationData(PAYMENT_CONFIRMATION) 
		1 * activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> activityReservationData

		activityReservationData.getPaymentReference() >> REFERENCE
		activityReservationData.getInvoiceReference() >> REFERENCE

		and:
		adventure.getState().getValue() == State.CONFIRMED
	}



	@Unroll('Exceptions: #exception, #n, #state')
	def 'oneBankException'() {
		given:
		adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
		adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)

		when:
		for(int i = 0; i < n; i++)
			adventure.process()

		then:
		bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> { throw exception }

		and:
		adventure.getState().getValue() == state
		
		where:
		exception 		     | n 				      | state
		new BankException()  	     | 1 				      | State.CONFIRMED
		new BankException()          | ConfirmedState.MAX_BANK_EXCEPTIONS     | State.UNDO
		new BankException()  	     | ConfirmedState.MAX_BANK_EXCEPTIONS - 1 | State.CONFIRMED
		new RemoteAccessException()  | 1 				      | State.CONFIRMED
	}
	
	@Unroll('Exceptions: #exception, #state')
	def 'activity and remote exception'() {
		given:
		adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
		adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)

		when:
		adventure.process()

		then:
		1 * bankInterface.getOperationData(PAYMENT_CONFIRMATION)
		
		activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> { throw exception }

		and:
		adventure.getState().getValue() == state

		where:
		exception                   | state
		new ActivityException()     | State.UNDO
		new RemoteAccessException() | State.CONFIRMED 
	}

	def 'activityNoPaymentConfirmation'() {
		given:
		adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
		adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)

		when:
		adventure.process()
		
		then:
		1 * bankInterface.getOperationData(PAYMENT_CONFIRMATION)
		1 * activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> activityReservationData

		1 * activityReservationData.getPaymentReference() >> null

		and:
		adventure.getState().getValue() == State.UNDO
	}

	def 'activityNoInvoiceReference'() {
		given:
		adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
		adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION) 

		when:
		adventure.process()

		then:
		1 * bankInterface.getOperationData(PAYMENT_CONFIRMATION)
		1 * activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> activityReservationData

		activityReservationData.getPaymentReference() >> REFERENCE
		activityReservationData.getInvoiceReference() >> null

		and:
		adventure.getState().getValue() == State.UNDO
	}

	@Unroll('car exceptions: #exception, #state')
	def 'car exceptions'() {
		given:
		adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
		adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
		adventure.setRentingConfirmation(RENTING_CONFIRMATION)
		
		when:
		adventure.process()

		then:
		1 * bankInterface.getOperationData(PAYMENT_CONFIRMATION)
		1 * activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> activityReservationData

		activityReservationData.getPaymentReference()     >> REFERENCE
		activityReservationData.getInvoiceReference()     >> REFERENCE
		carInterface.getRentingData(RENTING_CONFIRMATION) >> { throw exception }
		
		and:
		adventure.getState().getValue() == state

		where:
		exception                   | state
		new CarException()          | State.UNDO
		new RemoteAccessException() | State.CONFIRMED		
	}

	def 'carNoPaymentConfirmation'() {
		given:
		adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
		adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
		adventure.setRentingConfirmation(RENTING_CONFIRMATION)
		
		when:
		adventure.process()
		
		then:
		1 * bankInterface.getOperationData(PAYMENT_CONFIRMATION)
		1 * activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> activityReservationData

		activityReservationData.getPaymentReference()     >> REFERENCE
		activityReservationData.getInvoiceReference()     >> REFERENCE
		carInterface.getRentingData(RENTING_CONFIRMATION) >> rentingData
		rentingData.getPaymentReference()        	  >> null

		and:
		adventure.getState().getValue() == State.UNDO
	}

	def 'carNoInvoiceRefrence'() {
		given:
		adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
		adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
		adventure.setRentingConfirmation(RENTING_CONFIRMATION)

		when:
		adventure.process()

		then:
		1 * bankInterface.getOperationData(PAYMENT_CONFIRMATION)
		1 * activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> activityReservationData

		activityReservationData.getPaymentReference()     >> REFERENCE
		activityReservationData.getInvoiceReference()     >> REFERENCE
		carInterface.getRentingData(RENTING_CONFIRMATION) >> rentingData
		rentingData.getPaymentReference() 		  >> REFERENCE 
		rentingData.getInvoiceReference() 	          >> null

		and:
		adventure.getState().getValue() == State.UNDO
	}	

	@Unroll('Hotel exceptions: #exception, #state')
	def 'hotel exceptions'() {
		given:
		adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
		adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
		adventure.setRoomConfirmation(ROOM_CONFIRMATION)

		when:
		adventure.process()

		then:
		1 * bankInterface.getOperationData(PAYMENT_CONFIRMATION)
		1 * activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> activityReservationData

		activityReservationData.getPaymentReference()		 >> REFERENCE
		activityReservationData.getInvoiceReference() 		 >> REFERENCE
		hotelInterface.getRoomBookingData(ROOM_CONFIRMATION)     >> { throw exception }
		
		and:
		adventure.getState().getValue() == state
	
		where:
		exception   		     | state
		new HotelException() 	     | State.UNDO
		new RemoteAccessException () | State.CONFIRMED
	}

	def 'hotelNoPaymentConfirmation'() {
		given:
		adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
		adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
		adventure.setRoomConfirmation(ROOM_CONFIRMATION)
		
		when:
		adventure.process()

		then:
		1 * bankInterface.getOperationData(PAYMENT_CONFIRMATION)
		1 * activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> activityReservationData

		activityReservationData.getPaymentReference() 	     >> REFERENCE
		activityReservationData.getInvoiceReference()	     >> REFERENCE
		hotelInterface.getRoomBookingData(ROOM_CONFIRMATION) >> roomBookingData
		roomBookingData.getPaymentReference() 		     >> null

		and:
		adventure.getState().getValue() == State.UNDO	
	}




	def 'hotelNoInvoiceReference'() {
		given:
		adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
		adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
		adventure.setRoomConfirmation(ROOM_CONFIRMATION)

		when:
		adventure.process()
		
		then:	
		1 * bankInterface.getOperationData(PAYMENT_CONFIRMATION)
		1 * activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> activityReservationData

		activityReservationData.getPaymentReference()    	 >> REFERENCE
		activityReservationData.getInvoiceReference() 		 >> REFERENCE
		hotelInterface.getRoomBookingData(ROOM_CONFIRMATION)     >> roomBookingData
		roomBookingData.getPaymentReference() 			 >> REFERENCE
		roomBookingData.getInvoiceReference() 			 >> null

		and:
		adventure.getState().getValue() == State.UNDO
	}	
}

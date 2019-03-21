package pt.ulisboa.tecnico.softeng.broker.domain;

import spock.lang.Unroll
import pt.ulisboa.tecnico.softeng.broker.services.remote.ActivityInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.CarInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.BankInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRoomBookingData;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException;


class BulkRoomBookingGetRoomBookingData4TypeMethodSpockTest extends SpockRollbackTestAbstractClass {

	def activityInterface
	def bankInterface
	def carInterface
	def hotelInterface
	def taxInterface

	def bulk

	@Override
	def populate4Test() {

		activityInterface = Mock(ActivityInterface)
		bankInterface     = Mock(BankInterface)
		carInterface      = Mock(CarInterface)
		hotelInterface    = Mock(HotelInterface)
		taxInterface      = Mock(TaxInterface)

		def broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER,
		NIF_AS_BUYER, BROKER_IBAN, activityInterface, bankInterface, carInterface,
		hotelInterface, taxInterface)
		
		bulk = new BulkRoomBooking(broker, NUMBER_OF_BULK, BEGIN, END, NIF_AS_BUYER, CLIENT_IBAN)
		new Reference(bulk, REF_ONE)
		new Reference(bulk, REF_TWO)
	}
	
	@Unroll('success SINGLE DOUBLE: #state')
	def 'success SINGLE DOUBLE'() {
		when:
		bulk.getRoomBookingData4Type(state)

		then:
		hotelInterface.getRoomBookingData(_ as String) >> {

			def roomBookingData = new RestRoomBookingData()
			roomBookingData.setRoomType(state)
			return roomBookingData
		}
		
		and:
		bulk.getReferences().size() == 1	

		where:
		state  | _
		SINGLE | _
		DOUBLE | _
	}
	
	@Unroll('hotel remote exception: #exception')
	def 'hotel remote exception'() {
		given:
		2 * hotelInterface.getRoomBookingData(_ as String) >> { throw exception }	
		
		expect:
		bulk.getRoomBookingData4Type(DOUBLE) == null
		bulk.getReferences().size() == 2

		where:
		exception                   | _
		new HotelException()        | _
		new RemoteAccessException() | _
	}
	
	def 'maxRemoteException'() {
		given:
		def errors = BulkRoomBooking.MAX_REMOTE_ERRORS

		errors * hotelInterface.getRoomBookingData(_ as String) >> { throw new RemoteAccessException() }
		
		expect:
		for (int i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS / 2; i++) 
            		bulk.getRoomBookingData4Type(DOUBLE) == null
		bulk.getReferences().size() == 2
		bulk.getCancelled() == true
	}
	
	def 'maxMinusOneRemoteException'() {
		given:
		def errors = BulkRoomBooking.MAX_REMOTE_ERRORS - 1

		errors * hotelInterface.getRoomBookingData(_ as String) >> { throw new RemoteAccessException() }
		_ * hotelInterface.getRoomBookingData(_ as String) >> {

			def roomBookingData = new RestRoomBookingData()
			roomBookingData.setRoomType(DOUBLE)
			return roomBookingData
		} 
		
		when:
		bulk.getRoomBookingData4Type(DOUBLE)

		then:
		for (int i = 0; i < (errors + 1) / 2 - 1; i++) 
			bulk.getRoomBookingData4Type(DOUBLE) == null
		bulk.getReferences().size() == 1
	}

	def 'remoteExceptionValueIsResetBySuccess'() {
		given:
		def errors = BulkRoomBooking.MAX_REMOTE_ERRORS - 1

		errors * hotelInterface.getRoomBookingData(_ as String) >> { throw new RemoteAccessException() }
		1 * hotelInterface.getRoomBookingData(_ as String) >> {

			def roomBookingData = new RestRoomBookingData()
			roomBookingData.setRoomType(DOUBLE)
			return roomBookingData
		}
		_ * hotelInterface.getRoomBookingData(_ as String)      >> { throw new RemoteAccessException() }

		expect:
		for (int i = 0; i < (errors + 1) / 2 - 1; i++) 
			bulk.getRoomBookingData4Type(DOUBLE) == null

		when:
		bulk.getRoomBookingData4Type(DOUBLE)

		then:
		bulk.getReferences().size() == 1	
		for (int i = 0; i < (errors + 1) / 2 - 1; i++)
			bulk.getRoomBookingData4Type(DOUBLE) == null
		bulk.getCancelled() == false
	}

	def 'remoteExceptionValueIsResetByHotelSuccess'() {
		given:
		def errors = BulkRoomBooking.MAX_REMOTE_ERRORS - 1

		errors * hotelInterface.getRoomBookingData(_ as String) >> { throw new RemoteAccessException() }
		1 * hotelInterface.getRoomBookingData(_ as String)      >> { throw new HotelException() }
		_ * hotelInterface.getRoomBookingData(_ as String) 	    >> { throw new RemoteAccessException() }

		expect:
		for (int i = 0; i < (errors + 1) / 2 - 1; i++) 
			bulk.getRoomBookingData4Type(DOUBLE) == null

		when:
		bulk.getRoomBookingData4Type(DOUBLE)
		
		then:
		bulk.getReferences().size() == 2	
		for (int i = 0; i < (errors + 1) / 2 - 1; i++)
			bulk.getRoomBookingData4Type(DOUBLE) == null
		bulk.getCancelled() == false
	}
}


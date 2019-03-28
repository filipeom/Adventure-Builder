package pt.ulisboa.tecnico.softeng.hotel.services.local

import pt.ulisboa.tecnico.softeng.hotel.domain.Processor

import java.util.stream.Collectors

import org.joda.time.LocalDate

import pt.ist.fenixframework.FenixFramework
import pt.ulisboa.tecnico.softeng.hotel.domain.Hotel
import pt.ulisboa.tecnico.softeng.hotel.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.hotel.domain.Room
import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException
import pt.ulisboa.tecnico.softeng.hotel.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.hotel.services.remote.TaxInterface

class HotelInterfaceBulkBookingMethodAlternativeSpockTest extends SpockRollbackTestAbstractClass {
	def INVOICE_REFERENCE = 'InvoiceReference'
	def PAYMENT_REFERENCE = 'PaymentReference'
	def arrival = new LocalDate(2016, 12, 19)
	def departure = new LocalDate(2016, 12, 21)
	def NIF_BUYER = "123456789"
	def IBAN_BUYER = "IBAN_BUYER"
	def BULK_ID = "BULK_ID"

	def hotel
	def taxInterface
	def bankInterface
	def hotelInterface
	def processor

	@Override
	def populate4Test() {

		hotelInterface = new HotelInterface()

		bankInterface = Mock(BankInterface)
		taxInterface = Mock(TaxInterface)
		processor = new Processor(bankInterface, taxInterface)

		hotel = new Hotel("XPTO123", "Paris", "NIF", "IBAN", 20.0, 30.0, processor)

		new Room(hotel, "01", Type.DOUBLE)
		new Room(hotel, "02", Type.SINGLE)
		new Room(hotel, "03", Type.DOUBLE)
		new Room(hotel, "04", Type.SINGLE)

		bankInterface = Mock(BankInterface)
		taxInterface = Mock(TaxInterface)
		processor = new Processor(bankInterface, taxInterface)

		hotel = new Hotel("XPTO124", "Paris", "NIF2", "IBAN2", 25.0, 35.0, processor)
		new Room(hotel, "01", Type.DOUBLE)
		new Room(hotel, "02", Type.SINGLE)
		new Room(hotel, "03", Type.DOUBLE)
		new Room(hotel, "04", Type.SINGLE)
	}

	def "success"() {
		given:
		bankInterface.processPayment(_) >> PAYMENT_REFERENCE
		taxInterface.submitInvoice(_) >> INVOICE_REFERENCE

		when:
		def references = hotelInterface.bulkBooking(2, arrival, departure, NIF_BUYER,
				IBAN_BUYER, BULK_ID)

		then:
		references.size() == 2
	}

	def "zeroNumber"() {
		when:
		hotelInterface.bulkBooking(0, arrival, departure, NIF_BUYER, IBAN_BUYER, BULK_ID)

		then:
		thrown(HotelException)
	}

	def "noRooms"() {
		given:
		for (hotel in FenixFramework.getDomainRoot().getHotelSet()) {
			hotel.delete()
		}
		taxInterface = Mock(TaxInterface)
		bankInterface = Mock(BankInterface)
		processor = new Processor(bankInterface, taxInterface)
		hotel = new Hotel("XPTO124", "Paris", "NIF", "IBAN", 27.0, 37.0, processor)

		when:
		hotelInterface.bulkBooking(3, arrival, departure, NIF_BUYER, IBAN_BUYER, BULK_ID)

		then:
		thrown(HotelException)
	}

	def "OneNumber"() {
		when:
		def references = hotelInterface.bulkBooking(1, arrival, departure, NIF_BUYER,
				IBAN_BUYER, BULK_ID)

		then:
		references.size() == 1
	}

	def "nullArrival"() {
		when:
		hotelInterface.bulkBooking(2, null, departure, NIF_BUYER, IBAN_BUYER, BULK_ID)

		then:
		thrown(HotelException)
	}

	def "nullDeparture"() {
		when:
		hotelInterface.bulkBooking(2, arrival, null, NIF_BUYER, IBAN_BUYER, BULK_ID)

		then:
		thrown(HotelException)
	}

	def "reserveAll"() {
		when:
		def references = hotelInterface.bulkBooking(8, arrival, departure, NIF_BUYER,
				IBAN_BUYER, BULK_ID)

		then:
		references.size() == 8
	}

	def "reserveAllPlusOne"() {
		when:
		hotelInterface.bulkBooking(9, arrival, departure, NIF_BUYER, IBAN_BUYER, BULK_ID)
		then:
		thrown (HotelException)
		and:
		hotelInterface.getAvailableRooms(8, arrival, departure).size() == 8
	}

	def "idempotentBulkBooking"() {
		given:
		bankInterface.processPayment(_) >> PAYMENT_REFERENCE
		taxInterface.submitInvoice(_) >> INVOICE_REFERENCE

		when:
		def references = hotelInterface.bulkBooking(4, arrival, departure, NIF_BUYER,
				IBAN_BUYER, BULK_ID)
		def equalReferences = hotelInterface.bulkBooking(4, arrival, departure, NIF_BUYER,
				IBAN_BUYER, BULK_ID)
		then:
		references.size() == 4
		hotelInterface.getAvailableRooms(4, arrival, departure).size() == 4
		references.stream().sorted().collect(Collectors.toList()) == equalReferences.stream().sorted().collect(Collectors.toList())
	}

}
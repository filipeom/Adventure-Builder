package pt.ulisboa.tecnico.softeng.hotel.domain;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.joda.time.LocalDate;

import pt.ist.fenixframework.FenixFramework;
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;

public class Hotel extends Hotel_Base {
	static final int CODE_SIZE = 7;

	public Hotel(final String code, final String name, final String nif, final String iban, final double priceSingle, final double priceDouble) {
		this(code, name, nif, iban, priceSingle, priceDouble, new Processor());
	}

	public Hotel(final String code, final String name, final String nif, final String iban, final double priceSingle, final double priceDouble, final Processor processor) {
		checkArguments(code, name, nif, iban, priceSingle, priceDouble);

		setCode(code);
		setName(name);
		setNif(nif);
		setIban(iban);
		setPriceSingle(priceSingle);
		setPriceDouble(priceDouble);

		setProcessor(processor);

		FenixFramework.getDomainRoot().addHotel(this);
	}

	public void delete() {
		setRoot(null);

		getProcessor().delete();

		for (final Room room : getRoomSet()) {
			room.delete();
		}

		deleteDomainObject();
	}

	private void checkArguments(final String code, final String name, final String nif, final String iban, final double priceSingle,
			final double priceDouble) {
		if (code == null || name == null || isEmpty(code) || isEmpty(name) || nif == null || isEmpty(nif)
				|| iban == null || isEmpty(iban) || priceSingle < 0 || priceDouble < 0) {

			throw new HotelException();
		}

		if (code.length() != Hotel.CODE_SIZE) {
			throw new HotelException();
		}

		for (final Hotel hotel : FenixFramework.getDomainRoot().getHotelSet()) {
			if (hotel.getCode().equals(code)) {
				throw new HotelException();
			}
		}

		for (final Hotel hotel : FenixFramework.getDomainRoot().getHotelSet()) {
			if (hotel.getNif().equals(nif)) {
				throw new HotelException();
			}
		}
	}

	public Room hasVacancy(final Room.Type type, final LocalDate arrival, final LocalDate departure) {
		if (type == null || arrival == null || departure == null) {
			throw new HotelException();
		}

		for (final Room room : getRoomSet()) {
			if (room.isFree(type, arrival, departure)) {
				return room;
			}
		}
		return null;
	}

	public Set<Room> getAvailableRooms(final LocalDate arrival, final LocalDate departure) {
		final Set<Room> availableRooms = new HashSet<>();
		for (final Room room : getRoomSet()) {
			if (room.isFree(room.getType(), arrival, departure)) {
				availableRooms.add(room);
			}
		}
		return availableRooms;
	}

	public double getPrice(final Room.Type type) {
		if (type == null) {
			throw new HotelException();
		} else {
			return type.equals(Room.Type.SINGLE) ? getPriceSingle() : getPriceDouble();
		}
	}

	public void setPrice(final Room.Type type, final double price) {
		if (price < 0 || type == null) {
			throw new HotelException();
		} else if (type.equals(Room.Type.SINGLE)) {
			setPriceSingle(price);
		} else {
			setPriceDouble(price);
		}
	}

	private boolean isEmpty(final String str) {
		return str.trim().length() == 0;
	}

	@Override
	public void addRoom(final Room room) {
		if (hasRoom(room.getNumber())) {
			throw new HotelException();
		}

		super.addRoom(room);
	}

	public boolean hasRoom(final String number) {
		for (final Room room : getRoomSet()) {
			if (room.getNumber().equals(number)) {
				return true;
			}
		}
		return false;
	}

	public Booking getBooking(final String reference) {
		for (final Room room : getRoomSet()) {
			final Booking booking = room.getBooking(reference);
			if (booking != null) {
				return booking;
			}
		}
		return null;
	}

	public Room getRoomByNumber(final String number) {
		return getRoomSet().stream().filter(r -> r.getNumber().equals(number)).findFirst().orElse(null);
	}

	@Override
	public int getCounter() {
		final int counter = super.getCounter() + 1;
		setCounter(counter);
		return counter;
	}

	public Booking getBooking4AdventureId(final String adventureId) {
		return getRoomSet().stream().flatMap(r -> r.getBookingSet().stream())
				.filter(b -> b.getAdventureId() != null && b.getAdventureId().equals(adventureId)).findFirst()
				.orElse(null);
	}

	public Collection<? extends Booking> getBookings4BulkId(final String bulkId) {
		return getRoomSet().stream().flatMap(r -> r.getBookingSet().stream())
				.filter(b -> b.getBulkId() != null && b.getBulkId().equals(bulkId)).collect(Collectors.toSet());
	}

	public Booking reserveRoom(final Room.Type type, final LocalDate arrival, final LocalDate departure, final String buyerNif,
			final String buyerIban, final String adventureId) {
		final Room room = hasVacancy(type, arrival, departure);
		if (room != null) {
			final Booking newBooking = room.reserve(type, arrival, departure, buyerNif, buyerIban);
			newBooking.setAdventureId(adventureId);

			return newBooking;
		}

		throw new HotelException();

	}

}

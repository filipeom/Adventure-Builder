package pt.ulisboa.tecnico.softeng.hotel.services.local;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.joda.time.LocalDate;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;
import pt.ulisboa.tecnico.softeng.hotel.domain.Booking;
import pt.ulisboa.tecnico.softeng.hotel.domain.Hotel;
import pt.ulisboa.tecnico.softeng.hotel.domain.Room;
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;
import pt.ulisboa.tecnico.softeng.hotel.services.local.dataobjects.HotelData;
import pt.ulisboa.tecnico.softeng.hotel.services.local.dataobjects.RoomBookingData;
import pt.ulisboa.tecnico.softeng.hotel.services.local.dataobjects.RoomData;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.dataobjects.RestRoomBookingData;

public class HotelInterface {

	@Atomic(mode = TxMode.READ)
	public List<HotelData> getHotels() {
		return FenixFramework.getDomainRoot().getHotelSet().stream().map(h -> new HotelData(h))
				.collect(Collectors.toList());
	}

	@Atomic(mode = TxMode.WRITE)
	public void createHotel(final HotelData hotelData) {
		new Hotel(hotelData.getCode(), hotelData.getName(), hotelData.getNif(), hotelData.getIban(),
				hotelData.getPriceSingle(), hotelData.getPriceDouble());
	}

	@Atomic(mode = TxMode.READ)
	public HotelData getHotelDataByCode(final String code) {
		final Hotel hotel = getHotelByCode(code);

		if (hotel != null) {
			return new HotelData(hotel);
		}

		return null;
	}

	@Atomic(mode = TxMode.WRITE)
	public void createRoom(final String hotelCode, final RoomData roomData) {
		new Room(getHotelByCode(hotelCode), roomData.getNumber(), roomData.getType());
	}

	@Atomic(mode = TxMode.READ)
	public RoomData getRoomDataByNumber(final String code, final String number) {
		final Room room = getRoomByNumber(code, number);
		if (room == null) {
			return null;
		}

		return new RoomData(room);
	}

	@Atomic(mode = TxMode.WRITE)
	public void createBooking(final String code, final String number, final RoomBookingData booking) {
		final Room room = getRoomByNumber(code, number);
		if (room == null) {
			throw new HotelException();
		}

		new Booking(room, booking.getArrival(), booking.getDeparture(), booking.getBuyerNif(), booking.getBuyerIban());
	}

	@Atomic(mode = TxMode.WRITE)
	public RestRoomBookingData reserveRoom(final RestRoomBookingData roomBookingData) {
		final Booking booking = getBooking4AdventureId(roomBookingData.getAdventureId());
		if (booking != null) {
			return new RestRoomBookingData(booking);
		}

		final Room.Type type = roomBookingData.getRoomType().equals("SINGLE") ? Room.Type.SINGLE : Room.Type.DOUBLE;

		for (final Hotel hotel : FenixFramework.getDomainRoot().getHotelSet()) {
			return new RestRoomBookingData(hotel.reserveRoom(type, roomBookingData.getArrival(),
					roomBookingData.getDeparture(), roomBookingData.getBuyerNif(), roomBookingData.getBuyerIban(),
					roomBookingData.getAdventureId()));
		}
		throw new HotelException();
	}

	@Atomic(mode = TxMode.WRITE)
	public String cancelBooking(final String reference) {
		for (final Hotel hotel : FenixFramework.getDomainRoot().getHotelSet()) {
			final Booking booking = hotel.getBooking(reference);
			if (booking != null && booking.getCancellation() != null) {
				return booking.getCancellation();
			} else if (booking != null && booking.getCancellation() == null) {
				return booking.cancel();
			}
		}
		throw new HotelException();
	}

	@Atomic(mode = TxMode.READ)
	public RestRoomBookingData getRoomBookingData(final String reference) {
		for (final Hotel hotel : FenixFramework.getDomainRoot().getHotelSet()) {
			for (final Room room : hotel.getRoomSet()) {
				final Booking booking = room.getBooking(reference);
				if (booking != null) {
					return new RestRoomBookingData(booking);
				}
			}
		}
		throw new HotelException();
	}

	@Atomic(mode = TxMode.WRITE)
	public Set<String> bulkBooking(final int number, final LocalDate arrival, final LocalDate departure, final String buyerNif, final String buyerIban, final String bulkId) {
		final Set<Booking> bookings = getBookings4BulkId(bulkId);
		if (!bookings.isEmpty()) {
			return bookings.stream().map(b -> b.getReference()).collect(Collectors.toSet());
		}

		if (number < 1) {
			throw new HotelException();
		}

		final List<Room> rooms = getAvailableRooms(number, arrival, departure);
		if (rooms.size() < number) {
			throw new HotelException();
		}

		final Set<String> references = new HashSet<>();
		for (int i = 0; i < number; i++) {
			final Booking booking = rooms.get(i).reserve(rooms.get(i).getType(), arrival, departure, buyerNif, buyerIban);
			booking.setBulkId(bulkId);
			references.add(booking.getReference());
		}

		return references;
	}

	@Atomic(mode = TxMode.WRITE)
	public void deleteHotels() {
		FenixFramework.getDomainRoot().getHotelSet().stream().forEach(h -> h.delete());
	}

	List<Room> getAvailableRooms(final int number, final LocalDate arrival, final LocalDate departure) {
		final List<Room> availableRooms = new ArrayList<>();
		for (final Hotel hotel : FenixFramework.getDomainRoot().getHotelSet()) {
			availableRooms.addAll(hotel.getAvailableRooms(arrival, departure));
			if (availableRooms.size() >= number) {
				return availableRooms;
			}
		}
		return availableRooms;
	}

	private Hotel getHotelByCode(final String code) {
		return FenixFramework.getDomainRoot().getHotelSet().stream().filter(h -> h.getCode().equals(code)).findFirst()
				.orElse(null);
	}

	private Room getRoomByNumber(final String code, final String number) {
		final Hotel hotel = getHotelByCode(code);
		if (hotel == null) {
			return null;
		}

		final Room room = hotel.getRoomByNumber(number);
		if (room == null) {
			return null;
		}
		return room;
	}

	private Booking getBooking4AdventureId(final String adventureId) {
		for (final Hotel hotel : FenixFramework.getDomainRoot().getHotelSet()) {
			final Booking booking = hotel.getBooking4AdventureId(adventureId);
			if (booking != null) {
				return booking;
			}
		}
		return null;
	}

	private Set<Booking> getBookings4BulkId(final String bulkId) {
		final Set<Booking> bookings = new HashSet<Booking>();
		for (final Hotel hotel : FenixFramework.getDomainRoot().getHotelSet()) {
			bookings.addAll(hotel.getBookings4BulkId(bulkId));
		}
		return bookings;
	}

}

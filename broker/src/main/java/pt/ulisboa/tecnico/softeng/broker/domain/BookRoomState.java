package pt.ulisboa.tecnico.softeng.broker.domain;

import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State;
import pt.ulisboa.tecnico.softeng.broker.domain.BulkRoomBooking;
import pt.ulisboa.tecnico.softeng.broker.exception.BrokerException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface.Type;
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRoomBookingData;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException;

public class BookRoomState extends BookRoomState_Base {
  public static final int MAX_REMOTE_ERRORS = 10;

  @Override
  public State getValue() {
    return State.BOOK_ROOM;
  }

  @Override
  public void process() {
    RestRoomBookingData newRoomData;
    Type roomType;

    try {
      /* getBulkRoomBooking throws BrokerException when, (a) there are no available 
       * BulkRoomBookings in the date interval. (b) the BulkRoomBooking doesn't 
       * contain any rooms, i.e., NO references */ 
      BulkRoomBooking roomBulkBooking = getAdventure().getBroker().getBulkRoomBooking(
          getAdventure().getBegin(), getAdventure().getEnd());

      String reference = roomBulkBooking.getReferences().stream().findFirst().get();
      RestRoomBookingData roomData = getAdventure().getBroker().getHotelInterface().getRoomBookingData(reference);

      /* Get the room type */
      roomType = roomData.getRoomType().equals(Type.SINGLE.toString()) ? Type.SINGLE : Type.DOUBLE;

      /* Create a RestRoomBookingData with the correct date */
      newRoomData = new RestRoomBookingData(roomType, getAdventure().getBegin(),
          getAdventure().getEnd(), roomData.getBuyerNif(), roomData.getBuyerIban(), roomData.getAdventureId());

    } catch (HotelException | RemoteAccessException | BrokerException e) {
      /* catch and ignore, reserve a room directly in the hotel */ 
      newRoomData = new RestRoomBookingData(Type.SINGLE,
          getAdventure().getBegin(), getAdventure().getEnd(), getAdventure().getBroker().getNifAsBuyer(),
          getAdventure().getBroker().getIban(), getAdventure().getID());
    }

    try {
      RestRoomBookingData bookingData = getAdventure().getBroker().getHotelInterface().reserveRoom(newRoomData);
      getAdventure().setRoomConfirmation(bookingData.getReference());
      getAdventure().incAmountToPay(bookingData.getPrice());
    } catch (HotelException he) {
      getAdventure().setState(State.UNDO);
      return;
    } catch (RemoteAccessException rae) {
      incNumOfRemoteErrors();
      if (getNumOfRemoteErrors() == MAX_REMOTE_ERRORS) {
        getAdventure().setState(State.UNDO);
      }
      return;
    }

    if (getAdventure().shouldRentVehicle()) {
      getAdventure().setState(State.RENT_VEHICLE);
    } else {
      getAdventure().setState(State.PROCESS_PAYMENT);
    }
  }

}

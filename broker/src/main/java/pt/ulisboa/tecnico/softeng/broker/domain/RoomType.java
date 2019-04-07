package pt.ulisboa.tecnico.softeng.broker.domain;

import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface.Type;

public class RoomType extends RoomType_Base {

  public RoomType(Adventure adventure, Type type) {
    setAdventure(adventure);
    setType(type);
  }

  public void delete() {
    setAdventure(null);

    deleteDomainObject();
  }
}

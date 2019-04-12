package pt.ulisboa.tecnico.softeng.broker.domain;

import pt.ulisboa.tecnico.softeng.broker.services.remote.CarInterface.Type;
public class Vehicle extends Vehicle_Base {
    
    public Vehicle(Adventure adventure, Type type) {
        setAdventure(adventure);
        setType(type);
    }

    public void delete() {
        setAdventure(null);

        deleteDomainObject();
    }
}

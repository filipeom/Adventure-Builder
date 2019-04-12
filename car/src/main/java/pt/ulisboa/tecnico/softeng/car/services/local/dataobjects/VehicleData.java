package pt.ulisboa.tecnico.softeng.car.services.local.dataobjects;

import pt.ulisboa.tecnico.softeng.car.domain.Vehicle;

public class VehicleData {
    private Vehicle.Type type;
    private String plate;
    private Integer kilometers;
    private Double price;
    private RentACarData rentacar;

    public VehicleData() { }

    public VehicleData(Vehicle.Type type, String plate, int kilometers, long price, RentACarData rentacar) {
        this.type = type;
        this.plate = plate;
        this.kilometers = kilometers;
        this.price = (double) (Math.round((price / 1000.0) * 1000) / 1000);
        this.rentacar = rentacar;
    }

    public Vehicle.Type getType() {
        return type;
    }

    public void setType(Vehicle.Type type) {
        this.type = type;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public Integer getKilometers() {
        return kilometers;
    }

    public void setKilometers(Integer kilometers) {
        this.kilometers = kilometers;
    }

    public Long getPrice() {
        return price.longValue() * 1000;
    }

    public void setPrice(long price) {
        this.price = (double) (Math.round((price / 1000.0) * 1000) / 1000);
    }

    public RentACarData getRentacar() {
        return rentacar;
    }

    public void setRentACar(RentACarData rentACar) {
        this.rentacar = rentACar;
    }
}

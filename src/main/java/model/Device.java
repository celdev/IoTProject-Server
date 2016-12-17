package model;

public class Device extends IoTUnit {

    private State deviceState;
    private String name;

    public Device(int id, State deviceState, String name) {
        super(id);
        this.deviceState = deviceState;
        this.name = name;
    }

    @Override
    public String toString() {
        return "D%" + getId() + "%" + deviceState + "%" + name;
    }

    @Override
    public void copyInfoToThis(IoTUnit unit) {
        if (unit instanceof Device) {
            Device device = (Device) unit;
            deviceState = device.deviceState;
            name = device.name;
        }
    }

}

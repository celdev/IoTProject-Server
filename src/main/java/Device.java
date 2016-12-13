public class Device extends IoTUnit implements Switchable {

    private State deviceState;
    private String name;

    public Device(int id, State deviceState, String name) {
        super(id);
        this.deviceState = deviceState;
        this.name = name;
    }

    @Override
    public State getDeviceState() {
        return null;
    }

    @Override
    public String toString() {
        return "D%" + getId() + "%" + deviceState + "%" + name;
    }

    @Override
    void copyInfoToThis(IoTUnit unit) {
        if (unit instanceof Device) {
            Device device = (Device) unit;
            deviceState = device.deviceState;
            name = device.name;
        }
    }

}

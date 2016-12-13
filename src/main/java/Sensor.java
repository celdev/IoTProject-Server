import java.util.Arrays;

public class Sensor extends IoTUnit{

    private static SensorHeader sensorHeader = SensorHeader.getInstance();

    private String[] sensorValues;

    public Sensor(int id, String[] sensorValues) {
        super(id);
        this.sensorValues = sensorValues;
    }

    public String getValueOfField(SensorHeader.SensorField sensorField) {
        int columnIndex = SensorHeader.SensorField.sensorFieldToColumnIndex(sensorField);
        if (columnIndex == -1) {
            return "error";
        }
        return sensorValues[columnIndex];
    }

    @Override
    void copyInfoToThis(IoTUnit unit) {
        if (unit instanceof Sensor) {
            sensorValues = ((Sensor) unit).sensorValues;
        }
    }



    @Override
    public String toString() {
        return "S%" + getId() + "%" + Arrays.toString(sensorValues);
    }
}

package infohandlers;

import executors.ActionExecutor;
import model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class IoTSensorDeviceHandler {

    private List<Device> devices;
    private List<Sensor> sensors;

    private static final Action GET_ACTION = new Action();

    private static final long MS_BETWEEN_UPDATE = 30 * 1000;

    private static IoTSensorDeviceHandler instance;
    private GetInfoThread getInfoThread;

    private IoTSensorDeviceHandler() {
        getInfoThread = new GetInfoThread();
        getInfoThread.start();
    }

    public static IoTSensorDeviceHandler getInstance() {
        if (instance == null) {
            instance = new IoTSensorDeviceHandler();
        }
        return instance;
    }

    private void getInfo() {
        try {
            String result = ActionExecutor.getInstance().executeAction(GET_ACTION);
            parseDevice(result);
            parseSensor(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateDevices(List<Device> newInfo) {
        for (Device device : newInfo) {
            int index = devices.indexOf(device);
            if (index != -1) {
                devices.get(index).copyInfoToThis(device);
            } else {
                devices.add(device);
            }
        }
    }

    private void updateSensors(List<Sensor> newInfo) {
        for (Sensor sensor : newInfo) {
            int index = sensors.indexOf(sensor);
            if (index != -1) {
                sensors.get(index).copyInfoToThis(sensor);
            } else {
                sensors.add(sensor);
            }
        }
    }

    public String getDeviceAndSensorInfo() {
        getInfo();
        StringBuilder builder = new StringBuilder();
        devices.forEach(device -> builder.append(device.toString()).append("\n"));
        builder.append("P%").append(SensorHeader.getInstance().getHeaders()).append("\n");
        sensors.forEach(sensor -> builder.append(sensor.toString()).append("\n"));
        builder.deleteCharAt(builder.length() -1);
        return builder.toString();
    }

    private int parseNumberOfDevice(String line) {
        String intParse = line.substring(line.lastIndexOf(" ")).trim();
        return Integer.parseInt(intParse);
    }

    private void parseDevice(String result) throws Exception {
        String[] devicePart = result.split("SENSORS:");
        String[] lines = devicePart[0].split("\n");
        int numberOfDevice = parseNumberOfDevice(lines[0]);
        List<Device> tempList = new ArrayList<>(numberOfDevice);
        for(int i = 1; i <= numberOfDevice; i++) {
            tempList.add(extractDevice(lines[i]));
        }
        if (devices != null) {
            updateDevices(tempList);
        } else {
            devices = new CopyOnWriteArrayList<>(tempList);
        }
    }

    private Device extractDevice(String line) throws Exception {
        String[] cols = line.split("\t");
        int id = Integer.parseInt(cols[0]);
        return new Device(id, State.stringToState(cols[2]), cols[1]);
    }

    private Sensor extractSensor(String line) throws NumberFormatException {
        String[] cols = line.split("\t");
        for(int i = 0; i < cols.length; i++) {
            cols[i] = cols[i].trim();
        }
        int id = Integer.parseInt(cols[2]);
        return new Sensor(id, cols);
    }

    public String getTemperature() {
        if (sensors == null) {
            getInfo();
        }
        for (Sensor sensor : sensors) {
            if (sensor.getId() == 135) {
                return sensor.getValueOfField(SensorHeader.SensorField.TEMP);
            }
        }
        return null;
    }

    private void parseSensor(String result) throws Exception {
        String sensorPart = result.split("SENSORS:")[1];
        String[] lines = sensorPart.split("\n");
        int numberOfSensors = lines.length - 3;
        List<Sensor> tempSensors = new ArrayList<>(numberOfSensors);
        for(int i = 3; i < 3 + numberOfSensors; i++) {
            tempSensors.add(extractSensor(lines[i]));
        }
        if (sensors == null) {
            sensors = new CopyOnWriteArrayList<>(tempSensors);
        } else {
            updateSensors(tempSensors);
        }
    }


    private class GetInfoThread extends Thread {

        private boolean alive;

        @Override
        public void run() {
            alive = true;
            while (alive) {
                getInfo();
                try {
                    sleep(MS_BETWEEN_UPDATE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void kill() {
            alive = false;
        }
    }
}

package infohandlers;

public class IoTInformationHandler {

    private static IoTInformationHandler instance;
    private static final ThreadHandler threadHandler = ThreadHandler.getInstance();

    private static final IoTSensorDeviceHandler iotSensorDeviceHandler = IoTSensorDeviceHandler.getInstance();

    private IoTInformationHandler() {
    }

    public static IoTInformationHandler getInstance() {
        if (instance == null) {
            instance = new IoTInformationHandler();
        }
        return instance;
    }

    public String getInfo() {
        return iotSensorDeviceHandler.getDeviceAndSensorInfo() + "\n" +
                threadHandler.getAllThreads();
    }

}

class IoTInformationHandler {

    private static IoTInformationHandler instance;
    private static final ThreadHandler threadHandler = ThreadHandler.getInstance();

    private static final IoTSensorDeviceHandler iotSensorDeviceHandler = IoTSensorDeviceHandler.getInstance();

    private IoTInformationHandler() {
    }

    static IoTInformationHandler getInstance() {
        if (instance == null) {
            instance = new IoTInformationHandler();
        }
        return instance;
    }

    String getInfo() {
        return iotSensorDeviceHandler.getDeviceAndSensorInfo() + "\n" +
                threadHandler.getAllThreads();
    }

    /*public static void main(String[] args) {
        TestClient.password = args[0];
        IoTInformationHandler ioTInformationHandler = getInstance();
        iotSensorDeviceHandler.getInfo();
        threadHandler.addNewConditionThread(new Condition("tråd1", new Action()) {
            @Override
            public boolean conditionIsTrue() {
                return false;
            }
        });
        final long timeNow = System.currentTimeMillis();
        threadHandler.addNewConditionThread(new Condition("om tråden är 2,", new Action(1,State.OFF)) {
            @Override
            public boolean conditionIsTrue() {
                if ((timeNow + 15000) < System.currentTimeMillis()) {
                    return true;
                }
                return false;
            }
        }, new TestClient());
        //System.out.println("\n\n\n\n\n\n\n");

        //System.out.println("====all information=====");
        //System.out.println(ioTInformationHandler.getInfo());
        //System.out.println("====all information=====");
    }*/


}

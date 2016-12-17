public class Action {

    private static final String TOOL_NAME = "tdtool";
    private final String command;

    public Action(Device device, State state) {
        this.command = TOOL_NAME + " " + state.getArgument() + " " + device.getId();
    }

    Action(int deviceID, State state) {
        this.command = TOOL_NAME + " " + state.getArgument() + " " + deviceID;
    }

    Action() {
        this.command = TOOL_NAME + " -l";
    }

    String getCommand() {
        return command;
    }
}

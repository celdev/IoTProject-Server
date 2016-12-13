public class Action {

    public static final String TOOL_NAME = "tdtool";
    private String command;

    public Action(Device device, State state) {
        this.command = TOOL_NAME + " " + state.getArgument() + " " + device.getId();
    }

    public Action(int deviceID, State state) {
        this.command = TOOL_NAME + " " + state.getArgument() + " " + deviceID;
    }

    public Action() {
        this.command = TOOL_NAME + " -l";
    }

    public String getCommand() {
        return command;
    }
}

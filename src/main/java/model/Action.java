package model;

public class Action {

    private static final String TOOL_NAME = "tdtool";
    private final String command;


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

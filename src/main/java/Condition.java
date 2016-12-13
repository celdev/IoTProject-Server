abstract class Condition {

    private String command;
    private Action action;

    public Condition(String command, Action action) {
        this.command = "[" + command + "]";
        this.action = action;
    }

    public Action getAction() {
        return action;
    }

    public String getCommand() {
        return command;
    }

    public abstract boolean conditionIsTrue();

}

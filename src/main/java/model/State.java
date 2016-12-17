package model;

public enum State {

    ON("-n"),OFF("-f");

    private final String argument;

    State(String argument) {
        this.argument = argument;
    }

    public String getArgument() {
        return argument;
    }

    public static State stringToState(String str) throws Exception{
        str = str.toLowerCase();
        if (str.equals("on")) {
            return ON;
        } else if (str.equals("off")) {
            return OFF;
        }
        throw new Exception("Incorrect state conversion: " + str);
    }
}

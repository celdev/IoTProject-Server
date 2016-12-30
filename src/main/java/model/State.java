package model;

/** This enum represents the state that the IoT devices can have
 *  It's either ON or OFF
 *
 *  each of the values in the enum have a argument which is the
 *  argument used in tdtool to make a device get that state
 *
 *  -n DEVICE_ID will make the devide turn on
 *
 * */
public enum State {

    ON("-n"),OFF("-f");

    private final String argument;

    State(String argument) {
        this.argument = argument;
    }

    public String getArgument() {
        return argument;
    }

    /** Returns ON or OFF depending on the String passed as a parameter
     *  if the parameter isn't ON or OFF (case insensitive)
     *  an exception will be thrown
     * */
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

package util;

/** This class contains helper methods for extracting
 *  information from Strings
 * */
public class InformationExtractor {

    /** Extracts and returns an integer value from the parameter
     * */
    public static int extractTemperature(String command) throws IllegalArgumentException{
        String intValue = command.replaceAll("[^0-9]", "");
        try {
            return Integer.parseInt(intValue);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
    }

    /** Extracts and returns an integer value from the parameter which have the form of i.e. "22.4°c"
     * */
    public static int extractTemperatureFromTemperatureString(String temperature) throws NumberFormatException {
        return Integer.parseInt(temperature.substring(0, temperature.indexOf(".")));
    }
}

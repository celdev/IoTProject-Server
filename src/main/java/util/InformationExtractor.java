package util;

public class InformationExtractor {


    public static int extractTemperature(String command) throws IllegalArgumentException{
        String intValue = command.replaceAll("[^0-9]", "");
        try {
            return Integer.parseInt(intValue);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
    }

    public static int extractTemperatureFromTemperatureString(String temperature) throws NumberFormatException {
        return Integer.parseInt(temperature.substring(0, temperature.indexOf(".")));
    }
}

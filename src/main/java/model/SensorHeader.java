package model;

import java.util.Arrays;
import java.util.stream.Stream;

/** This class represents the "head" row of the sensor information from the
 *  tdtool
 *
 *  The different header values are hard coded in an enum
 * */
public class SensorHeader {

    private static SensorHeader instance;

    private final String[] headers;

    private SensorHeader() {
        headers = SensorField.getHeaders();
    }

    public static SensorHeader getInstance() {
        if (instance == null) {
            instance = new SensorHeader();
        }
        return instance;
    }

    /** Returns the headers
     * */
    public String getHeaders() {
        return Arrays.toString(headers);
    }

    public enum SensorField {

        PROTOCOL("PROTOCOL"),
        MODEL("MODEL"),
        ID("ID"),
        TEMP("TEMP"),
        HUMIDITY("HUMIDITY"),
        RAIN("RAIN"),
        WIND("WIND"),
        LASTUPDATED("LAST_UPDATED");

        private final String head;

        SensorField(String head) {
            this.head = head;
        }

        /** Converts a SensorField into an index
         *  i.e. protocol = index 0
         * */
        public static int sensorFieldToColumnIndex(SensorField sensorField) {
            for(int i = 0; i < values().length; i++) {
                if (sensorField.equals(values()[i])) {
                    return i;
                }
            }
            return -1;
        }

        /** Converts the head field of the enums into an array of String
         * */
        private static String[] getHeaders() {
            return Stream.of(values()).map(sensorField -> sensorField.head).toArray(String[]::new);
        }
    }
}

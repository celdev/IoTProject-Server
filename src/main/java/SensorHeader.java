import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        private String head;

        SensorField(String head) {
            this.head = head;
        }

        public static int sensorFieldToColumnIndex(SensorField sensorField) {
            for(int i = 0; i < values().length; i++) {
                if (sensorField.equals(values()[i])) {
                    return i;
                }
            }
            return -1;
        }

        private static String[] getHeaders() {
            return Stream.of(values()).map(sensorField -> sensorField.head).toArray(String[]::new);
        }
    }
}

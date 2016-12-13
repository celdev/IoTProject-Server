public class SensorCondition extends Condition {

    public SensorCondition(String command, Action action) {
        super(command, action);
    }

    @Override
    public boolean conditionIsTrue() {
        return false;
    }
}

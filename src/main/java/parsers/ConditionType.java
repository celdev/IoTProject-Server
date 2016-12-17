package parsers;

public enum ConditionType {
    LESS_THAN, EQUAL, GREATER_THAN;

    boolean compare(int now, int target) {
        switch (this) {
            case EQUAL:
                return now == target;
            case LESS_THAN:
                return now < target;
            case GREATER_THAN:
                return now > target;
        }
        return true;
    }
}

package parsers;

/** The different types of conditions that may be made
 *  are less than, equal or greater than
 *
 *  This enum holds the representation of those comparisons
 *  and holds a method for comparing two values
 *  depending on the enum used
 * */
public enum ComparisionType {
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

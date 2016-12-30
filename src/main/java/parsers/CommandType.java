package parsers;

/** The different command types that may be made
 *  Simple are commands like "turn off the lights"
 *  Condition are commands like "turn off the lights when it's less than 20 degrees"
 * */
enum CommandType {
    SIMPLE, CONDITION
}

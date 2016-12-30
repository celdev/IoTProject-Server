package parsers;

import executors.ActionExecutor;
import infohandlers.IoTSensorDeviceHandler;
import infohandlers.ThreadHandler;
import model.Action;
import model.Condition;
import model.State;
import util.Constants;
import util.InformationExtractor;

import java.util.Optional;

/** This class contains a implementation of the CommandParserInterface
 *
 *  The parsing is done by defining a number of words i.e.
 *      * the word/words for "turn off", "turn on"
 *      * the name of the devices
 *      * the words for the comparison words (less than, equal, greater than)
 *      ...
 *
 *  The specification of these words are made in the Builder-class
 *  There's are several ways to make sure all the required methods are called in the Builder
 *  The chosen one is to return null (in an Optional wrapper) since it fits the scope
 *  of this project, a "Step-builder" would have been more suitable for this task though.
 *
 *  if contains a large amount of methods for parsing the contents of the command
 *  sent as a parameter to the parseCommand-method
 *
 *  These method use the words stored in the instance variables provided
 *  by the Builder
 *
 * */
public class CommandParser implements CommandParserInterface {

    private final String[] conditionWords;
    private final String[] onWords;
    private final String[] offWords;

    private final String nameOfUnit1;
    private final String nameOfUnit2;
    private final String ifWord;

    private final String lessThanWord;
    private final String greaterThanWord;
    private final String equalWord;

    /** Constructor which takes a Builder as a parameter
     * */
    CommandParser(Builder builder) {
        if (builder == null) {
            throw new IllegalArgumentException("Can't build with a null Builder");
        }
        this.conditionWords = builder.bConditionWords;
        this.onWords = builder.bOnWords;
        this.offWords = builder.bOffWords;
        this.nameOfUnit1 = builder.bNameOfUnit1;
        this.nameOfUnit2 = builder.bNameOfUnit2;
        this.ifWord = builder.bIfWord;
        this.lessThanWord = builder.bLessThanWord;
        this.greaterThanWord = builder.bGreaterThanWord;
        this.equalWord = builder.bEqualWord;
    }


    /** Extracts the command type (if the command is simple or if it has a condition, i.e. contains "when")
     * */
    private CommandType extractCommandType(String command) {
        for (String s : conditionWords) {
            if (command.contains(s)) {
                return CommandType.CONDITION;
            }
        }
        return CommandType.SIMPLE;
    }

    /** This method parses and executes a simple commands (a command without a condition)
     *
     *  extracts
     *      * the state of the action (i.e. "TURN OFF the lamp")
     *      * the device to set the state of
     *
     *  if successful OK_RESPONSE will be returned after the action is executed
     *
     *  if an error occurs during the execution of the action or during parsing
     *  an exception will be thrown and the ERROR_RESPONSE will be returned
     * */
    private String parseSimpleCommand(String command) {
        State state = extractState(command);
        if (state != null) {
            try {
                int deviceID = extractDeviceID(command);
                try {
                    System.out.println("executed: " + ActionExecutor.getInstance().executeAction(new Action(deviceID, state)));
                    return Constants.OK_RESPONSE;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Error couldn't extract device from command");
                return Constants.ERROR_RESPONSE;
            }
        }
        return Constants.ERROR_RESPONSE;
    }

    /** Extracts the ID of the device
     *  the ID values are hardcoded in the telldus config file
     *  the String stored in nameOfUnit1 = IoT device id = 1
     *                    in nameOfUnit2 = IoT device id = 2
     *
     *  throws an exception if neither of the words in nameOfUnit1 or 2
     *  is contained in the command
     * */
    private int extractDeviceID(String command) throws IllegalArgumentException {
        if (command.contains(nameOfUnit1)) {
            return 1;
        } else if (command.contains(nameOfUnit2)) {
            return 2;
        }
        throw new IllegalArgumentException();
    }

    /** Extracts which state the command wants a unit to be in after the action is executed
     *  can be ON or OFF (i.e. "turn on the lights" will extract the state "ON"
     *
     *  some language can have multiple words for "turn on"/"turn off"
     *  i.e. "släck lampan" and "stäng av lampan" = STATE.OFF
     * */
    private State extractState(String command) {
        for (String on : onWords) {
            if (command.contains(on)) {
                return State.ON;
            }
        }
        for (String off : offWords) {
            if (command.contains(off)) {
                return State.OFF;
            }
        }
        return null;
    }

    /** Tries to parse a condition command
     *  Tries to extract
     *      * the wanted state (on/off)
     *      * the device to do something with
     *      * the condition
     *  if the condition is "if" then the condition will be tried and
     *  the action will be executed if true
     *  if the condition is "when" then the condition will be used to create a
     *  ConditionThread in which the condition will be checked until it's true
     *  in which case the action will be executed
     *
     *  if anything goes wrong during parsing the ERROR_RESPONSE constant will be returned
     * */
    private String parseConditionCommand(String command) {
        State state = extractState(command);
        if (state != null) {
            try {
                int deviceID = extractDeviceID(command);
                int targetTemperature = InformationExtractor.extractTemperature(command);
                ComparisionType condition = extractCondition(command);
                if (command.contains(ifWord)) {
                    return runIfCommand(deviceID,targetTemperature,condition, state);
                } else {
                    Condition condition1 = new Condition(command, new Action(deviceID, state)) {
                        @Override
                        public boolean conditionIsTrue() {
                            String temperature = IoTSensorDeviceHandler.getInstance().getTemperature();
                            if (temperature != null) {
                                int intTemperature = InformationExtractor.extractTemperatureFromTemperatureString(temperature);
                                if (condition.compare(intTemperature, targetTemperature)) {
                                    return true;
                                }
                            }
                            return false;
                        }
                    };
                    ThreadHandler.getInstance().addNewConditionThread(condition1);
                    return "ok";
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Error: couldn't extract required information from the command");
            }
        } else {
            System.out.println("Error: couldn't extract state from command");
        }
        return Constants.ERROR_RESPONSE;
    }

    /** Parses the command passed as a parameter
     *  If something goes wrong during parsing the ERROR_RESPONSE String will be returned
     * */
    @Override
    public String parseCommand(String command) {
        try {
            CommandType commandType = extractCommandType(command);
            if (commandType == null) {
                return Constants.ERROR_RESPONSE;
            }
            if (commandType.equals(CommandType.SIMPLE)) {
                return parseSimpleCommand(command);
            } else if (commandType.equals(CommandType.CONDITION)) {
                return parseConditionCommand(command);
            }
        } catch (Exception e) {
            System.out.println("Error: couldn't extract command type from command");
        }
        return Constants.ERROR_RESPONSE;
    }

    /** This method will check if the condition is true and execute the action of the command if it's true
     * */
    private String runIfCommand(int deviceID, int targetTemperature, ComparisionType comparisionType, State state) {
        String temperature = IoTSensorDeviceHandler.getInstance().getTemperature();
        if (temperature != null) {
            int intTemperature = InformationExtractor.extractTemperatureFromTemperatureString(temperature);
            if (comparisionType.compare(intTemperature, targetTemperature)) {
                try {
                    ActionExecutor.getInstance().executeAction(new Action(deviceID, state));
                    return "true";
                } catch (Exception e) {
                    e.printStackTrace();
                    return Constants.ERROR_RESPONSE;
                }
            }
        }
        return "false";
    }

    /** Extracts the condition (Comparision) type of the command
     *  if a condition type can't be extracted an exception is thrown
     * */
    private ComparisionType extractCondition(String command) throws IllegalArgumentException {
        if (command.contains(greaterThanWord)) {
            return ComparisionType.GREATER_THAN;
        } else if (command.contains(lessThanWord)) {
            return ComparisionType.LESS_THAN;
        } else if (command.contains(equalWord)) {
            return ComparisionType.EQUAL;
        }
        throw new IllegalArgumentException();
    }


    /** This static inner class is the Builder for this type of CommandParser
     *
     *  contains a large amount of variables that need to be set before a CommandParser
     *  successfully can be built.
     *
     *  The variables are set by calling all the "set"-methods of the Builder class
     * */
    static class Builder {

        private String[] bConditionWords;
        private String[] bOnWords;
        private String[] bOffWords;

        private String bNameOfUnit1;
        private String bNameOfUnit2;
        private String bIfWord;

        private String bLessThanWord;
        private String bGreaterThanWord;
        private String bEqualWord;

        Builder setConditionWords(String... conditionWords) {
            this.bConditionWords = conditionWords;
            return this;
        }

        Builder setOnWords(String... onWords) {
            this.bOnWords = onWords;
            return this;
        }

        Builder setOffWords(String... offWords) {
            this.bOffWords = offWords;
            return this;
        }

        Builder setNameOfUnits(String unit1, String unit2) {
            this.bNameOfUnit1 = unit1;
            this.bNameOfUnit2 = unit2;
            return this;
        }

        Builder setIfWord(String ifWord) {
            this.bIfWord = ifWord;
            return this;
        }

        Builder setComparisonWords(String lessThanWord, String greaterThanWord, String equalWord) {
            this.bLessThanWord = lessThanWord;
            this.bGreaterThanWord = greaterThanWord;
            this.bEqualWord = equalWord;
            return this;
        }

        /** Tries to build a CommandParser using this builder
         *  and wraps in in a Optional, if any of the variables are null
         *  null will be wrapped in the CommandParser
         * */
        Optional<CommandParser> build(){
            if (allIsSet()) {
                return Optional.of(new CommandParser(this));
            }
            return Optional.empty();
        }

        /** Returns false if any of the variables are null
         * */
        private boolean allIsSet() {
            return bConditionWords != null && bOnWords != null && bOffWords != null && bNameOfUnit1 != null &&
                    bNameOfUnit2 != null && bIfWord != null && bLessThanWord != null && bGreaterThanWord != null &&
                    bEqualWord != null;
        }


    }

}

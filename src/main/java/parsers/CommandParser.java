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

    public CommandParser(Builder builder) {
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


    private CommandType extractCommandType(String command) {
        for (String s : conditionWords) {
            if (command.contains(s)) {
                return CommandType.CONDITION;
            }
        }
        return CommandType.SIMPLE;
    }

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

    private int extractDeviceID(String command) throws IllegalArgumentException {
        if (command.contains(nameOfUnit1)) {
            return 1;
        } else if (command.contains(nameOfUnit2)) {
            return 2;
        }
        throw new IllegalArgumentException();
    }


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

    private String parseConditionCommand(String command) {
        State state = extractState(command);
        if (state != null) {
            try {
                int deviceID = extractDeviceID(command);
                int targetTemperature = InformationExtractor.extractTemperature(command);
                ConditionType condition = extractCondition(command);
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





    private String runIfCommand(int deviceID, int targetTemperature, ConditionType conditionType, State state) {
        String temperature = IoTSensorDeviceHandler.getInstance().getTemperature();
        if (temperature != null) {
            int intTemperature = InformationExtractor.extractTemperatureFromTemperatureString(temperature);
            if (conditionType.compare(intTemperature, targetTemperature)) {
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



    private ConditionType extractCondition(String command) throws IllegalArgumentException {
        if (command.contains(greaterThanWord)) {
            return ConditionType.GREATER_THAN;
        } else if (command.contains(lessThanWord)) {
            return ConditionType.LESS_THAN;
        } else if (command.contains(equalWord)) {
            return ConditionType.EQUAL;
        }
        throw new IllegalArgumentException();
    }


    public static class Builder {

        private String[] bConditionWords;
        private String[] bOnWords;
        private String[] bOffWords;

        private String bNameOfUnit1;
        private String bNameOfUnit2;
        private String bIfWord;

        private String bLessThanWord;
        private String bGreaterThanWord;
        private String bEqualWord;

        public Builder setConditionWords(String... conditionWords) {
            this.bConditionWords = conditionWords;
            return this;
        }

        public Builder setOnWords(String... onWords) {
            this.bOnWords = onWords;
            return this;
        }

        public Builder setOffWords(String... offWords) {
            this.bOffWords = offWords;
            return this;
        }

        public Builder setNameOfUnits(String unit1, String unit2) {
            this.bNameOfUnit1 = unit1;
            this.bNameOfUnit2 = unit2;
            return this;
        }

        public Builder setIfWord(String ifWord) {
            this.bIfWord = ifWord;
            return this;
        }

        public Builder setEqualWords(String lessThanWord, String greaterThanWord, String equalWord) {
            this.bLessThanWord = lessThanWord;
            this.bGreaterThanWord = greaterThanWord;
            this.bEqualWord = equalWord;
            return this;
        }

        public Optional<CommandParser> build(){
            if (allIsSet()) {
                return Optional.of(new CommandParser(this));
            }
            return Optional.empty();
        }

        private boolean allIsSet() {
            return bConditionWords != null && bOnWords != null && bOffWords != null && bNameOfUnit1 != null &&
                    bNameOfUnit2 != null && bIfWord != null && bLessThanWord != null && bGreaterThanWord != null &&
                    bEqualWord != null;
        }


    }

}

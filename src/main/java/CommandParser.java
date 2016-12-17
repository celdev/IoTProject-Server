public class CommandParser {

	private static CommandParser instance;

	private String[] conditionWords = {" när ", " om "};
    private String[] onWord = {"sätt på", "tänd"};
    private String[] offWord = {"stäng av", "släck"};

    private String nameOfUnit1 = "lampa";
    private String nameOfUnit2 = "värme";
    private String ifWord = " om ";

    private static final String lessThanWord = " mindre ";
    private static final String greaterThanWord = " mer ";
    private static final String equalWord = " är ";

	private CommandParser() {
	}

	public static CommandParser getInstance() {
		if (instance == null) {
			instance = new CommandParser();
		}
		return instance;
	}

	public String parseCommand(String command) {
		try {
			CommandType commandType = extractCommandType(command);
			if (commandType == null) {
				return Server.ERROR_RESPONSE;
			}
			if (commandType.equals(CommandType.SIMPLE)) {
				return parseSimpleCommand(command);
			} else if (commandType.equals(CommandType.CONDITION)) {
				return parseConditionCommand(command);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Server.ERROR_RESPONSE;
	}

    private String parseConditionCommand(String command) {
        State state = extractState(command);
        if (state != null) {
            try {
                int deviceID = extractDeviceID(command);
                int targetTemperature = extractTemperature(command);
                ConditionType condition = extractCondition(command);
                if (command.contains(ifWord)) {
                    return runIfCommand(deviceID,targetTemperature,condition, state);
                } else {
                    Condition condition1 = new Condition(command, new Action(deviceID, state)) {
                        @Override
                        public boolean conditionIsTrue() {
                            String temperature = IoTSensorDeviceHandler.getInstance().getTemperature();
                            if (temperature != null) {
                                int intTemperature = extractTemperatureFromString(temperature);
                                if (compare(intTemperature, targetTemperature, condition)) {
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
                e.printStackTrace();
            }
        }
        return Server.ERROR_RESPONSE;
    }

    private int extractTemperatureFromString(String temperature) throws NumberFormatException {
        return Integer.parseInt(temperature.substring(0, temperature.indexOf(".")));
    }


	private String runIfCommand(int deviceID, int targetTemperature, ConditionType conditionType, State state) {
		String temperature = IoTSensorDeviceHandler.getInstance().getTemperature();
		if (temperature != null) {
			int intTemperature = extractTemperatureFromString(temperature);
			if (compare(intTemperature, targetTemperature, conditionType)) {
				try {
					ActionExecutor.getInstance().executeAction(new Action(deviceID, state));
					return "true";
				} catch (Exception e) {
					e.printStackTrace();
					return Server.ERROR_RESPONSE;
				}
			}
		}
		return "false";
	}

    private boolean compare(int now, int target, ConditionType conditionType) {
        switch (conditionType) {
            case EQUAL:
                return now == target;
            case LESS_THAN:
                return now < target;
            case GREATER_THAN:
                return now > target;
        }
        return true;
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

    private int extractTemperature(String command) throws IllegalArgumentException{
        String intValue = command.replaceAll("[^0-9]", "");
        try {
            return Integer.parseInt(intValue);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
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
		for (String on : onWord) {
			if (command.contains(on)) {
				return State.ON;
			}
		}
		for (String off : offWord) {
			if (command.contains(off)) {
				return State.OFF;
			}
		}
		return null;
	}

	private String parseSimpleCommand(String command) {
		State state = extractState(command);
		if (state != null) {
			try {
				int deviceID = extractDeviceID(command);
				try {
                    System.out.println("executed: " + ActionExecutor.getInstance().executeAction(new Action(deviceID, state)));
                    return Server.OK_RESPONSE;
                } catch (Exception e) {
					e.printStackTrace();
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				return Server.ERROR_RESPONSE;
			}
		}
		return Server.ERROR_RESPONSE;
	}

	private CommandType extractCommandType(String command) {
		for (String s : conditionWords) {
			if (command.contains(s)) {
				return CommandType.CONDITION;
			}
		}
		return CommandType.SIMPLE;
	}

	private enum CommandType {
		SIMPLE, CONDITION
	}

	private enum ConditionType {
		LESS_THAN, EQUAL, GREATER_THAN;
	}

}

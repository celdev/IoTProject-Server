import java.nio.charset.Charset;

public class CommandParser {

	private static CommandParser instance;

	private String[] conditionWords = { " när ", " om " };
	private String[] onWord = { "sätt på", "tänd" };
	private String[] offWord = { "stäng av", "släck" };

	private String nameOfUnit1 = new String("lampa".getBytes(), Charset.defaultCharset());
	private String nameOfUnit2 = "värme";

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
			System.out.println("Trying to parse command = " + command);
			CommandType commandType = extractCommandType(command);
			System.out.println("Extracted command type = " + commandType);
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
				if (command.contains(" om ")) {
					return runIfCommand(deviceID, targetTemperature, condition, state);
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
		System.out.println("trying to parse int temperature from " + temperature + " it contains dot = "
				+ temperature.contains("."));
		return Integer.parseInt(temperature.substring(0, temperature.indexOf(".")));
	}

	public static void main(String[] args) {
		CommandParser commandParser = CommandParser.getInstance();
		System.out.println("c " + commandParser.extractTemperatureFromString("24.7°"));
	}

	private String runIfCommand(int deviceID, int targetTemperature, ConditionType conditionType, State state) {
		String temperature = IoTSensorDeviceHandler.getInstance().getTemperature();
		if (temperature != null) {
			int intTemperature = extractTemperatureFromString(temperature);
			if (compare(intTemperature, targetTemperature, conditionType)) {
				try {
					System.out.println("if condition was true, executing command");
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
		if (command.contains(" mer ")) {
			return ConditionType.GREATER_THAN;
		} else if (command.contains(" mindre ")) {
			return ConditionType.LESS_THAN;
		} else if (command.contains("är")) {
			return ConditionType.EQUAL;
		}
		throw new IllegalArgumentException();
	}

	private int extractTemperature(String command) throws IllegalArgumentException {
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
		System.out.println("Trying to extract state from " + command);
		// System.out.println("" + command + " contains " + onWord + " = " +
		// command.contains(onWord));
		// System.out.println("" + command + " contains " + offWord + " = " +
		// command.contains(offWord));
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
		System.out.println("Extracted state = " + state);
		if (state != null) {
			try {
				int deviceID = extractDeviceID(command);
				System.out.println("Extracted device id = " + deviceID);
				try {
					String response = ActionExecutor.getInstance().executeAction(new Action(deviceID, state));
					System.out.println("executed action and got response = " + response);
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

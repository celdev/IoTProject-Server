import infohandlers.IoTInformationHandler;
import parsers.*;
import spark.Request;
import spark.Response;
import util.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static spark.Spark.*;

public class Server {

    private Map<String,CommandParserInterface> commandParsers;

    public static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("file.encoding", "UTF-8");
        int port = 4091;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                port = 4091;
            }
        }
        Server server = new Server(port);
        server.setupRoutes();
    }

    private Server(int port) {
        port(port);
        createParsers();
    }

    private void createParsers() {
        commandParsers = new HashMap<>();
        Optional<CommandParser> thaiParserOpt = ThaiCommandParser.commandParserBuilder();
        thaiParserOpt.ifPresent(commandParser -> commandParsers.put("TH", commandParser));
        Optional<CommandParser> swedishParserOpt = SweCommandParser.commandParserBuilder();
        swedishParserOpt.ifPresent(commandParser -> commandParsers.put("SV", commandParser));
        Optional<CommandParser> englishParserOpt = EngCommandParser.commandParserBuilder();
        englishParserOpt.ifPresent(commandParser -> commandParsers.put("EN", commandParser));
    }

    private CommandParserInterface getCommandParserInterface(String lang) {
        switch (lang) {
            case "SV":
            case "sv":
                return commandParsers.get("SV");
            case "EN":
            case "en":
                return commandParsers.get("EN");
            case "TH":
            case "th":
                return commandParsers.get("TH");
            default:
                return commandParsers.get("SV");
        }
    }


    private void setupRoutes() {
        get("/*/command/*", this::processVoiceCommand);
        get("get", this::processGet);
        get("test", (req, res) -> "ok");
    }

    private Object processVoiceCommand(Request request, Response response) {
        String lang = request.splat()[0];
        String command = request.splat()[1];
        System.out.println("got command " + command);
        if (command == null || command.trim().isEmpty()) {
            return Constants.ERROR_RESPONSE;
        }
        response.type("text/plain");
        return getCommandParserInterface(lang).parseCommand(command.toLowerCase().replace("_", " ").trim());
    }

    private Object processGet(Request request, Response response) {
        response.type("text/plain");
        return IoTInformationHandler.getInstance().getInfo();
    }

}
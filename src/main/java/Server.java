import infohandlers.IoTInformationHandler;
import parsers.*;
import spark.Request;
import spark.Response;
import util.Constants;

import java.util.Optional;

import static spark.Spark.*;

public class Server {

    private CommandParserInterface commandParser;

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
        setParser("SE");
    }

    private void setParser(String language) {
        Optional<CommandParser> optional;
        switch (language) {
            case "TH":
                optional = ThaiCommandParser.commandParserBuilder();
                break;
            case "EN":
                optional = EngCommandParser.commandParserBuilder();
                break;
            case "SE":
                optional = SweCommandParser.commandParserBuilder();
                break;
            default:
                optional = SweCommandParser.commandParserBuilder();
        }
        if (optional.isPresent()) {
            commandParser = optional.get();
        } else {
            System.out.println("Couldn't create command parser of language " + language);
            stop();
        }
    }

    private void setupRoutes() {
        get("command/*", this::processVoiceCommand);
        get("get", this::processGet);
        get("test", (req, res) -> "ok");
    }

    private Object processVoiceCommand(Request request, Response response) {
        String command = request.splat()[0];
        System.out.println("got command " + command);
        if (command == null || command.trim().isEmpty()) {
            return Constants.ERROR_RESPONSE;
        }
        response.type("text/plain");
        return commandParser.parseCommand(command.toLowerCase().replace("_", " ").trim());
    }

    private Object processGet(Request request, Response response) {
        response.type("text/plain");
        return IoTInformationHandler.getInstance().getInfo();
    }

}
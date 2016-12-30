import infohandlers.IoTInformationHandler;
import parsers.*;
import spark.Request;
import spark.Response;
import util.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static spark.Spark.*;

/** This class contains the Spark Framework server
 *  Sets up the routes and commandparsers
 * */
public class Server {

    private Map<String,CommandParserInterface> commandParsers;

    /** Sets some properties needed to run the web server on the Raspberry PI
     *
     *  Starts the server on port 4091 and sets the routes
     * */
    public static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("file.encoding", "UTF-8");
        Server server = new Server(4091);
        server.setupRoutes();
    }

    /** Sets the port of the server and creates the
     *  command parsers
     * */
    private Server(int port) {
        port(port);
        createParsers();
    }

    /** Creates the command parsers
     *
     *  The Builders creates an Optional CommandParser since if the
     *  dev doesn't call all the required methods while building the parser the
     *  command parser will be null
     *
     *  Stores the parser in a map where a 2 letter language tag is the key and
     *  the command parser is the value
     * */
    private void createParsers() {
        commandParsers = new HashMap<>();
        Optional<CommandParser> thaiParserOpt = ThaiCommandParser.commandParserBuilder();
        thaiParserOpt.ifPresent(commandParser -> commandParsers.put("TH", commandParser));
        Optional<CommandParser> swedishParserOpt = SweCommandParser.commandParserBuilder();
        swedishParserOpt.ifPresent(commandParser -> commandParsers.put("SV", commandParser));
        Optional<CommandParser> englishParserOpt = EngCommandParser.commandParserBuilder();
        englishParserOpt.ifPresent(commandParser -> commandParsers.put("EN", commandParser));
    }

    /** Returns the CommandParser(Interface) for the language tag
     *  If the tag isn't a key in the command parsers map the swedish
     *  CommandParser will be returned
     * */
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

    /** Sets the three routes
     *  for sending commands
     *  getting the information
     *  test if the server is a IoT web server for this project
     * */
    private void setupRoutes() {
        get("/*/command/*", this::processVoiceCommand);
        get("get", this::processGet);
        //returns "ok"
        get("test", (req, res) -> "ok");
    }

    /** The handler for the command route
     *
     *  the first object in the splat will be the 2 letter language tag
     *  the second object in the splat will be the command
     *
     *  if the command isn't empty or null the server tries
     *  to parse the command
     * */
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

    /** The handler for the get route
     *  returns the information of the IoT devices and sensors
     *  and the running condition threads
     * */
    private Object processGet(Request request, Response response) {
        response.type("text/plain");
        return IoTInformationHandler.getInstance().getInfo();
    }

}
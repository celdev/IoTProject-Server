import spark.Request;
import spark.Response;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static spark.Spark.*;

public class Server {

    private CommandParser commandParser = CommandParser.getInstance();
    public static final String ERROR_RESPONSE = "error";

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
            return ERROR_RESPONSE;
        }
        response.type("text/plain");
        return commandParser.parseCommand(command.toLowerCase().replace("_", " ").trim());
    }

    private Object processGet(Request request, Response response) {
        response.type("text/plain");
        return IoTInformationHandler.getInstance().getInfo();
    }

}
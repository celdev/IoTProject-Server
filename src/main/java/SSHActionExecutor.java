import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import executors.ActionExecutorInterface;
import model.Action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/** This class contains an ActionExecutor that enables the web server
 *  to run locally and SSH-ing into the IoT-gateway to execute the commands
 *
 *  This makes it possible to avoid having to package and re-upload the jar file
 *  in every new build
 * */
class SSHActionExecutor implements ActionExecutorInterface {

    private static final String hostname = "INSERT_RASPBERRY_PI_IP";
    private static final String username = "RASPBERRY_PI_USERNAME";
    private static final String password = "RASPBERRY_PI_PASSWORD";


    @Override
    public String executeAction(Action action) throws Exception {
        return remoteCall(action);
    }

    /** Executes the action remotely by SSH-ing into the
     *  Raspberry PI using the hostname, username and password defined above
     * */
    private String remoteCall(Action action) {
        try {
            Connection conn = new Connection(hostname);
            conn.connect();
            Boolean isAuthenticated =
                    conn.authenticateWithPassword(username, password);
            if (!isAuthenticated) {
                //om de inte är det så händer ett IOException och vi returnerar sedan ""
                throw new IOException("Authentication failed.");
            }
            Session sess = conn.openSession();
            System.out.println("executing command " + action.getCommand());
            sess.execCommand(action.getCommand());
            InputStream stdout = new StreamGobbler(sess.getStdout());
            BufferedReader br = new BufferedReader(new
                    InputStreamReader(stdout)); //reads text
            StringBuilder stringBuilder = new StringBuilder();
            while (true) {
                String line = br.readLine(); // read line
                if (line == null) {
                    break;
                }
                stringBuilder.append(line).append("\n");
            }
            System.out.println("ExitCode: " + sess.getExitStatus());
            sess.close(); // Close this session
            conn.close();
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}

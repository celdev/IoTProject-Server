import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class TestClient implements ActionExecutorInterface {



    private static final String hostname = "81.230.190.13";
    private static final String username = "pi";
    private static String password = "";


    @Override
    public String executeAction(Action action) throws Exception {
        return remoteCall(action);
    }

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

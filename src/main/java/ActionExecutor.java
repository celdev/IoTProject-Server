

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ActionExecutor implements ActionExecutorInterface {

    private static ActionExecutor instance;

    private ActionExecutor() {

    }

    public static ActionExecutor getInstance() {
        if (instance == null) {
            instance = new ActionExecutor();
        }
        return instance;
    }

    public String executeAction(Action action) throws Exception{
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(action.getCommand());
            process.waitFor();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine())!= null) {
                output.append(line).append("\n");
            }
            return output.toString();

    }

}

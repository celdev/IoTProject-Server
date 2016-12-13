public class ConditionThread extends Thread {

    private static long idCounter = 0;

    private long id;
    private Condition condition;
    private boolean alive;
    private ThreadHandlerInterface threadHandlerInterface;
    private ActionExecutorInterface actionExecutor;

    public ConditionThread(Condition condition, ThreadHandlerInterface threadHandlerInterface) {
        this.id = idCounter++;
        this.condition = condition;
        this.threadHandlerInterface = threadHandlerInterface;
        actionExecutor = ActionExecutor.getInstance();
        start();
    }

    public ConditionThread(Condition condition, ThreadHandlerInterface threadHandlerInterface, ActionExecutorInterface actionExecutor) {
        this.id = idCounter++;
        this.condition = condition;
        this.threadHandlerInterface = threadHandlerInterface;
        this.actionExecutor = actionExecutor;
        start();
    }


    @Override
    public void run() {
        alive = true;
        while (alive) {
            if (condition.conditionIsTrue()) {
                try {
                    actionExecutor.executeAction(condition.getAction());
                } catch (Exception e) {
                    e.printStackTrace();
                    kill();
                }
                kill();
            }
            try {
                sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void kill() {
        threadHandlerInterface.removeDeadThread(this);
        alive = false;
    }

    @Override
    public String toString() {
        return "T%" + id + "%" + condition.getCommand() + "%" + alive;
    }
}

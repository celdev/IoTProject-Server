package infohandlers;

import executors.ActionExecutor;
import executors.ActionExecutorInterface;
import model.Condition;

class ConditionThread extends Thread {

    private static long idCounter = 0;

    private final Condition condition;
    private final ThreadHandlerInterface threadHandlerInterface;
    private final ActionExecutorInterface actionExecutor;

    private final long id;

    private boolean alive;

    ConditionThread(Condition condition, ThreadHandlerInterface threadHandlerInterface) {
        this.id = idCounter++;
        this.condition = condition;
        this.threadHandlerInterface = threadHandlerInterface;
        actionExecutor = ActionExecutor.getInstance();
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

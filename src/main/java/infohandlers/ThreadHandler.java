package infohandlers;

import executors.ActionExecutorInterface;
import model.Condition;

import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

public class ThreadHandler implements ThreadHandlerInterface {


    private static ThreadHandler instance;
    private CopyOnWriteArraySet<ConditionThread> threadSet;

    private ThreadHandler() {
        threadSet = new CopyOnWriteArraySet<>();
    }

    public static ThreadHandler getInstance() {
        if (instance == null) {
            instance = new ThreadHandler();
        }
        return instance;
    }

    public String getAllThreads() {
        return threadSet.stream().map(Object::toString).collect(Collectors.joining("\n"));
    }

    public void addNewConditionThread(Condition condition) {
        threadSet.add(new ConditionThread(condition, this));
    }

    public void addNewConditionThread(Condition condition, ActionExecutorInterface actionExecutorInterface) {
        threadSet.add(new ConditionThread(condition, this, actionExecutorInterface));
    }


    @Override
    public void removeDeadThread(ConditionThread conditionThread) {
        threadSet.remove(conditionThread);
    }




}

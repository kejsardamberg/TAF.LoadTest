package se.claremont.taf.performance.loadagent;

import se.claremont.taf.performance.threadscenario.ThreadScenario;
import se.claremont.taf.performance.ThreadScenarioList;
import se.claremont.taf.performance.threadscenariosteps.ThreadStepExecutionStatus;
import se.claremont.taf.performance.threadscenariosteps.ThreadStepResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LoadAgent {

    private String friendlyName;
    private String ipAddress;
    private ThreadScenarioList threadScenarioList;
    private List<LoadAgentChangeListener> listeners;

    public LoadAgent(String name){
        friendlyName = name;
        threadScenarioList = new ThreadScenarioList();
        listeners = new ArrayList<>();
    }

    public LoadAgent(String name, String ipAddress){
        this.friendlyName = name;
        threadScenarioList = new ThreadScenarioList();
        this.ipAddress = ipAddress;
        listeners = new ArrayList<>();
    }

    public void addChangeListener(LoadAgentChangeListener listener){
        listeners.add(listener);
    }

    public ThreadScenarioList getThreadScenarioList(){
        return threadScenarioList;
    }

    public void fireChangeEvent(){
        for(LoadAgentChangeListener listener : listeners){
            listener.loadAgentChanged();
        }
    }

    public void removeThreadScenario(ThreadScenario threadScenario){
        threadScenarioList.getScenarios().remove(threadScenario);
        fireChangeEvent();
    }

    public void addThreadScenario(ThreadScenario threadScenario){
        threadScenarioList.add(threadScenario);
        fireChangeEvent();
    }

    public void setIpAddress(String ipAddress){
        this.ipAddress = ipAddress;
        fireChangeEvent();
    }

    public void setName(String name){
        friendlyName = name;
        fireChangeEvent();
    }

    public String getName() {
        return friendlyName;
    }

    public String toString(){
        if(friendlyName != ipAddress)
            return  friendlyName + " (" + ipAddress + ")";

        return ipAddress;
    }

    public Collection<? extends ThreadScenario> getThreadScenarios() {
        return this.threadScenarioList.getScenarios();
    }

    public Callable<ThreadStepResult> executeTests() {
        Callable<ThreadStepResult> task = () -> {
            ThreadStepResult loadAgentResults = new ThreadStepResult("Load agent " + getName());
            ExecutorService threadScenarioExecutor = Executors.newFixedThreadPool(threadScenarioList.getScenarios().size());
            for (ThreadScenario threadScenario : this.threadScenarioList.getScenarios()) {
                ThreadStepResult threadScenarioResults = new ThreadStepResult("Scenario " + threadScenario.getDisplayName());
                Future<ThreadStepResult> futureResult = threadScenarioExecutor.submit(threadScenario.execute(loadAgentResults));
                try {
                    threadScenarioResults = futureResult.get();
                } catch (InterruptedException ie) {
                    if (threadScenarioResults.status != ThreadStepExecutionStatus.FAILED)
                        threadScenarioResults.status = ThreadStepExecutionStatus.HALTED;
                    threadScenarioResults.addMessage("Execution halted");
                } catch (Exception e) {
                    threadScenarioResults.addMessage(e.toString());
                    threadScenarioResults.status = ThreadStepExecutionStatus.FAILED;
                } finally {
                    threadScenarioResults.stopExeuction();
                    loadAgentResults.attachSubResult(threadScenarioResults);
                }
            }
            return loadAgentResults;
        };
        return task;
    }
}

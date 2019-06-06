package se.claremont.taf.performance.threadscenariosteps;

import se.claremont.taf.performance.gui.guiProfiles.TafDialog;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.List;

public abstract class ThreadScenarioStep {

    private Integer maximumAcceptedIStepIterationDurationInMilliseconds;
    private List<ThreadScenarioStepChangeListener> listeners;
    public int iterationCount;

    public ThreadScenarioStep(){
        listeners = new ArrayList<ThreadScenarioStepChangeListener>();
        maximumAcceptedIStepIterationDurationInMilliseconds = null;
        iterationCount = 1;
    }

    public boolean shouldMeasureTime(){
        return maximumAcceptedIStepIterationDurationInMilliseconds != null && maximumAcceptedIStepIterationDurationInMilliseconds != 0;
    }

    public void setMaximumAcceptedIStepIterationDurationInMilliseconds(Integer milliseconds){
        this.maximumAcceptedIStepIterationDurationInMilliseconds = milliseconds;
        fireChangeEvent();
    }

    public Integer getMaximumAcceptedIStepIterationDurationInMilliseconds(){
        return maximumAcceptedIStepIterationDurationInMilliseconds;
    }

    void setIterationCount(int iterations){
        iterationCount = iterations;
    }

    abstract String getDisplayName();

    abstract Callable<ThreadStepResult> execute(ThreadStepResult parentResult);

    abstract TafDialog editDialog(Window parent);

    abstract void setName(String text);

    public void fireChangeEvent(){
        for(ThreadScenarioStepChangeListener listener : listeners){
            listener.changeEvent(this);
        }
    }

    public void addChangeListener(ThreadScenarioStepChangeListener threadScenarioStepChangeListener){
        listeners.add(threadScenarioStepChangeListener);
    }
}

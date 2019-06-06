package se.claremont.taf.performance;

import se.claremont.taf.performance.loadagent.LoadAgent;
import se.claremont.taf.performance.loadagent.LoadAgentChangeListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LoadAgentList {
    private List<LoadAgent> agents;
    private List<LoadAgentChangeListener> listeners;

    public LoadAgentList(){
        listeners = new ArrayList<>();
        agents = new ArrayList<LoadAgent>();
        addAgent(new LoadAgent("localhost", "127.0.0.1"));
    }

    public static LoadAgent identifyLoadAgentFromName(String trim) {
        for(LoadAgent agent : TestRunner.getInstance().loadAgentList.getAgents()){
            if(agent.getName().equals(trim)) return agent;
        }
        return null;
    }

    public LoadAgent getDefaultAgent(){
        return agents.get(0);
    }

    public List<LoadAgent> getAgents(){
        return Collections.unmodifiableList(agents);
    }

    public void remove(int index){
        agents.remove(index);
        for(LoadAgentChangeListener listener : listeners){
            listener.loadAgentRemoved();
        }
    }

    public void addChangeListener(LoadAgentChangeListener listener){
        listeners.add(listener);
    }

    public List<String> getAgentsAsStrings(){
        List<String> names = new ArrayList<>();
        for(LoadAgent loadAgent : agents){
            names.add(loadAgent.getName());
        }
        return names;
    }

    public interface LoadAgentChangeListener{

        void loadAgentAdded();
        void loadAgentChanged();
        void loadAgentRemoved();
    }

    private void fireLoadAgentChanged(){
        for(LoadAgentChangeListener listener : listeners){
            listener.loadAgentChanged();
        }
    }

    public void addAgent(LoadAgent loadAgent){
        loadAgent.addChangeListener(new se.claremont.taf.performance.loadagent.LoadAgentChangeListener() {
            @Override
            public void loadAgentChanged() {
                fireLoadAgentChanged();
            }
        });
        agents.add(loadAgent);

        for(LoadAgentChangeListener listener : listeners){
            listener.loadAgentAdded();
        }
    }
}

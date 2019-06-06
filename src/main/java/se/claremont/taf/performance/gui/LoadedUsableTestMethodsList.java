package se.claremont.taf.performance.gui;

import se.claremont.taf.performance.LoadResource;

import java.util.ArrayList;
import java.util.List;

public class LoadedUsableTestMethodsList {
    private List<LoadResource> loadResourceList;
    private List<LoadedResourceListener> listeners;

    public LoadedUsableTestMethodsList(){
        loadResourceList = new ArrayList<>();
        listeners = new ArrayList<>();
    }

    public void addActionListener(LoadedResourceListener loadedResourceListener){
        listeners.add(loadedResourceListener);
    }

    public void add(LoadResource loadResource){
        for(LoadedResourceListener listener : listeners){
            listener.resourceAddedEvent();
        }
        loadResourceList.add(loadResource);
    }

    public List<LoadResource> getLoadResourceList(){
        return loadResourceList;
    }
}

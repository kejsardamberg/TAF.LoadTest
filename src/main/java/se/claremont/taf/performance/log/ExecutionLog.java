package se.claremont.taf.performance.log;

import java.util.*;

public class ExecutionLog  {

    private List<LogPost> logCollection;

    public ExecutionLog(){
        logCollection = new ArrayList<>();
    }

    public synchronized void log(LogPost logPost){

    }
}

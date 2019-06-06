package se.claremont.taf.performance.threadscenariosteps;

public enum ThreadStepExecutionStatus {
    NOT_STARTED("Not yet started", "notstarted.png"),
    RUNNING("Running", "/running.png"),
    PASSED("Passed", "/passed.png"),
    FAILED("Failed", "/failed.png"),
    HALTED("Halted","");

    private final String treeResultsIconUrl;
    private final String friendlyName;

    private ThreadStepExecutionStatus(String friendlyName, String treeResultIconUrl){
        this.friendlyName = friendlyName;
        this.treeResultsIconUrl = treeResultIconUrl;
    }

    @Override
    public String toString(){
        return friendlyName;
    }

    public String getResultsTreeIcon() {
        return treeResultsIconUrl;
    }
}

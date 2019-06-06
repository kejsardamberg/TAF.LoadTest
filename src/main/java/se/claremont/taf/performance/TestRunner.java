package se.claremont.taf.performance;

import se.claremont.taf.performance.gui.guiProfiles.TafLabel;
import se.claremont.taf.performance.gui.guiProfiles.TafPanel;
import se.claremont.taf.performance.loadagent.LoadAgent;
import se.claremont.taf.performance.log.ExecutionLog;
import se.claremont.taf.performance.threadscenario.ThreadScenario;
import se.claremont.taf.performance.threadscenariosteps.ThreadStepExecutionStatus;
import se.claremont.taf.performance.threadscenariosteps.ThreadStepResult;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class TestRunner {
    public LoadAgentList loadAgentList;
    public ExecutionLog executionLog;
    ThreadStepResult runResult;
    private RunExecutor executor;
    private ExecutorService loadAgentExecutor;
    private static TestRunner single_instance = null;
    private TafPanel registeredThreadScenarios;

    private TestRunner()
    {
        loadAgentList = new LoadAgentList();
        executionLog = new ExecutionLog();
    }

    // static method to create instance of Singleton class
    public static TestRunner getInstance()
    {
        if (single_instance == null)
            single_instance = new TestRunner();

        return single_instance;
    }

    private class RunExecutor extends Thread{

        public void run(){
            runResult = new ThreadStepResult("Test run");
            runResult.startExecution();
            loadAgentExecutor = Executors.newFixedThreadPool(loadAgentList.getAgents().size());
            runResult.startExecution();
            for(LoadAgent loadAgent : loadAgentList.getAgents()){
                ThreadStepResult loadAgentResult = new ThreadStepResult(loadAgent.getName());
                loadAgentResult.startExecution();
                Future<ThreadStepResult> futureAgentResult = loadAgentExecutor.submit(loadAgent.executeTests());
                try {
                    loadAgentResult = futureAgentResult.get();
                } catch (InterruptedException ie){
                    if(loadAgentResult.status != ThreadStepExecutionStatus.FAILED)
                    loadAgentResult.status = ThreadStepExecutionStatus.HALTED;
                }catch (Exception e){
                    runResult.addMessage(e.toString());
                    runResult.status = ThreadStepExecutionStatus.FAILED;
                } finally {
                    runResult.stopExeuction();
                    runResult.attachSubResult(loadAgentResult);
                }
            }
            ResultsPresenter presenter = new ResultsPresenter(runResult);
        }
    }

    public class ResultsPresenter extends JFrame implements ActionListener{
        public ResultsPresenter(ThreadStepResult result){
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setTitle("TAF - Load test results");
            getContentPane().add(result.getResultAsGuiTree());
            pack();
            setVisible(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            this.dispose();
        }
    }

    public int getNextDisplayNameCounter(){
        int i = 0;
        for(LoadAgent la : loadAgentList.getAgents()){
            i = i + la.getThreadScenarios().size();
        }
        return i + 1;
    }

    public void run(){
        executor = new RunExecutor();
        executor.start();
    }

    public void killRun() {
        if(loadAgentExecutor == null || loadAgentExecutor.isTerminated() || loadAgentExecutor.isShutdown())return;
        loadAgentExecutor.shutdown();
        executor.interrupt();
        runResult.stopExeuction();
        ResultsPresenter presenter = new ResultsPresenter(runResult);
    }

    public TafPanel getRunPanel(Window parentWindow){
        TafPanel panel = new TafPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        registeredThreadScenarios = new TafPanel();
        registeredThreadScenarios.setLayout(new BoxLayout(registeredThreadScenarios, BoxLayout.Y_AXIS));
        updateGuiPanel(parentWindow);
        loadAgentList.addChangeListener(new LoadAgentList.LoadAgentChangeListener() {
            @Override
            public void loadAgentAdded() {
                updateGuiPanel(parentWindow);
            }

            @Override
            public void loadAgentChanged() {
                updateGuiPanel(parentWindow);
            }

            @Override
            public void loadAgentRemoved() {
                updateGuiPanel(parentWindow);
            }
        });
        panel.add(new JScrollPane(registeredThreadScenarios));

        panel.setPreferredSize(new Dimension( Toolkit.getDefaultToolkit().getScreenSize().width * 2/4,  Toolkit.getDefaultToolkit().getScreenSize().height * 2/4));
        return panel;
    }

    private void updateGuiPanel(Window parentWindow){
        List<ThreadScenario> scenarios = new ArrayList<>();
        for(LoadAgent la : loadAgentList.getAgents()){
            for(ThreadScenario ts : la.getThreadScenarios()){
                scenarios.add(ts);
            }
        }

        registeredThreadScenarios.removeAll();
        registeredThreadScenarios.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.gridy = 0;
        TafLabel scenarioNameLabel = new TafLabel("Scenario name");
        c.gridx = 0;
        registeredThreadScenarios.add(scenarioNameLabel, c);

        TafLabel scenarioLoadAgentLabel = new TafLabel("Executing Load Agent");
        c.gridx = 1;
        registeredThreadScenarios.add(scenarioLoadAgentLabel, c);

        TafLabel concurrentThreadCountLabel = new TafLabel("Concurrent thread count");
        c.gridx = 2;
        registeredThreadScenarios.add(concurrentThreadCountLabel, c);

        TafLabel editLabel = new TafLabel("Edit");
        c.gridx = 3;
        registeredThreadScenarios.add(editLabel, c);

        for(int i = 0; i < scenarios.size(); i++){
            ThreadScenario ts = scenarios.get(i);
            ts.addToGuiPanel(parentWindow, registeredThreadScenarios, i+1);
        }
        parentWindow.revalidate();
        parentWindow.repaint();
    }

}

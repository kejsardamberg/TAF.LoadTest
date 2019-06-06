package se.claremont.taf.performance.threadscenario;

import se.claremont.taf.performance.TestRunner;
import se.claremont.taf.performance.gui.guiProfiles.TafButton;
import se.claremont.taf.performance.gui.guiProfiles.TafPanel;
import se.claremont.taf.performance.gui.guiProfiles.TafTextField;
import se.claremont.taf.performance.loadagent.LoadAgent;
import se.claremont.taf.performance.threadscenariosteps.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.*;

public class ThreadScenario {

    private String name;
    public ThreadStepCycle steps;
    public LoadAgent loadAgent;
    public int concurrentThreadCount;

    public ThreadScenario(String displayName, LoadAgent loadAgent){
        this.name = displayName;
        this.loadAgent = loadAgent;
        concurrentThreadCount = 10;
        steps = new ThreadStepCycle();
        steps.addChangeListener(new ThreadScenarioStepChangeListener() {
            @Override
            public void changeEvent(ThreadScenarioStep threadScenarioStep) {
                setName(steps.getDisplayName());
            }
        });
        steps.iterationCount = 0; //Marks forever, until aborted.
    }

    public Callable<ThreadStepResult> execute(ThreadStepResult parentResult){
        Callable<ThreadStepResult> task = () -> {
            ExecutorService stepRunner = Executors.newFixedThreadPool(concurrentThreadCount);
            ThreadStepResult overallResults = new ThreadStepResult(getDisplayName());
            overallResults.startExecution();
            try {
                for (int i = 0; i < concurrentThreadCount; i++) {
                    ThreadStepResult result = new ThreadStepResult(getDisplayName()+ "_Thread" + i);
                    result.startExecution();
                    Future<ThreadStepResult> futureResults = stepRunner.submit(steps.execute(result));
                    ThreadStepResult results = futureResults.get();
                    if (results.status == ThreadStepExecutionStatus.FAILED) {
                        overallResults.status = ThreadStepExecutionStatus.FAILED;
                        result.status = ThreadStepExecutionStatus.FAILED;
                        result.messages.addAll(results.messages);
                    }
                    result.stopExeuction();
                    overallResults.attachSubResult(result);
                }
                //stepRunner.awaitTermination(60, TimeUnit.MINUTES);
            }catch (Exception e){
                overallResults.addMessage(e.toString());
                overallResults.status = ThreadStepExecutionStatus.FAILED;
                if(parentResult != null)
                    parentResult.attachSubResult(overallResults);
                overallResults.stopExeuction();
                return overallResults;
            } finally {
                if(parentResult != null)
                    parentResult.attachSubResult(overallResults);
                overallResults.stopExeuction();
                return overallResults;
            }
        };
        return task;
    }

    public void setName(String name){
        this.name = name;
        loadAgent.fireChangeEvent();
    }

    public String getDisplayName(){
        return name;
    }

    public void addToGuiPanel(Window parentWindow, TafPanel panel, int y_position_in_gridbag){
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = y_position_in_gridbag;

        c.gridx = 0;
        panel.add(guiNameField(), c);

        c.gridx = 1;
        panel.add(guiLoadAgentDropdown(), c);

        c.gridx = 2;
        panel.add(guiConcurrentThreadsSpinner(), c);
        TafButton editButton = new TafButton("...");
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                steps.editDialog(parentWindow);
            }
        });

        c.gridx = 3;
        panel.add(editButton, c);
    }

    private JSpinner guiConcurrentThreadsSpinner() {
        SpinnerNumberModel concurrentThreadCountSpinnerModel = new SpinnerNumberModel(concurrentThreadCount, 1, Integer.MAX_VALUE, 1);
        JSpinner concurrentThreadsCount = new JSpinner(concurrentThreadCountSpinnerModel);
        concurrentThreadsCount.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                Integer value = Integer.parseInt(concurrentThreadsCount.getValue().toString());
                if( value == null || value == 0 ) return;
                concurrentThreadCount = value;
                loadAgent.fireChangeEvent();
            }
        });
        return concurrentThreadsCount;
    }

    private JComboBox<LoadAgent> guiLoadAgentDropdown() {
        LoadAgent[] agents = new LoadAgent[TestRunner.getInstance().loadAgentList.getAgents().size()];
        JComboBox<LoadAgent> loadAgentDropdown = new JComboBox<LoadAgent>(TestRunner.getInstance().loadAgentList.getAgents().toArray(agents));
        loadAgentDropdown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox<LoadAgent> combo = (JComboBox<LoadAgent>) e.getSource();
                LoadAgent loadAgent = (LoadAgent) combo.getSelectedItem();
                moveThreadScenarioToNewLoadAgent(loadAgent);
            }
        });
        return loadAgentDropdown;
    }

    private TafTextField guiNameField(){
        TafTextField scenarioNameField = new TafTextField();
        scenarioNameField.setText(getDisplayName());
        scenarioNameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                steps.setName(scenarioNameField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                steps.setName(scenarioNameField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                steps.setName(scenarioNameField.getText());
            }
        });
        return scenarioNameField;
    }

    public void moveThreadScenarioToNewLoadAgent(LoadAgent receivingLoadAgent){
        loadAgent.removeThreadScenario(this);
        receivingLoadAgent.addThreadScenario(this);
        loadAgent = receivingLoadAgent;
        loadAgent.fireChangeEvent();
    }

}

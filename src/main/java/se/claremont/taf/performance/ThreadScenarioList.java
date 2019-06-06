package se.claremont.taf.performance;

import se.claremont.taf.performance.LoadAgentList.LoadAgentChangeListener;
import se.claremont.taf.performance.gui.guiProfiles.TafLabel;
import se.claremont.taf.performance.gui.guiProfiles.TafPanel;
import se.claremont.taf.performance.loadagent.LoadAgent;
import se.claremont.taf.performance.threadscenario.ThreadScenario;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ThreadScenarioList {
    private List<ThreadScenario> scenarios;
    private List<TreadScenarioListListener> listeners;
    //  private ThreadScenarioPanel loadScenarioPanel;

    public ThreadScenarioList(){
        scenarios = new ArrayList<>();
        listeners = new ArrayList<>();
      //  addEventListener(new GuiListListener());
    }

    public void addEventListener(TreadScenarioListListener treadScenarioListListener){
        listeners.add(treadScenarioListListener);
    }

    public void add(ThreadScenario step) {
        scenarios.add(step);
        for (TreadScenarioListListener listener : listeners){
            listener.threadScenarioAdded();
        }
    }

    /*
    private class GuiListListener extends TreadScenarioListListener{

        @Override
        public void threadScenarioAdded() {
            if(loadScenarioPanel == null) return;
            loadScenarioPanel.updatePanel();
        }
    }

    public ThreadScenarioPanel guiPanel(){
        loadScenarioPanel = new ThreadScenarioPanel(this);
        addEventListener(new GuiListListener());
        loadScenarioPanel.updatePanel();
        TestRunner.getInstance().loadAgentList.addChangeListener(new LoadAgentListener());
        return loadScenarioPanel;
    }

*/
    public int size() {
        return scenarios.size();
    }

    public List<ThreadScenario> getScenarios() {
        return scenarios;
    }


    public abstract class TreadScenarioListListener {
        public abstract void threadScenarioAdded();
    }

    /*
    public class ThreadScenarioPanel extends TafPanel{
        private ThreadScenarioList threadScenarioList;

        public ThreadScenarioPanel(ThreadScenarioList threadScenarioList) {
            super();
            this.threadScenarioList = threadScenarioList;
        }

        public void updatePanel() {
            if(loadScenarioPanel == null)return;
            loadScenarioPanel.removeAll();
            TafPanel threadDistributionPanel = new TafPanel();
            loadScenarioPanel.add(new TafLabel("Thread scenario distribution"));
            if(threadScenarioList.size() == 0){
                threadDistributionPanel.setLayout(new GridLayout(4, 3));
            } else {
                threadDistributionPanel.setLayout(new GridLayout(TestRunner.getInstance().threadScenarioList.size() + 1, 3));
            }
            threadDistributionPanel.add(new TafLabel("Thread scenario"));
            threadDistributionPanel.add(new TafLabel("LoadAgent)"));
            threadDistributionPanel.add(new TafLabel("Concurrent Threads"));
            if(TestRunner.getInstance().threadScenarioList.size() == 0){
                for(int i=0; i < 9;i++){
                    TafLabel blank = new TafLabel("");
                    blank.setBackground(Color.white);
                    threadDistributionPanel.add(blank);
                }
            } else {
                Border border = BorderFactory.createLineBorder(Color.black);
                for(ThreadScenario threadScenario : threadScenarioList.getScenarios()){
                    TafLabel threadScenarioName = new TafLabel(threadScenario.getDisplayName());
                    threadScenarioName.setBackground(Color.white);
                    threadDistributionPanel.add(threadScenarioName);

                    JComboBox agentList = new JComboBox();
                    for(LoadAgent agent : TestRunner.getInstance().loadAgentList.getAgents()){
                        agentList.addItem(agent.getName());
                    }
                    agentList.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            threadScenario.loadAgent = LoadAgentList.identifyLoadAgentFromName(agentList.getSelectedItem().toString().trim());
                        }
                    });
                    agentList.setBorder(border);
                    agentList.setSelectedItem(threadScenario.loadAgent);
                    threadDistributionPanel.add(agentList);

                    SpinnerNumberModel concurrentThreadModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);
                    JSpinner spinner = new JSpinner(concurrentThreadModel);
                    spinner.addChangeListener(new ChangeListener() {
                        @Override
                        public void stateChanged(ChangeEvent e) {
                            Integer value = Integer.parseInt(spinner.getValue().toString().trim());
                            if(value != null) {
                                threadScenario.steps.iterationCount = value;
                            }
                        }
                    });
                    spinner.setValue(threadScenario.steps.iterationCount);
                    spinner.setBorder(border);
                    threadDistributionPanel.add(spinner);

                }
            }
            JScrollPane scrollPane = new JScrollPane(threadDistributionPanel);
            loadScenarioPanel.add(scrollPane);
        }

    }

    public class LoadAgentListener implements LoadAgentChangeListener {
        public void loadAgentAdded(){
            loadScenarioPanel.updatePanel();
        }
        public void loadAgentRemoved(){
            loadScenarioPanel.updatePanel();
        }
    }
    */
}

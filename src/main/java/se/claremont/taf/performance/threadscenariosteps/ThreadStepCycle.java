package se.claremont.taf.performance.threadscenariosteps;

import se.claremont.taf.performance.gui.MainGui;
import se.claremont.taf.performance.gui.guiProfiles.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ThreadStepCycle extends ThreadScenarioStep {

    private ArrayList<ThreadScenarioStep> steps;
    private String cycleName;
    private JFrame parentFrame;
    private DefaultListModel model ;

    public ThreadStepCycle(){
        super();
        steps = new ArrayList<>();
        cycleName = "Thread Step Cycle";
        model = new DefaultListModel();
        addChangeListener(new ListUpdateListener());
    }

    public void setName(String name){
        cycleName = name;
        fireChangeEvent();
    }

    @Override
    public String getDisplayName() {
        return cycleName;
    }

    @Override
    public Callable<ThreadStepResult> execute(ThreadStepResult parentResult) {
        Callable<ThreadStepResult> task = () -> {
            ThreadStepResult result = new ThreadStepResult(cycleName);
            result.startExecution();
            int i = 0;
            while (iterationCount == 0 || i < iterationCount){
                i++;
                try {
                    boolean success = true;
                    for (ThreadScenarioStep step : steps) {
                        ExecutorService taskManager = Executors.newSingleThreadExecutor();
                        Future<ThreadStepResult> futureStepResult = taskManager.submit(step.execute(result));
                        ThreadStepResult methodExecutionResult = futureStepResult.get();
                        result.messages.addAll(methodExecutionResult.messages);

                        if (methodExecutionResult.status == ThreadStepExecutionStatus.FAILED)
                            success = false;
                    }
                    if (success) {
                        result.setStatus(ThreadStepExecutionStatus.PASSED);
                    } else {
                        result.setStatus(ThreadStepExecutionStatus.FAILED);
                    }
                    //while (!taskManager.awaitTermination(30, TimeUnit.SECONDS));
                } catch (Exception e) {
                    result.status = ThreadStepExecutionStatus.FAILED;
                    result.addMessage(e.toString());
                } finally {
                    result.stopExeuction();
                    if (parentResult != null) parentResult.attachSubResult(result);
                    return result;
                }
            }
            result.stopExeuction();
            if (parentResult != null) parentResult.attachSubResult(result);
            return result;
        };
        return task;
    }

    @Override
    public TafDialog editDialog(Window parent) {
        TafDialog dialog = new TafDialog(parent, "TAF - Edit Thread Step Cycle");
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        TafPanel panel = new TafPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        TafPanel namePanel = new TafPanel();
        namePanel.setLayout(new FlowLayout());
        TafLabel nameLabel = new TafLabel("Step name:");
        namePanel.add(nameLabel);
        TafTextField nameField = new TafTextField();
        nameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                setName(nameField.getText().trim());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                setName(nameField.getText().trim());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                setName(nameField.getText().trim());
            }
        });
        nameField.setText(cycleName);
        namePanel.add(nameField);
        panel.add(namePanel);
        panel.add(getStepsPanel(this));

        TafPanel buttonPanel = new TafPanel();
        TafButton okButton = new TafButton("Ok");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        buttonPanel.add(okButton);

        panel.add(buttonPanel);
        dialog.getContentPane().add(panel);
        dialog.pack();
        dialog.setVisible(true);
        return dialog;
    }

    public void updateStepsModel(){
        model.removeAllElements();
        for(ThreadScenarioStep step : this.steps){
            model.addElement(step.getDisplayName());
        }
    }

    public void removeStep(int stepIndex){
        steps.remove(stepIndex);
        fireChangeEvent();
    }

    public JPanel getStepsPanel(ThreadStepCycle targetCycle){
        TafPanel panel = new TafPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        TafLabel stepNameLabel = new TafLabel("Step name");
        JList methodsPanel = new JList(model);
        TafButton removeStepButton = new TafButton("Remove step");
        removeStepButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                targetCycle.removeStep(methodsPanel.getSelectedIndex());
            }
        });
        removeStepButton.setEnabled(false);
        panel.add(stepNameLabel);
        methodsPanel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        updateStepsModel();
        methodsPanel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(!methodsPanel.isSelectionEmpty()){
                    removeStepButton.setEnabled(true);
                } else {
                    removeStepButton.setEnabled(false);
                }
            }
        });

        TafPanel registeredStepsPanel = new TafPanel();
        registeredStepsPanel.setLayout(new BoxLayout(registeredStepsPanel, BoxLayout.Y_AXIS));

        TafLabel registeredStepsLabel = new TafLabel("Registered thread scenario steps");
        registeredStepsPanel.add(registeredStepsLabel);

        registeredStepsPanel.add(new JScrollPane(methodsPanel));

        TafPanel addStepPanel = new TafPanel();
        addStepPanel.setLayout(new FlowLayout());

        TafLabel stepTypeDropDownLabel = new TafLabel("Step type:");
        addStepPanel.add(stepTypeDropDownLabel);

        TafButton addStepButton = new TafButton("Add step...");

        JComboBox testStepDropDown = new JComboBox(MainGui.threadScenarioStepTypes.keySet().toArray());
        testStepDropDown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(ActionListener al : addStepButton.getActionListeners()){
                    addStepButton.removeActionListener(al);
                }
                addStepButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Class<? extends ThreadScenarioStep> c = MainGui.threadScenarioStepTypes.get(testStepDropDown.getSelectedItem().toString());
                        try {
                            ThreadScenarioStep o = c.getConstructor().newInstance(null);
                            ListUpdateListener updateListener = new ListUpdateListener();
                            addChangeListener(updateListener);
                            o.addChangeListener(updateListener);
                            addStep(o);
                            o.editDialog(parentFrame);
                        } catch (Throwable e1) {
                            System.out.println(e1.toString());
                        }
                    }

                });
            }
        });

        addStepPanel.add(testStepDropDown);
        addStepPanel.add(addStepButton);
        registeredStepsPanel.add(addStepPanel);
        registeredStepsPanel.add(removeStepButton);

        panel.add(registeredStepsPanel);

        return panel;
    }

    private class ListUpdateListener extends ThreadScenarioStepChangeListener {
        @Override
        public void changeEvent(ThreadScenarioStep threadScenarioStep) {
            updateStepsModel();
        }
    }

    public void addStep(ThreadScenarioStep step) {
        this.steps.add(step);
        fireChangeEvent();
    }
}

package se.claremont.taf.performance.threadscenariosteps;

import se.claremont.taf.performance.gui.guiProfiles.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.*;

public class ThreadStepExecutionIterator extends ThreadScenarioStep {
    private int iterations;
    private ThreadScenarioStep step;
    private String stepName;

    public ThreadStepExecutionIterator(){
        super();
        stepName = "Execution iterator step";
    }

    public ThreadStepExecutionIterator(ThreadScenarioStep step, int iterations){
        super();
        this.step = step;
        this.iterations = iterations;
        stepName = step.getDisplayName() + " (x" + iterations + ")";
    }

    @Override
    public String getDisplayName() {
        return stepName;
    }

    @Override
    public Callable<ThreadStepResult> execute(ThreadStepResult parentResult) {
        Callable<ThreadStepResult> task = () -> {
            ExecutorService taskManager = Executors.newSingleThreadExecutor();
            ThreadStepResult result = new ThreadStepResult(getDisplayName());
            result.startExecution();
            boolean success = true;
            result.setStatus(ThreadStepExecutionStatus.RUNNING);
            for(int i = 0; i<iterations;i++){
                Future<ThreadStepResult> futureIterationResult = taskManager.submit(step.execute(result));
                ThreadStepResult iterationResult = futureIterationResult.get();
                if(iterationResult.status == ThreadStepExecutionStatus.FAILED)
                    success = false;
                if(iterationResult.messages.size() > 0)
                    result.messages.addAll(iterationResult.messages);
            }
            if(success){
                result.setStatus(ThreadStepExecutionStatus.PASSED);
            } else {
                result.setStatus(ThreadStepExecutionStatus.FAILED);
            }
            while (!taskManager.awaitTermination(30, TimeUnit.SECONDS))
            result.stopExeuction();
            if(parentResult != null) parentResult.attachSubResult(result);
            return result;
        };
        return task;
    }

    @Override
    public TafDialog editDialog(Window parent) {
        TafDialog dialog = new TafDialog(parent, "TAF - Thread Step Execution Iterator");
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        TafPanel panel = new TafPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        TafPanel namePanel = new TafPanel();
        TafLabel nameLabel = new TafLabel("Step name:");
        namePanel.add(nameLabel);
        TafTextField nameField = new TafTextField();
        nameField.setText(stepName);
        nameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                setName(nameField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                setName(nameField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                setName(nameField.getText());
            }
        });
        namePanel.add(nameField);
        panel.add(namePanel);
        TafPanel iterationSPanel = new TafPanel();
        iterationSPanel.setLayout(new FlowLayout());
        TafLabel stepNameLabel = new TafLabel("Step:");
        iterationSPanel.add(stepNameLabel);
        TafTextField stepNameValue = new TafTextField();
        stepNameValue.setText("            ");
        iterationSPanel.add(stepNameValue);
        TafButton addStepButton = new TafButton("Add...");
        iterationSPanel.add(addStepButton);
        panel.add(iterationSPanel);

        TafPanel countPanel = new TafPanel();
        countPanel.setLayout(new FlowLayout());
        TafLabel countLabel = new TafLabel("Iteration count:");
        countPanel.add(countLabel);
        SpinnerNumberModel countModel = new SpinnerNumberModel(1, 0, Integer.MAX_VALUE, 1);
        JSpinner spinner = new JSpinner(countModel);
        spinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                Integer value = Integer.parseInt(spinner.getValue().toString());
                if(value != null)
                    setIterations(value);
            }
        });
        countPanel.add(spinner);
        panel.add(countPanel);

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

        dialog.add(panel);
        dialog.pack();
        dialog.setVisible(true);
        return dialog;
    }

    public void setIterations(int iterations){
        this.iterations = iterations;
        fireChangeEvent();
    }

    @Override
    public void setName(String name) {
        stepName = name;
        fireChangeEvent();
    }
}

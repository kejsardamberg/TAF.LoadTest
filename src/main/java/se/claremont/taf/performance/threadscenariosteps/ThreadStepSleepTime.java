package se.claremont.taf.performance.threadscenariosteps;

import se.claremont.taf.performance.gui.guiProfiles.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Callable;

public class ThreadStepSleepTime extends ThreadScenarioStep{

    private int millisecondsToSleep;
    private String name;

    public ThreadStepSleepTime(){
        super();
        millisecondsToSleep = 100;
        name = "Sleep 100 ms";
    }


    public ThreadStepSleepTime(int milliseconds){
        super();
        this.millisecondsToSleep = milliseconds;
        name = "Sleep " + millisecondsToSleep + " ms";
    }

    public void setName(String name){
        this.name = name;
        fireChangeEvent();
    }

    public void setTime(int millisecondsToSleep){
        name = "Sleep " + millisecondsToSleep + " ms";
        this.millisecondsToSleep = millisecondsToSleep;
        fireChangeEvent();
    }

    @Override
    public String getDisplayName() {
        return name;
    }

    @LoadTestFeature
    public void sleep(int millisecondsToSleep){
        ThreadStepResult result = new ThreadStepResult(getDisplayName());
        result.startExecution();
        try {
            Thread.sleep(millisecondsToSleep);
        } catch (InterruptedException e) {
            result.addMessage(e.getMessage());
        }
        result.setStatus(ThreadStepExecutionStatus.PASSED);
        result.stopExeuction();
    }

    @Override
    public Callable<ThreadStepResult> execute(ThreadStepResult parentResult) {
        Callable<ThreadStepResult> resultCallable = () -> {
            ThreadStepResult result = new ThreadStepResult(getDisplayName());
            result.startExecution();
            try {
                Thread.sleep(millisecondsToSleep);
            } catch (InterruptedException e) {
                result.addMessage(e.getMessage());
            }
            result.setStatus(ThreadStepExecutionStatus.PASSED);
            result.stopExeuction();
            if(parentResult != null) parentResult.attachSubResult(result);
            return result;
        };
        return resultCallable;
    }

    @Override
    public TafDialog editDialog(Window parent) {
        TafDialog dialog = new TafDialog(parent, "TAF");
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        TafPanel panel = new TafPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        TafPanel namePanel = new TafPanel();
        TafLabel nameLabel = new TafLabel("Step name:");
        namePanel.add(nameLabel);
        TafLabel nameField = new TafLabel(name);
        namePanel.add(nameField);
        panel.add(namePanel);

        TafPanel valuePanel = new TafPanel();
        TafLabel label = new TafLabel("Sleep time in milliseconds:");
        valuePanel.add(label);
        TafTextField valueField = new TafTextField();
        valueField.setText("       " + millisecondsToSleep);
        valueField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                attemptSetTime(valueField);
                nameField.setText(getDisplayName());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                attemptSetTime(valueField);
                nameField.setText(getDisplayName());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                attemptSetTime(valueField);
                nameField.setText(getDisplayName());
            }
        });

        valuePanel.add(valueField);
        panel.add(valuePanel);

        TafPanel buttonPanel = new TafPanel();
        TafButton okButton = new TafButton("Ok");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setName("Sleep " + millisecondsToSleep + " ms");
                fireChangeEvent();
                dialog.dispose();
            }
        });
        buttonPanel.add(okButton);

        panel.add(buttonPanel);

        dialog.getContentPane().add(panel);
        dialog.setInitialFocus(valueField);
        dialog.getRootPane().setDefaultButton(okButton);
        dialog.pack();
        dialog.setVisible(true);
        return dialog;
    }

    private void attemptSetTime(TafTextField field){
        Integer i = null;
        try{
            i = Integer.parseInt(field.getText().trim());
        }catch (Exception e){
            return;
        }
        if(i == null)return;
        setTime(i);
        fireChangeEvent();
    }
}



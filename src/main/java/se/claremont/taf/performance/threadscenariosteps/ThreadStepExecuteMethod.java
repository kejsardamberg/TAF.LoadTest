package se.claremont.taf.performance.threadscenariosteps;

import se.claremont.taf.performance.gui.JarLoader;
import se.claremont.taf.performance.gui.LoadedUsefulMethodsTable;
import se.claremont.taf.performance.gui.guiProfiles.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

public class ThreadStepExecuteMethod extends ThreadScenarioStep {

    private Method method;
    private String stepName;
    private Object object;

    public ThreadStepExecuteMethod(){
        super();
        stepName = "Thread step method executor";
    }

    public void setName(String name){
        stepName = name;
        fireChangeEvent();
    }

    public void setIterationCount(int iterationCount){
        this.iterationCount = iterationCount;
    }

    @Override
    public String getDisplayName(){
        String name = stepName;
        if(iterationCount > 1){
            name += " (x" + iterationCount + ")";
        }
        return name;
    }

    @Override
    public Callable<ThreadStepResult> execute(ThreadStepResult parentResult) {
        Callable<ThreadStepResult> task = () -> {
            ThreadStepResult overallStatus = new ThreadStepResult(getDisplayName());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            PrintStream old = System.out;

            try {
                System.setOut(ps);
                for (int i = 0; i < iterationCount; i++) {
                    long startTime = System.currentTimeMillis();
                    ThreadStepResult resultStatus = new ThreadStepResult(getDisplayName() + i);
                    resultStatus.startExecution();
                    try {
                        if (method == null) throw new Exception("Method is null. Cannot execute.");
                        if (object == null) {
                            if(!Stream.of(method.getDeclaringClass().getConstructors())
                                    .anyMatch((c) -> c.getParameterCount() == 0)){
                                throw new Exception("No constructor without argument for class '" + method.getDeclaringClass().getName() + "'. Cannot execute.");
                            }
                            Constructor<?> ctor = method.getDeclaringClass().getConstructor();
                            object = ctor.newInstance();
                        }
                        method.invoke(object);
                        resultStatus.setStatus(ThreadStepExecutionStatus.PASSED);
                    } catch (Throwable e) {
                        resultStatus.setStatus(ThreadStepExecutionStatus.FAILED);
                        resultStatus.addMessage(e.getMessage());
                    } finally {
                        resultStatus.addMessage("Output:" + System.lineSeparator() + baos.toString());
                        resultStatus.stopExeuction();
                        long stopTime = System.currentTimeMillis();
                        if(shouldMeasureTime() && stopTime-startTime > this.getMaximumAcceptedIStepIterationDurationInMilliseconds()){
                            resultStatus.status = ThreadStepExecutionStatus.FAILED;
                            resultStatus.addMessage("Iteration took longer than expected. Maximum time was " +
                                    getMaximumAcceptedIStepIterationDurationInMilliseconds() +
                                    " ms. It took " + (stopTime-startTime) + " ms.");
                        }
                        overallStatus.attachSubResult(resultStatus);
                        return resultStatus;
                    }
                }
            } catch (Exception ex){
                overallStatus.addMessage(ex.toString());
                overallStatus.status = ThreadStepExecutionStatus.FAILED;
            }finally {
                System.out.flush();
                System.setOut(old);
                overallStatus.stopExeuction();
                if (parentResult != null) parentResult.attachSubResult(overallStatus);
                return overallStatus;
            }
        };
        return task;
    }

    @Override
    public TafDialog editDialog(Window parent) {
        TafDialog dialog = new TafDialog(parent, "TAF - Thread Step Execute Method");
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

        LoadedUsefulMethodsTable methodsTable = new LoadedUsefulMethodsTable();
        panel.add(methodsTable);
        JarLoader jarLoader = new JarLoader();
        panel.add(jarLoader);

        TafPanel maxTimePanel = new TafPanel();
        TafLabel maxMsLabel = new TafLabel("Optional max iteration duration");
        maxTimePanel.add(maxMsLabel);
        TafTextField maxDuration = new TafTextField();
        String maxDurationText = "";
        if(getMaximumAcceptedIStepIterationDurationInMilliseconds() != null){
            maxDurationText = getMaximumAcceptedIStepIterationDurationInMilliseconds().toString();
        }
        maxDuration.setText("      " + maxDurationText);
        maxDuration.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                Integer value = Integer.parseInt(maxDuration.getText().trim());
                if(value == null || value == 0){
                   setMaximumAcceptedIStepIterationDurationInMilliseconds(null);
                } else {
                    setMaximumAcceptedIStepIterationDurationInMilliseconds(value);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                Integer value = Integer.parseInt(maxDuration.getText().trim());
                if(value == null || value == 0){
                    setMaximumAcceptedIStepIterationDurationInMilliseconds(null);
                } else {
                    setMaximumAcceptedIStepIterationDurationInMilliseconds(value);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                Integer value = Integer.parseInt(maxDuration.getText().trim());
                if(value == null || value == 0){
                    setMaximumAcceptedIStepIterationDurationInMilliseconds(null);
                } else {
                    setMaximumAcceptedIStepIterationDurationInMilliseconds(value);
                }
            }
        });
        maxTimePanel.add(maxDuration);
        TafLabel maxDurationUnitLabel = new TafLabel("milliseconds");
        maxTimePanel.add(maxDurationUnitLabel);
        panel.add(maxTimePanel);

        TafButton okButton = new TafButton("Ok");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(methodsTable.getSelected() != null){
                    method = methodsTable.getSelected().method;
                    setName("Execute '" + method.getDeclaringClass().getName() + "." + method.getName() + "' method");
                }
                dialog.dispose();
            }
        });
        panel.add(okButton);
        dialog.getContentPane().add(panel);
        dialog.pack();
        dialog.setVisible(true);
        return dialog;
    }
}

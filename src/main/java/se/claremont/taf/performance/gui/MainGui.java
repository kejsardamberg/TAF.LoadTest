package se.claremont.taf.performance.gui;

import org.reflections.Reflections;
import se.claremont.taf.performance.LoadResource;
import se.claremont.taf.performance.TestRunner;
import se.claremont.taf.performance.threadscenario.ThreadScenario;
import se.claremont.taf.performance.gui.guiProfiles.*;
import se.claremont.taf.performance.threadscenariosteps.LoadTestFeature;
import se.claremont.taf.performance.threadscenariosteps.ThreadScenarioStep;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class MainGui {
    protected TafFrame window;
    protected static TestRunner testRunner;
    protected static LoadedUsableTestMethodsList loadedUsableTestMethodsList;
    public static Map<String, Class<? extends ThreadScenarioStep>> threadScenarioStepTypes; //ClassName, Class
    //private ThreadScenarioList.ThreadScenarioPanel loadScenarioPanel;

    public MainGui(TestRunner testRunner){
        this.testRunner = testRunner;
        threadScenarioStepTypes = new HashMap<>();
        loadedUsableTestMethodsList = new LoadedUsableTestMethodsList();
        scanSelJarForLoadTestFeatures();
        discoverThreadStepTypes();
        window = new TafFrame();
        window.setTitle("TAF - Load Testing Scenario Manager");
        window.getContentPane().setLayout(new BoxLayout(window.getContentPane(), BoxLayout.Y_AXIS));
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //loadScenarioPanel = TestRunner.getInstance().threadScenarioList.guiPanel();

        window.getContentPane().add(TestRunner.getInstance().getRunPanel(window));

        //window.getContentPane().add(loadScenarioPanel);

        TafPanel actionPanel = new TafPanel();
        actionPanel.setLayout(new FlowLayout());
        TafButton addLoadAgentButton = new TafButton("Manage Load Agents...");
        addLoadAgentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoadAgentsDialog dialog = new LoadAgentsDialog(window);
            }
        });
        actionPanel.add(addLoadAgentButton);
        TafButton addThreadScenarioButton = new TafButton("Add thread scenario...");
        addThreadScenarioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ThreadScenario scenario = new ThreadScenario("Thread Scenario " + (TestRunner.getInstance().getNextDisplayNameCounter()), TestRunner.getInstance().loadAgentList.getDefaultAgent());
                TestRunner.getInstance().loadAgentList.getDefaultAgent().addThreadScenario(scenario);
                TafDialog editor = scenario.steps.editDialog(window);
            }
        });
        actionPanel.add(addThreadScenarioButton);
        window.getContentPane().add(actionPanel);
        TafPanel buttonPanel = new TafPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        TafButton closeButton = new TafButton("Exit");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                window.dispose();
                TestRunner.getInstance().killRun();
                System.exit(0);
            }
        });

        TafButton killButton = new TafButton("Kill current run");
        TafButton runButton = new TafButton("Run scenario");
        killButton.setEnabled(false);
        killButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TestRunner.getInstance().killRun();
                runButton.setEnabled(true);
                killButton.setEnabled(false);
            }
        });
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runButton.setEnabled(false);
                killButton.setEnabled(true);
                TestRunner.getInstance().run();
            }
        });

        TafPanel persistationPanel = new TafPanel();
        persistationPanel.setLayout(new FlowLayout());
        TafButton loadScenarioButton = new TafButton("Load scenario...");
        loadScenarioButton.setEnabled(false);
        persistationPanel.add(loadScenarioButton);
        TafButton saveButton = new TafButton("Save scenario...");
        saveButton.setEnabled(false);
        persistationPanel.add(saveButton);
        buttonPanel.add(persistationPanel);

        TafPanel runPanel = new TafPanel();
        runPanel.add(killButton);
        runPanel.add(runButton);
        buttonPanel.add(runPanel);

        buttonPanel.add(closeButton);
        window.getContentPane().add(buttonPanel);

        window.pack();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        window.setLocation(dim.width/2-window.getSize().width/2, dim.height/2-window.getSize().height/2);
        window.setVisible(true);
    }

    private void scanSelJarForLoadTestFeatures(){
        Field f;
        try {
            f = ClassLoader.class.getDeclaredField("classes");
            f.setAccessible(true);
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Vector<Class> classes =  (Vector<Class>) f.get(classLoader);

            for(Class cls : classes){
                for(Method m : cls.getMethods()){
                    Annotation a = m.getAnnotation(LoadTestFeature.class);
                    if(a == null) continue;
                    loadedUsableTestMethodsList.add(new LoadResource(new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath()), cls, m));
                }
            }
        } catch (Exception e) {

            e.printStackTrace();
        }    }

    private void discoverThreadStepTypes(){
        Reflections reflections = new Reflections("se.claremont.taf.performance");
        Set<Class<? extends ThreadScenarioStep>> classes = reflections.getSubTypesOf(ThreadScenarioStep.class);
        //ServiceLoader<ThreadScenarioStep> loader = ServiceLoader.load(ThreadScenarioStep.class);
        for(Class<? extends ThreadScenarioStep> stepType : classes){
            threadScenarioStepTypes.put(stepType.getSimpleName(), stepType);
        }
    }
}

package se.claremont.taf.performance.gui;

import org.junit.Ignore;
import se.claremont.taf.performance.LoadResource;
import se.claremont.taf.performance.gui.guiProfiles.TafButton;
import se.claremont.taf.performance.gui.guiProfiles.TafLabel;
import se.claremont.taf.performance.gui.guiProfiles.TafPanel;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;

public class LoadedUsefulMethodsTable extends JPanel {

    public JList gridPanel;
    public DefaultListModel model;

    public LoadedUsefulMethodsTable(){
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        model = new DefaultListModel();
        gridPanel = new JList(model);
        gridPanel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.add(new TafLabel("Loaded methods"));
        update();
        JScrollPane scrollPane = new JScrollPane(gridPanel);
        this.add(scrollPane);
        MainGui.loadedUsableTestMethodsList.addActionListener(new TableActionLister());
    }

    public LoadResource getSelected(){
        return MainGui.loadedUsableTestMethodsList.getLoadResourceList().get(gridPanel.getSelectedIndex());
    }


    public void update(){
        //Border border = BorderFactory.createLineBorder(Color.black);
        model.removeAllElements();
        for (int i = 0; i < MainGui.loadedUsableTestMethodsList.getLoadResourceList().size(); i++){
            model.addElement(
                    MainGui.loadedUsableTestMethodsList.getLoadResourceList().get(i).clazz.getName() +
                    "." +
                    MainGui.loadedUsableTestMethodsList.getLoadResourceList().get(i).method.getName());
            /*
            TafPanel rowPanel = new TafPanel();
            rowPanel.setLayout(new GridLayout(1,2));
            Color backgroundColor = Color.white;
            Color foregroundColor = Color.black;
            String rowTooltipText = "";
            TafLabel className = new TafLabel(MainGui.loadedUsableTestMethodsList.getLoadResourceList().get(i).clazz.getName());
            className.setBorder(border);

            boolean testMethodIsIgnored = checkIfTestMethodIsIgnored(MainGui.loadedUsableTestMethodsList.getLoadResourceList().get(i));
            if(testMethodIsIgnored){
                rowTooltipText += "Test method is ignored. ";
                backgroundColor = Color.white;
                foregroundColor = Color.GRAY;
            }
            TafLabel methodName = new TafLabel(MainGui.loadedUsableTestMethodsList.getLoadResourceList().get(i).method.getName());
            methodName.setBorder(border);

            className.setToolTipText(rowTooltipText);
            className.setBackground(backgroundColor);
            className.setForeground(foregroundColor);
            rowPanel.add(className);

            methodName.setToolTipText(rowTooltipText);
            methodName.setBackground(backgroundColor);
            methodName.setForeground(foregroundColor);
            rowPanel.add(methodName);
            gridPanel.add(rowPanel);
            */
        }
    }

    private boolean checkIfTestMethodIsIgnored(LoadResource loadResource){
        return loadResource.method.getAnnotation(Ignore.class) != null;
    }

    public class TableActionLister extends LoadedResourceListener {
        @Override
        public void resourceAddedEvent(){
            update();
        }

    }



    /*
    private boolean checkIfHasBeforeMethod(LoadResource loadResource) {
        try{
            Method[] methods = loadResource.classObject.getMethods();
            for(Method m : methods){
                Annotation before = m.getAnnotation(Before.class);
                if(before == null) continue;
                return true;

            }

        }catch (Throwable t){
            return true;
        }
        return false;
    }
    */
}

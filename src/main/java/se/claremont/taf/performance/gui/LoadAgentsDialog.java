package se.claremont.taf.performance.gui;

import se.claremont.taf.performance.loadagent.LoadAgent;
import se.claremont.taf.performance.TestRunner;
import se.claremont.taf.performance.gui.guiProfiles.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class LoadAgentsDialog {

    TafDialog frame;
    JList registeredLoadAgentsPanel;
    DefaultListModel listModel;

    public LoadAgentsDialog(TafFrame window) {
        frame = new TafDialog(window, "TAF - Manage Load Agents");
        LoadAgentsDialog passon = this;
        TafButton removeLoadAgent = new TafButton("Remove");
        removeLoadAgent.setEnabled(false);
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        TafLabel loadAgentListLabel = new TafLabel("Registered Load Agents");
        frame.getContentPane().add(loadAgentListLabel);
        listModel = new DefaultListModel();
        registeredLoadAgentsPanel = new JList(listModel);
        registeredLoadAgentsPanel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                removeLoadAgent.setEnabled(!registeredLoadAgentsPanel.isSelectionEmpty());
            }
        });
        registeredLoadAgentsPanel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        updateList();
        frame.getContentPane().add(new JScrollPane(registeredLoadAgentsPanel));

        TafPanel buttonPanel = new TafPanel();
        TafButton addLoadAgent = new TafButton("Add...");
        addLoadAgent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoadAgentAdditionWindow loadAgentAdditionWindow = new LoadAgentAdditionWindow(passon);
                loadAgentAdditionWindow.dialog.addWindowListener(new WindowListener() {
                    @Override
                    public void windowOpened(WindowEvent e) {

                    }

                    @Override
                    public void windowClosing(WindowEvent e) {

                    }

                    @Override
                    public void windowClosed(WindowEvent e) {
                        updateList();
                    }

                    @Override
                    public void windowIconified(WindowEvent e) {

                    }

                    @Override
                    public void windowDeiconified(WindowEvent e) {

                    }

                    @Override
                    public void windowActivated(WindowEvent e) {

                    }

                    @Override
                    public void windowDeactivated(WindowEvent e) {

                    }
                });
            }
        });
        buttonPanel.add(addLoadAgent);
        removeLoadAgent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TestRunner.getInstance().loadAgentList.getAgents().remove(registeredLoadAgentsPanel.getSelectedIndex());
                updateList();
            }
        });
        buttonPanel.add(removeLoadAgent);
        frame.getContentPane().add(buttonPanel);

        TafButton okButton = new TafButton("Close");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });
        frame.getContentPane().add(okButton);

        frame.pack();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        window.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
        frame.setVisible(true);
    }

    public void updateList() {
        //registeredLoadAgentsPanel.removeAll();
        listModel.removeAllElements();
        for(LoadAgent loadAgent : TestRunner.getInstance().loadAgentList.getAgents()){
            listModel.addElement(loadAgent.toString());
        }
        frame.revalidate();
        frame.repaint();
    }
}
